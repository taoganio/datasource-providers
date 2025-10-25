package io.github.taoganio.jdbc.provider.spring.boot.creator;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据源池
 */
public class DataSourcePool {

    /**
     * 类型
     */
    private Class<? extends DataSource> type;
    /**
     * 属性
     */
    private Properties properties;

    public Class<? extends DataSource> getType() {
        return type;
    }

    public void setType(Class<? extends DataSource> type) {
        this.type = type;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}