package io.github.taoganio.mongodb.provider;

import io.github.taoganio.datasource.provider.MultipleDataSourceRoutingProvider;
import io.github.taoganio.mongodb.MongoDataSource;

public class MongoMultipleDataSourceRoutingProvider
        extends MultipleDataSourceRoutingProvider<MongoDataSource> implements MongoDataSourceRoutingProvider {

    public MongoMultipleDataSourceRoutingProvider(MongoDataSourceProvider dataSourceProvider) {
        super(dataSourceProvider);
    }
}
