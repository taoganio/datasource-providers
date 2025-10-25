package io.github.taoganio.jdbc.provider;

import io.github.taoganio.datasource.provider.SimpleDataSourceProvider;

import javax.sql.DataSource;

public class JdbcSimpleDataSourceProvider
        extends SimpleDataSourceProvider<DataSource> implements JdbcDataSourceProvider {

    public JdbcSimpleDataSourceProvider(DataSource primaryDataSource) {
        super(primaryDataSource);
    }
}
