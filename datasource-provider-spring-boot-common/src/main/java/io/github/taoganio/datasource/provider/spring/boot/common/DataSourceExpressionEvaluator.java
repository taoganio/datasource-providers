package io.github.taoganio.datasource.provider.spring.boot.common;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

/**
 * 数据源表达式求值器
 */
public interface DataSourceExpressionEvaluator {

    EvaluationContext createEvaluationContext(Method method, Object[] args, Object target,
                                              Class<?> targetClass, Method targetMethod,
                                              @Nullable Object result, @Nullable BeanFactory beanFactory);

    @Nullable
    Object evaluateKey(AnnotatedElementKey elementKey, String keyExpression, EvaluationContext evaluationContext);

}
