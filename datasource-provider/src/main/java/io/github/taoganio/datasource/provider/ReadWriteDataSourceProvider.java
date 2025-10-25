package io.github.taoganio.datasource.provider;

import jakarta.annotation.Nullable;

/**
 * 数据源读写范围提供程序
 */
public interface ReadWriteDataSourceProvider<T> {

    /**
     * 获取数据源
     *
     * @param key   数据源映射标识
     * @param scope 范围
     * @return 数据源
     */
    @Nullable
    T getDataSource(String key, ReadWriteScope scope);

}
