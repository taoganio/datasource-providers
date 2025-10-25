package io.github.taoganio.datasource.provider;

import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleDataSourceProvider<T> implements DataSourceProvider<T> {

    private final Map<String, T> dataSourceMapping = new ConcurrentHashMap<>();
    private final T primaryDataSource;

    public SimpleDataSourceProvider(T primaryDataSource) {
        this.primaryDataSource = primaryDataSource;
    }

    @Nullable
    @Override
    public T getDataSource(String key) {
        return dataSourceMapping.get(key);
    }

    @Override
    public List<T> getAllDataSources() {
        return new ArrayList<>(dataSourceMapping.values());
    }

    @Override
    public T getPrimaryDataSource() {
        return primaryDataSource;
    }

    public void addDataSourceMapping(String key, T dataSource) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource cannot be null");
        }
        dataSourceMapping.put(key, dataSource);
    }

    public Map<String, T> getDataSourceMapping() {
        return dataSourceMapping;
    }

}
