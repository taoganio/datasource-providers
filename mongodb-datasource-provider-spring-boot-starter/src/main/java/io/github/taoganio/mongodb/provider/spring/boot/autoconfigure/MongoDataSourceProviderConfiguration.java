package io.github.taoganio.mongodb.provider.spring.boot.autoconfigure;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import io.github.taoganio.mongodb.MongoDataSourceImpl;
import io.github.taoganio.mongodb.provider.MongoDataSourceProvider;
import io.github.taoganio.mongodb.provider.MongoSimpleDataSourceProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AutoConfiguration
@EnableConfigurationProperties(MongoDataSourceProperties.class)
public class MongoDataSourceProviderConfiguration {

    @Bean
    @ConditionalOnMissingBean(MongoDataSourceProvider.class)
    public MongoDataSourceProvider mongoDataSourceProvider(MongoDataSourceProperties properties,
                                                           @Nullable ObjectProvider<MongoClientSettingsBuilderCustomizer> customizerProvider,
                                                           Environment environment) {

        Map<String, MongoProperties> definitions = properties.getDefinitions();
        Assert.notEmpty(definitions, "No mongo data source definitions found");

        String primary = properties.getPrimary();

        MongoProperties primaryProperties = StringUtils.hasLength(primary) ? definitions.get(primary) : null;
        if (primaryProperties == null) {
            Map.Entry<String, MongoProperties> next = definitions.entrySet().iterator().next();
            primary = next.getKey();
            primaryProperties = next.getValue();
        }

        MongoClientSettings settings = MongoClientSettings.builder().build();
        MongoClient primaryMongoClient = createMongoClient(customizerProvider, environment, primaryProperties, settings);

        MongoDataSourceImpl primaryDataSource = new MongoDataSourceImpl(primary, primaryMongoClient, primaryProperties.getMongoClientDatabase());
        MongoSimpleDataSourceProvider provider = new MongoSimpleDataSourceProvider(primaryDataSource);

        for (Map.Entry<String, MongoProperties> entry : definitions.entrySet()) {
            if (entry.getKey().equals(primary)) {
                provider.addDataSourceMapping(entry.getKey(), primaryDataSource);
                continue;
            }
            MongoProperties property = entry.getValue();
            MongoClient mongoClient = createMongoClient(customizerProvider, environment, property, settings);
            provider.addDataSourceMapping(entry.getKey(),
                    (new MongoDataSourceImpl(entry.getKey(), mongoClient, property.getMongoClientDatabase())));
        }
        return provider;
    }

    private MongoClient createMongoClient(ObjectProvider<MongoClientSettingsBuilderCustomizer> customizerProvider,
                                          Environment environment, MongoProperties properties, MongoClientSettings settings) {
        List<MongoClientSettingsBuilderCustomizer> builderCustomizers = null;
        if (customizerProvider != null) {
            builderCustomizers = customizerProvider.stream().collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(builderCustomizers)) {
            builderCustomizers = Collections.singletonList
                    (new MongoPropertiesClientSettingsBuilderCustomizer(properties, environment));
        }
        return new MongoClientFactory(builderCustomizers).createMongoClient(settings);
    }

}
