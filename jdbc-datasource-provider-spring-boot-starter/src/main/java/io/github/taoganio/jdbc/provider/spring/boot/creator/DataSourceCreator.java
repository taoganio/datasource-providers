package io.github.taoganio.jdbc.provider.spring.boot.creator;

import org.springframework.lang.Nullable;

import javax.sql.DataSource;

/**
 * 数据源创建者
 */
public interface DataSourceCreator {

    /**
     * 创建数据源
     *
     * @param definition 数据源
     * @throws Exception 如果创建失败，则抛出异常
     */
    @Nullable
    DataSource create(DataSourceDefinition definition) throws Exception;
}
