package io.github.taoganio.jdbc.provider;

import io.github.taoganio.datasource.provider.MultipleDataSourceRoutingProvider;

import javax.sql.DataSource;

public class JdbcMultipleDataSourceRoutingProvider
        extends MultipleDataSourceRoutingProvider<DataSource> implements JdbcDataSourceRoutingProvider {

    public JdbcMultipleDataSourceRoutingProvider(JdbcDataSourceProvider dataSourceProvider) {
        super(dataSourceProvider);
    }
}
