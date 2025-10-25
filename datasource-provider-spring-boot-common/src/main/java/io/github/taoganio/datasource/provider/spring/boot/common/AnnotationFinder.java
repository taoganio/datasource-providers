package io.github.taoganio.datasource.provider.spring.boot.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 注释查找器
 */
public interface AnnotationFinder {

    AnnotationFinder DEFAULT = AnnotationLookupUtil::findAnnotation;

    <A extends Annotation> A findAnnotation(Method method, Class<?> targetClass, Class<A> annotationType);


}
