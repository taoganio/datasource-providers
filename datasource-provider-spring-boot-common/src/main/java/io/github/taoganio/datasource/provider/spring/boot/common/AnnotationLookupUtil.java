package io.github.taoganio.datasource.provider.spring.boot.common;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 注解查找工具。
 * <p>
 * 支持：
 * 1. 当前方法（代理方法 -> 目标类实现方法）；
 * 2. 桥接方法；
 * 3. 当前类及父类；
 * 4. 接口及接口方法；
 * 5. Spring 合并注解（组合注解）。
 */
public abstract class AnnotationLookupUtil {

    /**
     * 查找方法或类层次上的注解（方法优先，类次之）。
     */
    public static <A extends Annotation> A findAnnotation(
            Method method, Class<?> targetClass, Class<A> annotationType) {

        if (method == null || annotationType == null) {
            return null;
        }

        Class<?> userClass = ClassUtils.getUserClass(targetClass);
        Method specificMethod = AopUtils.getMostSpecificMethod(method, userClass);

        // 方法上的注解（包括桥接方法）
        A ann = findMethodAnnotation(specificMethod, annotationType);
        if (ann != null) return ann;

        // 接口方法注解（包括默认方法和父接口）
        ann = findInterfaceMethodAnnotation(specificMethod, userClass, annotationType);
        if (ann != null) return ann;

        // 类及其父类注解
        return findClassHierarchyAnnotation(userClass, annotationType);
    }

    private static <A extends Annotation> A findMethodAnnotation(Method method, Class<A> annotationType) {
        A ann = AnnotatedElementUtils.findMergedAnnotation(method, annotationType);
        if (ann != null) return ann;

        // 桥接方法
        Method bridged = BridgeMethodResolver.findBridgedMethod(method);
        if (bridged != method) {
            ann = AnnotatedElementUtils.findMergedAnnotation(bridged, annotationType);
        }
        return ann;
    }

    private static <A extends Annotation> A findClassHierarchyAnnotation(Class<?> type, Class<A> annotationType) {
        Set<Class<?>> visited = new HashSet<>();
        Queue<Class<?>> queue = new LinkedList<>();
        queue.add(type);

        while (!queue.isEmpty()) {
            Class<?> current = queue.poll();
            if (current == null || current == Object.class || !visited.add(current)) continue;

            // 类上的注解
            A ann = AnnotatedElementUtils.findMergedAnnotation(current, annotationType);
            if (ann != null) return ann;
            // 父类入队
            queue.add(current.getSuperclass());
            // 接口入队
            queue.addAll(Arrays.asList(current.getInterfaces()));
        }
        return null;
    }

    private static <A extends Annotation> A findInterfaceMethodAnnotation(Method method, Class<?> clazz, Class<A> annotationType) {
        Set<Class<?>> visited = new HashSet<>();
        return findOnInterfacesRecursive(clazz, method, annotationType, visited);
    }

    private static <A extends Annotation> A findOnInterfacesRecursive(
            Class<?> type, Method method, Class<A> annotationType, Set<Class<?>> visited) {

        for (Class<?> iface : type.getInterfaces()) {
            if (!visited.add(iface)) continue;

            try {
                Method m = iface.getMethod(method.getName(), method.getParameterTypes());
                A ann = findMethodAnnotation(m, annotationType);
                if (ann != null) return ann;
            } catch (NoSuchMethodException ignored) {
            }

            A nested = findOnInterfacesRecursive(iface, method, annotationType, visited);
            if (nested != null) return nested;
        }

        Class<?> superClass = type.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            return findOnInterfacesRecursive(superClass, method, annotationType, visited);
        }

        return null;
    }
}
