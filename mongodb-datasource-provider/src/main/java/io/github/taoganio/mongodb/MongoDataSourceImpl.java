package io.github.taoganio.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDB 数据源实现类
 */
public class MongoDataSourceImpl implements MongoDataSource {

    private final String name;
    private final MongoClient mongoClient;
    private final String defaultDatabase;

    public MongoDataSourceImpl(String name, MongoClient mongoClient, String defaultDatabase) {
        if (name == null) {
            throw new IllegalArgumentException("name cannot be null");
        }
        if (mongoClient == null) {
            throw new IllegalArgumentException("mongoClient cannot be null");
        }
        if (defaultDatabase == null) {
            throw new IllegalArgumentException("defaultDatabase cannot be null");
        }
        this.name = name;
        this.mongoClient = mongoClient;
        this.defaultDatabase = defaultDatabase;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MongoClient getMongoClient() {
        return mongoClient;
    }

    @Override
    public MongoDatabase getDatabase(String databaseName) {
        if (databaseName == null) {
            throw new IllegalArgumentException("databaseName cannot be null");
        }
        return mongoClient.getDatabase(databaseName);
    }

    @Override
    public MongoDatabase getDatabase() {
        return mongoClient.getDatabase(defaultDatabase);
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}
