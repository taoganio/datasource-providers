package io.github.taoganio.mongodb.provider;

import io.github.taoganio.datasource.provider.DataSourceProvider;
import com.mongodb.client.MongoDatabase;
import io.github.taoganio.mongodb.MongoDataSource;
import jakarta.annotation.Nullable;

import java.util.Optional;

/**
 * MongoDB 数据源提供程序
 */
public interface MongoDataSourceProvider extends DataSourceProvider<MongoDataSource>{

    /**
     * 获取数据库
     *
     * @param key          数据源标识
     * @param databaseName 数据库名称
     * @return mongo 数据库
     */
    @Nullable
    default MongoDatabase getDatabase(String key, String databaseName) {
        return Optional.ofNullable(this.getDataSource(key))
                .map(e -> e.getMongoClient().getDatabase(databaseName))
                .orElse(null);
    }
}
