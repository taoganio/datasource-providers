package io.github.taoganio.datasource.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 读写范围路由器
 */
public class ReadWriteScopeRouterBean implements ReadWriteScopeRouter {

    private final String routeKey;
    private final Map<ReadWriteScope, String> dataSourceKeyMapping = new ConcurrentHashMap<>();

    public ReadWriteScopeRouterBean(String routeKey) {
        this.routeKey = routeKey;
    }

    @Override
    public String getRouteKey() {
        return routeKey;
    }

    @Override
    public String getDataSourceKey(ReadWriteScope scope) {
        if (scope == null) {
            return null;
        }
        return dataSourceKeyMapping.get(scope);
    }

    public void addDataSourceKeyScope(ReadWriteScope scope, String dataSourceKey) {
        if (scope == null) {
            throw new IllegalArgumentException("scope cannot be null");
        }
        if (dataSourceKey == null) {
            throw new IllegalArgumentException("dataSourceKey cannot be null");
        }
        dataSourceKeyMapping.put(scope, dataSourceKey);
    }

    public void setDataSourceKeyMapping(Map<ReadWriteScope, String> dataSourceKeyMapping) {
        if (dataSourceKeyMapping == null) {
            throw new IllegalArgumentException("dataSourceKeyMapping cannot be null");
        }
        this.dataSourceKeyMapping.putAll(dataSourceKeyMapping);
    }
}
