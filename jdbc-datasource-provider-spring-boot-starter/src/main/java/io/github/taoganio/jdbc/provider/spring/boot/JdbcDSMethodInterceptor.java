package io.github.taoganio.jdbc.provider.spring.boot;

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

public class JdbcDSMethodInterceptor implements MethodInterceptor, BeanFactoryAware {

    private final Map<MethodClassKey, JdbcDSContext> cache = new ConcurrentHashMap<>();
    private AnnotationFinder annotationFinder = AnnotationFinder.DEFAULT;
    private DataSourceExpressionEvaluator expressionEvaluator = new MethodDataSourceExpressionEvaluator();
    @Nullable
    private BeanFactory beanFactory;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> clazz = Objects.requireNonNull(invocation.getThis()).getClass();
        Method method = invocation.getMethod();
        JdbcDSContext context = cache.computeIfAbsent(new MethodClassKey(method, clazz), k -> {
            JdbcDS annotation = annotationFinder.findAnnotation(method, clazz, JdbcDS.class);
            Assert.notNull(annotation, "JdbcDS annotation must not be null");
            return new JdbcDSContext(annotation.routing(), annotation.key(), annotation.scope());
        });
        JdbcDSContextHolder.push(processContext(invocation, context));
        try {
            return invocation.proceed();
        } finally {
            JdbcDSContextHolder.poll();
        }
    }

    /**
     * 处理上下文
     */
    protected JdbcDSContext processContext(MethodInvocation invocation, JdbcDSContext context) {
        MethodInvocationContext mic
                = new MethodInvocationContext(invocation.getThis(), invocation.getMethod(), invocation.getArguments());
        EvaluationContext evaluationContext = expressionEvaluator
                .createEvaluationContext(mic.getMethod(), mic.getArgs(),
                        mic.getTarget(), mic.getTargetClass(), mic.getTargetMethod(), null, beanFactory);
        Object evaluateKey = expressionEvaluator.evaluateKey(mic.getMethodKey(), context.getKey(), evaluationContext);
        if (evaluateKey != null) {
            return new JdbcDSContext(context.isRouting(), evaluateKey.toString(), context.getReadWriteScope());
        }
        return context;
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
