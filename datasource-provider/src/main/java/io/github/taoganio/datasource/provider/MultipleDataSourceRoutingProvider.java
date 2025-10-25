package io.github.taoganio.datasource.provider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MultipleDataSourceRoutingProvider<T> implements DataSourceRoutingProvider<T> {

    private final Map<String, Object> dataSourceRouterMapping = new ConcurrentHashMap<>();
    private final DataSourceProvider<T> dataSourceProvider;

    public MultipleDataSourceRoutingProvider(DataSourceProvider<T> dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @Override
    public List<T> getDataSources(String routeKey) {
        Object keys = dataSourceRouterMapping.get(routeKey);
        if (keys == null) {
            return Collections.emptyList();
        }
        if (keys instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<String> dataSourceKeys = (Collection<String>) keys;
            List<T> dataSources = new ArrayList<>(dataSourceKeys.size());
            for (String dataSourceKey : dataSourceKeys) {
                T dataSource = dataSourceProvider.getDataSource(dataSourceKey);
                if (dataSource != null) {
                    dataSources.add(dataSource);
                }
            }
            return dataSources;
        } else if (keys instanceof String) {
            T dataSource = dataSourceProvider.getDataSource((String) keys);
            if (dataSource != null) {
                return Collections.singletonList(dataSource);
            }
        }
        return Collections.emptyList();
    }

    public void addRouterMapping(String routerKey, String dataSourceKey) {
        if (routerKey == null) {
            throw new IllegalArgumentException("routerKey cannot be null");
        }
        if (dataSourceKey == null) {
            throw new IllegalArgumentException("dataSourceKey cannot be null");
        }
        dataSourceRouterMapping.put(routerKey, dataSourceKey);
    }

    public void addRouterMapping(String routerKey, Collection<String> dataSourceKeys) {
        if (routerKey == null) {
            throw new IllegalArgumentException("routerKey cannot be null");
        }
        if (dataSourceKeys == null) {
            throw new IllegalArgumentException("dataSourceKeys cannot be null");
        }
        dataSourceRouterMapping.put(routerKey, new ArrayList<>(dataSourceKeys));
    }
}
