package io.github.taoganio.datasource.provider;


import jakarta.annotation.Nullable;

/**
 * 读写范围路由器
 */
public interface ReadWriteScopeRouter {

    /**
     * 得到路由键
     */
    String getRouteKey();

    /**
     * 获取数据源键
     *
     * @param scope 范围
     * @return 数据源键
     */
    @Nullable
    String getDataSourceKey(ReadWriteScope scope);

}
