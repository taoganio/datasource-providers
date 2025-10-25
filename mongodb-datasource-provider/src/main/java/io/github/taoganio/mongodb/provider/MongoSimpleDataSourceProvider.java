package io.github.taoganio.mongodb.provider;

import io.github.taoganio.datasource.provider.SimpleDataSourceProvider;
import io.github.taoganio.mongodb.MongoDataSource;

public class MongoSimpleDataSourceProvider
        extends SimpleDataSourceProvider<MongoDataSource> implements MongoDataSourceProvider {

    public MongoSimpleDataSourceProvider(MongoDataSource primaryDataSource) {
        super(primaryDataSource);
    }

}
