package io.github.taoganio.jdbc.provider.spring.boot;

import io.github.taoganio.datasource.provider.ReadWriteScope;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在类或方法上指定要切换的数据源
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JdbcDS {
    /**
     * 获取数据源标识, 支持 Spring EL 表达式
     *
     * @return 字符串
     */
    @AliasFor("key")
    String value() default "";

    /**
     * 启用后，按路由获取数据源
     */
    boolean routing() default false;

    @AliasFor("value")
    String key() default "";

    /**
     * 读写范围
     */
    ReadWriteScope scope() default ReadWriteScope.UNKNOWN;
}
