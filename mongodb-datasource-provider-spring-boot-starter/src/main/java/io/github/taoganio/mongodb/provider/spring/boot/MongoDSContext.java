package io.github.taoganio.mongodb.provider.spring.boot;

import io.github.taoganio.datasource.provider.ReadWriteScope;

/**
 * mongo 数据源上下文
 */
public class MongoDSContext {
    /**
     * 按路由获取数据源
     */
    private final boolean routing;
    /**
     * 查询数据源的标识
     */
    private final String key;
    /**
     * 查询的数据库
     */
    private final String database;
    /**
     * 读写范围
     */
    private final ReadWriteScope readWriteScope;

    public MongoDSContext(String key, String database) {
        this(true, key, database, ReadWriteScope.UNKNOWN);
    }

    public MongoDSContext(boolean routing, String key, String database) {
        this(routing, key, database, ReadWriteScope.UNKNOWN);
    }

    public MongoDSContext(boolean routing, String key, String database, ReadWriteScope readWriteScope) {
        this.key = key;
        this.database = database;
        this.readWriteScope = readWriteScope;
        this.routing = routing;
    }

    public boolean isRouting() {
        return routing;
    }

    public String getKey() {
        return key;
    }

    public String getDatabase() {
        return database;
    }

    public ReadWriteScope getReadWriteScope() {
        return readWriteScope;
    }

    @Override
    public String toString() {
        return "routing=" + routing +
                        ", key='" + key + '\'' +
                        ", database='" + database + '\'' +
                        ", scope=" + readWriteScope;
    }
}
