package io.github.taoganio.jdbc.provider;

import javax.sql.DataSource;
import java.util.Map;

public class DataSourceConfig {

    private String primary;

    private Map<String, DataSource> definitions;

    private RouterConfig routers;

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public Map<String, DataSource> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(Map<String, DataSource> definitions) {
        this.definitions = definitions;
    }

    public RouterConfig getRouters() {
        return routers;
    }

    public void setRouters(RouterConfig routers) {
        this.routers = routers;
    }
}
