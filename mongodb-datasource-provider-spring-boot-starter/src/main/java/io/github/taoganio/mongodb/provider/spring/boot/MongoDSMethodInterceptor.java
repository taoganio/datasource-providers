package io.github.taoganio.mongodb.provider.spring.boot;

import io.github.taoganio.datasource.provider.spring.boot.common.AnnotationFinder;
import io.github.taoganio.datasource.provider.spring.boot.common.DataSourceExpressionEvaluator;
import io.github.taoganio.datasource.provider.spring.boot.common.MethodDataSourceExpressionEvaluator;
import io.github.taoganio.datasource.provider.spring.boot.common.MethodInvocationContext;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.MethodClassKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MongoDSMethodInterceptor implements MethodInterceptor, BeanFactoryAware {

    private final Map<MethodClassKey, MongoDSContext> cache = new ConcurrentHashMap<>();
    private AnnotationFinder annotationFinder = AnnotationFinder.DEFAULT;
    private DataSourceExpressionEvaluator expressionEvaluator = new MethodDataSourceExpressionEvaluator();
    @Nullable
    private BeanFactory beanFactory;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> clazz = Objects.requireNonNull(invocation.getThis()).getClass();
        Method method = invocation.getMethod();
        MongoDSContext mongoDSContext = cache.computeIfAbsent(new MethodClassKey(method, clazz), k -> {
            MongoDS annotation = annotationFinder.findAnnotation(method, clazz, MongoDS.class);
            Assert.notNull(annotation, "MongoDS annotation must not be null");
            return new MongoDSContext(annotation.routing(), annotation.key(), annotation.database(), annotation.scope());
        });
        MongoDSContextHolder.push(processContext(invocation, mongoDSContext));
        try {
            return invocation.proceed();
        } finally {
            MongoDSContextHolder.poll();
        }
    }

    /**
     * 处理上下文
     */
    protected MongoDSContext processContext(MethodInvocation invocation, MongoDSContext context) {
        MethodInvocationContext mic
                = new MethodInvocationContext(invocation.getThis(), invocation.getMethod(), invocation.getArguments());
        EvaluationContext evaluationContext = expressionEvaluator
                .createEvaluationContext(mic.getMethod(), mic.getArgs(),
                        mic.getTarget(), mic.getTargetClass(), mic.getTargetMethod(), null, beanFactory);
        Object evaluateSource = expressionEvaluator.evaluateKey(mic.getMethodKey(), context.getKey(), evaluationContext);
        Object evaluateDatabase = expressionEvaluator.evaluateKey(mic.getMethodKey(), context.getDatabase(), evaluationContext);

        boolean evaluable = evaluateSource != null || evaluateDatabase != null;
        String key = evaluateSource != null ? evaluateSource.toString() : context.getKey();
        String database = evaluateDatabase != null ? evaluateDatabase.toString() : context.getDatabase();

        return evaluable ? new MongoDSContext(context.isRouting(), key, database, context.getReadWriteScope()) : context;
    }

    public void setAnnotationFinder(AnnotationFinder annotationFinder) {
        Assert.notNull(annotationFinder, "annotationFinder must not be null");
        this.annotationFinder = annotationFinder;
    }

    public void setExpressionEvaluator(DataSourceExpressionEvaluator expressionEvaluator) {
        Assert.notNull(expressionEvaluator, "expressionEvaluator must not be null");
        this.expressionEvaluator = expressionEvaluator;
    }

    @Override
    public void setBeanFactory(@Nullable BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
