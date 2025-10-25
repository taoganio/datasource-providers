package io.github.taoganio.jdbc.provider.spring.boot.autoconfigure;

import io.github.taoganio.jdbc.provider.spring.boot.JdbcDS;
import io.github.taoganio.jdbc.provider.spring.boot.creator.DataSourceDefinition;
import io.github.taoganio.jdbc.provider.spring.boot.creator.DataSourcePool;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

@ConfigurationProperties(prefix = "spring.datasource.jdbc")
public class JdbcDataSourceProperties {
    /**
     * 启动{@link JdbcDS} AOP切面
     */
    private boolean dsAop;
    /**
     * 为false时找不到对应的数据源定义时使用默认数据源, 否则抛出异常
     */
    private boolean strict;
    /**
     * 主数据源标识, 未定义时使用第一个
     */
    private String primary;
    /**
     * 自定义默认连接池类配置, 优先级高于 hikari 默认连接池配置
     */
    @NestedConfigurationProperty
    private DataSourcePool pool;
    /**
     * hikari 默认连接池配置
     */
    @NestedConfigurationProperty
    private HikariDataSource hikari;
    /**
     * 所有数据源定义
     */
    private Map<String, DataSourceDefinition> definitions;

    public boolean isDsAop() {
        return dsAop;
    }

    public void setDsAop(boolean dsAop) {
        this.dsAop = dsAop;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public DataSourcePool getPool() {
        return pool;
    }

    public void setPool(DataSourcePool pool) {
        this.pool = pool;
    }

    public HikariDataSource getHikari() {
        return hikari;
    }

    public void setHikari(HikariDataSource hikari) {
        this.hikari = hikari;
    }

    public Map<String, DataSourceDefinition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, DataSourceDefinition> definitions) {
        this.definitions = definitions;
    }

}
