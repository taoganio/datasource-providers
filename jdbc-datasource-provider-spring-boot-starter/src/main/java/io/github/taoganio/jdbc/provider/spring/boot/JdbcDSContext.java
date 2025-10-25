package io.github.taoganio.jdbc.provider.spring.boot;

import io.github.taoganio.datasource.provider.ReadWriteScope;

/**
 * JDBC 数据源上下文
 */
public class JdbcDSContext {
    /**
     * 按路由获取数据源
     */
    private final boolean routing;
    /**
     * 查询数据源的标识
     */
    private final String key;
    /**
     * 读写范围
     */
    private final ReadWriteScope readWriteScope;

    public JdbcDSContext(String key) {
        this(true, key, ReadWriteScope.UNKNOWN);
    }

    public JdbcDSContext(boolean routing, String key) {
        this(routing, key, ReadWriteScope.UNKNOWN);
    }

    public JdbcDSContext(boolean routing, String key, ReadWriteScope readWriteScope) {
        this.key = key;
        this.readWriteScope = readWriteScope;
        this.routing = routing;
    }

    public boolean isRouting() {
        return routing;
    }

    public String getKey() {
        return key;
    }

    public ReadWriteScope getReadWriteScope() {
        return readWriteScope;
    }

    @Override
    public String toString() {
        return "routing=" + routing +
                        ", key='" + key + '\'' +
                        ", scope=" + readWriteScope;
    }
}
