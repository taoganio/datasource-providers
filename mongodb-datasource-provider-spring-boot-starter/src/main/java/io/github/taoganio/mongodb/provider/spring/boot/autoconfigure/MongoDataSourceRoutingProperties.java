package io.github.taoganio.mongodb.provider.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "spring.datasource.mongodb.routers.datasource")
public class MongoDataSourceRoutingProperties {

    private boolean enabled = true;
    private Map<String, Object> routes;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public  Map<String, Object>  getRoutes() {
        return routes;
    }

    public void setRoutes( Map<String, Object>  routes) {
        this.routes = routes;
    }
}
