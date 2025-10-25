package io.github.taoganio.datasource.provider;

import jakarta.annotation.Nullable;

import java.util.List;

/**
 * 数据源提供程序
 */
public interface DataSourceProvider<T> {

    /**
     * 获取主要数据源
     *
     * @return 数据源
     */
    T getPrimaryDataSource();

    /**
     * 获取数据源
     *
     * @param dataSourceKey 数据源标识
     * @return 数据源
     */
    @Nullable
    T getDataSource(String dataSourceKey);

    /**
     * 获取所有数据源
     *
     * @return 数据源列表
     */
    List<T> getAllDataSources();
}
