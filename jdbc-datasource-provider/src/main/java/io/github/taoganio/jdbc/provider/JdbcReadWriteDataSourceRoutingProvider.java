package io.github.taoganio.jdbc.provider;

import io.github.taoganio.datasource.provider.ReadWriteDataSourceRoutingProvider;

import javax.sql.DataSource;

public class JdbcReadWriteDataSourceRoutingProvider
        extends ReadWriteDataSourceRoutingProvider<DataSource> implements JdbcReadWriteDataSourceProvider {

    public JdbcReadWriteDataSourceRoutingProvider(JdbcDataSourceProvider dataSourceProvider) {
        super(dataSourceProvider);
    }
}
