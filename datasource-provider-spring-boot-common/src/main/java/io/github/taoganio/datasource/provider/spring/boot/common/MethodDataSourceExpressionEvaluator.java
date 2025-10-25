package io.github.taoganio.datasource.provider.spring.boot.common;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MethodDataSourceExpressionEvaluator
        extends CachedExpressionEvaluator implements DataSourceExpressionEvaluator {

    private final Map<ExpressionKey, Expression> keyCache = new ConcurrentHashMap<>(64);

    @Override
    public EvaluationContext createEvaluationContext(Method method, Object[] args, Object target,
                                                     Class<?> targetClass, Method targetMethod,
                                                     @Nullable Object result, @Nullable BeanFactory beanFactory) {
        ExpressionRootObject rootObject = new ExpressionRootObject(method, args, target, targetClass);
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(
                rootObject, targetMethod, args, getParameterNameDiscoverer());
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    @Override
    public Object evaluateKey(AnnotatedElementKey elementKey, String keyExpression, EvaluationContext evalContext) {
        return getExpression(this.keyCache, elementKey, keyExpression).getValue(evalContext);
    }

}
