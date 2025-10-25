package io.github.taoganio.mongodb.provider;

import io.github.taoganio.datasource.provider.ReadWriteDataSourceRoutingProvider;
import io.github.taoganio.mongodb.MongoDataSource;

public class MongoReadWriteDataSourceRoutingProvider
        extends ReadWriteDataSourceRoutingProvider<MongoDataSource> implements MongoReadWriteDataSourceProvider {

    public MongoReadWriteDataSourceRoutingProvider(MongoDataSourceProvider dataSourceProvider) {
        super(dataSourceProvider);
    }
}
