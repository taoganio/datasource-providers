package io.github.taoganio.mongodb.provider;


import io.github.taoganio.datasource.provider.ReadWriteScope;
import io.github.taoganio.datasource.provider.ReadWriteScopeRouter;
import io.github.taoganio.mongodb.*;
import com.mongodb.client.MongoClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class MongoDataSourceProviderTest {

    private DataSourceConfig config;
    private MongoDataSourceProvider dataSourceProvider;

    @BeforeEach
    void setUp() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("datasource.yml");
        config = DataSourceConfigLoader.load(resourceAsStream);

        String primary = config.getPrimary();
        Map<String, MongoProperties> definitions = config.getDefinitions();
        MongoProperties primaryProperties = definitions.get(primary);
        MongoClient mongoClient = new MongoClientFactory(primaryProperties).create();
        MongoSimpleDataSourceProvider provider
                = new MongoSimpleDataSourceProvider(new MongoDataSourceImpl(primary, mongoClient, primaryProperties.getDatabase()));
        for (Map.Entry<String, MongoProperties> entry : definitions.entrySet()) {
            MongoProperties properties = entry.getValue();
            mongoClient = new MongoClientFactory(properties).create();
            provider.addDataSourceMapping(entry.getKey(), new MongoDataSourceImpl(entry.getKey(), mongoClient, properties.getDatabase()));
        }

        this.dataSourceProvider = provider;
    }

    @Test
    void simpleProviderTest() {
        MongoDataSource slave1 = dataSourceProvider.getDataSource("slave1");
        Assertions.assertNotNull(slave1.getDatabase());
    }

    @Test
    void routerDataSourceProviderTest() {
        MongoMultipleDataSourceRoutingProvider
                provider = new MongoMultipleDataSourceRoutingProvider(dataSourceProvider);

        Map<String, List<String>> datasourceRouter = config.getRouters().getDatasourceRouter();
        for (Map.Entry<String, List<String>> entry : datasourceRouter.entrySet()) {
            provider.addRouterMapping(entry.getKey(), entry.getValue());
        }

        MongoDataSource term = provider.getDataSource("term");
        Assertions.assertNotNull(term);

        MongoDataSource mailfriend = provider.getDataSource("mailfriend");
        Assertions.assertNotNull(mailfriend);

        List<MongoDataSource> mailfriends = provider.getDataSources("mailfriend");
        System.out.println(mailfriends.size());
    }

    @Test
    void readWriteDataSourceProviderTest() {
        MongoReadWriteDataSourceRoutingProvider provider =
                new MongoReadWriteDataSourceRoutingProvider(dataSourceProvider);
        List<ReadWriteScopeRouter> readwriteRouter = config.getRouters().getReadwriteRouter();
        readwriteRouter.forEach(provider::addRouter);

        MongoDataSource mailfriend = provider.getDataSource("mailfriend", ReadWriteScope.WRITE);
        Assertions.assertNotNull(mailfriend);

    }

}
