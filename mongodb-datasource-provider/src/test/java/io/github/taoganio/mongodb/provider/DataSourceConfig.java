package io.github.taoganio.mongodb.provider;


import io.github.taoganio.mongodb.MongoProperties;

import java.util.Map;

public class DataSourceConfig {

    private String primary;

    private Map<String, MongoProperties> definitions;

    private RouterConfig routers;

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public Map<String, MongoProperties> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, MongoProperties> definitions) {
        this.definitions = definitions;
    }

    public RouterConfig getRouters() {
        return routers;
    }

    public void setRouters(RouterConfig routers) {
        this.routers = routers;
    }
}
