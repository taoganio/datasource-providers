package io.github.taoganio.datasource.provider;

import jakarta.annotation.Nullable;

import java.util.List;

/**
 * 数据源路由提供程序
 */
public interface DataSourceRoutingProvider<T> {

    /**
     * 获取第一个数据源
     *
     * @param routeKey 路由键
     * @return 数据源
     */
    @Nullable
    default T getDataSource(String routeKey) {
        List<T> sources = getDataSources(routeKey);
        return sources == null || sources.isEmpty() ? null : sources.get(0);
    }

    /**
     * 获取多个数据源
     *
     * @param routeKey 路由键
     * @return 数据源列表
     */
    List<T> getDataSources(String routeKey);

}
