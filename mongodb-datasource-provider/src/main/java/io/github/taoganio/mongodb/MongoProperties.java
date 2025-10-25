package io.github.taoganio.mongodb;

import com.mongodb.ConnectionString;
import org.bson.UuidRepresentation;

public class MongoProperties {
    /**
     * 当配置的URI为空时使用的默认URI
     */
    public static final String DEFAULT_URI = "mongodb://localhost/test";

    /**
     * 当配置的端口为空时使用的默认端口
     */
    public static final int DEFAULT_PORT = 27017;

    /**
     * 在将UUID转换为BSON二进制值时使用的表示
     */
    private UuidRepresentation uuidRepresentation = UuidRepresentation.JAVA_LEGACY;

    /**
     * Mongo数据库URI。不能与主机、端口、凭据和副本集名称一起设置
     */
    private String uri;

    /**
     * Mongo服务器主机。不能用URI设置
     */
    private String host;

    /**
     * Mongo服务器端口。不能用URI设置
     */
    private Integer port = null;

    /**
     * 数据库名称
     */
    private String database;

    /**
     * 认证数据库名称
     */
    private String authenticationDatabase;

    /**
     * mongo服务器的登录用户。不能用URI设置
     */
    private String username;

    /**
     * mongo服务器登录密码。不能用URI设置
     */
    private char[] password;
    /**
     * 集群所需的副本集名称。不能用URI设置
     */
    private String replicaSetName;
    /**
     * 服务器选择超时(毫秒)
     */
    private long serverSelectionTimeout;

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getAuthenticationDatabase() {
        return this.authenticationDatabase;
    }

    public void setAuthenticationDatabase(String authenticationDatabase) {
        this.authenticationDatabase = authenticationDatabase;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return this.password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUri() {
        return this.uri;
    }

    public String determineUri() {
        return (this.uri != null) ? this.uri : DEFAULT_URI;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public UuidRepresentation getUuidRepresentation() {
        return uuidRepresentation;
    }

    public void setUuidRepresentation(UuidRepresentation uuidRepresentation) {
        this.uuidRepresentation = uuidRepresentation;
    }

    public String getReplicaSetName() {
        return this.replicaSetName;
    }

    public void setReplicaSetName(String replicaSetName) {
        this.replicaSetName = replicaSetName;
    }

    public long getServerSelectionTimeout() {
        return serverSelectionTimeout <= 0 ? 30000 : serverSelectionTimeout;
    }

    public void setServerSelectionTimeout(long serverSelectionTimeout) {
        this.serverSelectionTimeout = serverSelectionTimeout;
    }

    public String getMongoClientDatabase() {
        if (this.database != null) {
            return this.database;
        }
        return new ConnectionString(determineUri()).getDatabase();
    }

}

