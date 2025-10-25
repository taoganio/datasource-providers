package io.github.taoganio.mongodb.provider.spring.boot;

import io.github.taoganio.datasource.provider.ReadWriteScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在类或方法指定要切换的数据源
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoDS {

    /**
     * 启用后，按路由获取数据源
     */
    boolean routing() default false;

    /**
     * 获取数据源标识
     *
     * @return 字符串
     */
    String key() default "";

    /**
     * 数据库
     */
    String database() default "";

    /**
     * 读写范围
     */
    ReadWriteScope scope() default ReadWriteScope.UNKNOWN;
}
