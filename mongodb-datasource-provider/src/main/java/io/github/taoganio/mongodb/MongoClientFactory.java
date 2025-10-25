package io.github.taoganio.mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class MongoClientFactory {

    private final MongoProperties properties;

    public MongoClientFactory(MongoProperties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("MongodbProperties must not be null");
        }
        this.properties = properties;
    }

    /**
     * 创建mongo客户端
     *
     * @return {@link MongoClient}
     */
    public MongoClient create() {
        MongoClientSettings.Builder settingsBuilder = MongoClientSettings.builder();
        validateConfiguration();
        applyUuidRepresentation(settingsBuilder);
        applyHostAndPort(settingsBuilder);
        applyCredentials(settingsBuilder);
        applyTimeouts(settingsBuilder);
        return MongoClients.create(settingsBuilder.build(), driverInformation());
    }


    private void validateConfiguration() {
        if (hasCustomAddress() || hasCustomCredentials() || hasReplicaSet()) {
            if (this.properties.getUri() != null) {
                throw new IllegalArgumentException
                        ("Invalid mongo configuration, either uri or host/port/credentials/replicaSet must be specified");
            }
        }
    }

    private void applyUuidRepresentation(MongoClientSettings.Builder settingsBuilder) {
        settingsBuilder.uuidRepresentation(this.properties.getUuidRepresentation());
    }

    private void applyHostAndPort(MongoClientSettings.Builder settings) {
        if (hasCustomAddress()) {
            String host = getOrDefault(this.properties.getHost(), "localhost");
            int port = getOrDefault(this.properties.getPort(), MongoProperties.DEFAULT_PORT);
            ServerAddress serverAddress = new ServerAddress(host, port);
            settings.applyToClusterSettings(cluster -> cluster.hosts(Collections.singletonList(serverAddress)));
            return;
        }

        settings.applyConnectionString(new ConnectionString(this.properties.determineUri()));
    }

    private void applyCredentials(MongoClientSettings.Builder builder) {
        if (hasCustomCredentials()) {
            String database = (this.properties.getAuthenticationDatabase() != null)
                    ? this.properties.getAuthenticationDatabase() : this.properties.getMongoClientDatabase();
            builder.credential((MongoCredential.createCredential(this.properties.getUsername(), database,
                    this.properties.getPassword())));
        }
    }

    private void applyTimeouts(MongoClientSettings.Builder settingsBuilder) {
        settingsBuilder.applyToClusterSettings(e ->
                e.serverSelectionTimeout(properties.getServerSelectionTimeout(), TimeUnit.MILLISECONDS));
    }


    private boolean hasCustomCredentials() {
        return this.properties.getUsername() != null && this.properties.getPassword() != null;
    }

    private boolean hasCustomAddress() {
        return this.properties.getHost() != null || this.properties.getPort() != null;
    }

    private boolean hasReplicaSet() {
        return this.properties.getReplicaSetName() != null;
    }

    private MongoDriverInformation driverInformation() {
        return MongoDriverInformation.builder(MongoDriverInformation.builder().build())
                .driverName("dynamic-mongodb")
                .build();
    }

    private <V> V getOrDefault(V value, V defaultValue) {
        return (value != null) ? value : defaultValue;
    }
}
