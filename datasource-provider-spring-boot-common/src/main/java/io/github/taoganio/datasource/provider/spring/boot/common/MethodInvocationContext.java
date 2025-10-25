package io.github.taoganio.datasource.provider.spring.boot.common;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.AnnotatedElementKey;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MethodInvocationContext {

    private final Method method;

    private final Class<?> targetClass;

    private final Method targetMethod;

    private final AnnotatedElementKey methodKey;

    private final Object[] args;

    private final Object target;

    public MethodInvocationContext(Object target, Method method, Object[] args) {
        this(target, AopProxyUtils.ultimateTargetClass(target), method, args);
    }

    public MethodInvocationContext(Object target, Class<?> targetClass, Method method, Object[] args) {
        this.method = method;
        this.targetClass = targetClass;
        this.targetMethod = (!Proxy.isProxyClass(targetClass) ?
                AopUtils.getMostSpecificMethod(method, targetClass) : this.method);
        this.methodKey = new AnnotatedElementKey(this.targetMethod, targetClass);
        this.args = args;
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public AnnotatedElementKey getMethodKey() {
        return methodKey;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object getTarget() {
        return target;
    }
}
