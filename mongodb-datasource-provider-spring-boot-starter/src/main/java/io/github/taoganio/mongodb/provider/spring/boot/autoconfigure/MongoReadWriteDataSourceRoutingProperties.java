package io.github.taoganio.mongodb.provider.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "spring.datasource.mongodb.routers.readwrite")
public class MongoReadWriteDataSourceRoutingProperties {

    private boolean enabled = true;

    private Map<String, ReadWriteRoute> routes;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, ReadWriteRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, ReadWriteRoute> routes) {
        this.routes = routes;
    }

    public static class ReadWriteRoute {

        private String read;
        private String write;
        private String any;

        public String getRead() {
            return read;
        }

        public void setRead(String read) {
            this.read = read;
        }

        public String getWrite() {
            return write;
        }

        public void setWrite(String write) {
            this.write = write;
        }

        public String getAny() {
            return any;
        }

        public void setAny(String any) {
            this.any = any;
        }
    }
}
