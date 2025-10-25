package io.github.taoganio.datasource.provider;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReadWriteDataSourceRoutingProvider<T> implements ReadWriteDataSourceProvider<T> {

    private final DataSourceProvider<T> dataSourceProvider;
    private final Map<String, ReadWriteScopeRouter> readWriteRouterMapping = new ConcurrentHashMap<>();

    public ReadWriteDataSourceRoutingProvider(DataSourceProvider<T> dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @Override
    public T getDataSource(String routeKey, ReadWriteScope scope) {
        String dataSourceKey = Optional.ofNullable(readWriteRouterMapping.get(routeKey))
                .map(e -> e.getDataSourceKey(scope))
                .orElse(null);
        if (dataSourceKey != null) {
            return dataSourceProvider.getDataSource(dataSourceKey);
        }
        return null;
    }

    public void addRouter(ReadWriteScopeRouter readWriteRouter) {
        if (readWriteRouter == null) {
            throw new IllegalArgumentException("readWriteRouter cannot be null");
        }
        readWriteRouterMapping.put(readWriteRouter.getRouteKey(), readWriteRouter);
    }
}
