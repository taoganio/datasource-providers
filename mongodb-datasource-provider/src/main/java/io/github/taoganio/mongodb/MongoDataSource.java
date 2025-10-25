package io.github.taoganio.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.io.Closeable;

/**
 * MongoDB 数据源
 */
public interface MongoDataSource extends Closeable {

    String getName();

    MongoClient getMongoClient();

    MongoDatabase getDatabase();

    MongoDatabase getDatabase(String databaseName);

}
