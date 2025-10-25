package io.github.taoganio.mongodb.provider;


import io.github.taoganio.datasource.provider.ReadWriteDataSourceProvider;
import io.github.taoganio.datasource.provider.ReadWriteScope;
import com.mongodb.client.MongoDatabase;
import io.github.taoganio.mongodb.MongoDataSource;
import jakarta.annotation.Nullable;

import java.util.Optional;

/**
 * MongoDB 读写数据源提供程序
 */
public interface MongoReadWriteDataSourceProvider extends ReadWriteDataSourceProvider<MongoDataSource> {

    /**
     * 获取数据库
     *
     * @param key            数据源标识
     * @param databaseName   数据库名称
     * @param readWriteScope 读写范围
     * @return mongo 数据库
     */
    @Nullable
    default MongoDatabase getDatabase(String key, ReadWriteScope readWriteScope, String databaseName) {
        return Optional.ofNullable(getDataSource(key, readWriteScope))
                .map(e -> e.getMongoClient().getDatabase(databaseName))
                .orElse(null);
    }

}
