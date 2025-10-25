package io.github.taoganio.jdbc.provider.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "spring.datasource.jdbc.routers.datasource")
public class JdbcDataSourceRoutingProperties {

    /**
     * 启用路由功能
     */
    private boolean enabled = true;
    /**
     * 路由配置
     */
    private Map<String, Object> routes;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, Object> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, Object> routes) {
        this.routes = routes;
    }
}
