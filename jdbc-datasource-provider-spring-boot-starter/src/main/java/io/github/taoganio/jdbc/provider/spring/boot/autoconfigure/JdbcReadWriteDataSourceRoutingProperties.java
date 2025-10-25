package io.github.taoganio.jdbc.provider.spring.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "spring.datasource.jdbc.routers.readwrite")
public class JdbcReadWriteDataSourceRoutingProperties {

    /**
     * 启用读写路由
     */
    private boolean enabled = true;

    /**
     * 路由配置
     */
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

        /** 读 */
        private String read;
        /** 写 */
        private String write;
        /** 任何 */
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
