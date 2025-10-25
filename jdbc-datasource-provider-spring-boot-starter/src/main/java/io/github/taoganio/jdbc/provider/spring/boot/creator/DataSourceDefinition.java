package io.github.taoganio.jdbc.provider.spring.boot.creator;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Properties;

/**
 * 数据源定义
 */
public class DataSourceDefinition extends DataSourceProperties {
    /**
     * hikari 默认连接池配置
     */
    @NestedConfigurationProperty
    private HikariDataSource hikari;
    /**
     * 自定义连接池类型配置, 优先级高于 hikari 默认连接池配置
     */
    @NestedConfigurationProperty
    private Pool pool;

    public HikariDataSource getHikari() {
        return hikari;
    }

    public void setHikari(HikariDataSource hikari) {
        this.hikari = hikari;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public static class Pool {
        /**
         * 属性
         */
        private Properties properties;

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }
    }

}
