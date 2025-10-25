package io.github.taoganio.jdbc.provider;

import io.github.taoganio.datasource.provider.ReadWriteDataSourceProvider;

import javax.sql.DataSource;

/**
 * JDBC 数据源读写提供程序
 */
public interface JdbcReadWriteDataSourceProvider extends ReadWriteDataSourceProvider<DataSource> {

}
