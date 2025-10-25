package io.github.taoganio.jdbc.provider;

import io.github.taoganio.datasource.provider.ReadWriteScope;
import io.github.taoganio.datasource.provider.ReadWriteScopeRouter;
import io.github.taoganio.datasource.provider.ReadWriteScopeRouterBean;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataSourceConfigLoader {

    @SuppressWarnings("unchecked")
    public static DataSourceConfig load(InputStream configInputStream) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = configInputStream) {
            Map<String, Object> root = yaml.load(inputStream);
            Map<String, Object> datasourceConfig = (Map<String, Object>) root.get("datasource");

            DataSourceConfig config = new DataSourceConfig();
            config.setPrimary((String) datasourceConfig.get("primary"));

            // 解析数据源配置
            parseDataSources(datasourceConfig, config);

            // 解析路由配置
            parseRouters(datasourceConfig, config);

            return config;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load datasource config", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void parseDataSources(Map<String, Object> datasourceConfig, DataSourceConfig config) {
        Map<String, Object> datasources = (Map<String, Object>) datasourceConfig.get("definitions");
        Map<String, DataSource> datasourceMap = new java.util.HashMap<>();
        for (Map.Entry<String, Object> entry : datasources.entrySet()) {
            String datasourceName = entry.getKey();
            Map<String, Object> datasourceProps = (Map<String, Object>) entry.getValue();

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setPoolName("HikariCP 连接池");
            hikariConfig.setUsername((String) datasourceProps.get("username"));
            hikariConfig.setPassword(((String) datasourceProps.get("password")));
            hikariConfig.setJdbcUrl(((String) datasourceProps.get("url")));
            hikariConfig.setMaximumPoolSize(15);

            DataSource hikariDataSource = new HikariDataSource(hikariConfig);

            datasourceMap.put(datasourceName, hikariDataSource);
        }
        config.setDefinitions(datasourceMap);
    }

    @SuppressWarnings("unchecked")
    private static void parseRouters(Map<String, Object> datasourceConfig, DataSourceConfig config) {
        // 解析提供者配置
        if (!datasourceConfig.containsKey("routers")) {
            return;
        }
        Map<String, Object> providers = (Map<String, Object>) datasourceConfig.get("routers");
        RouterConfig routerConfig = new RouterConfig();

        if (providers.containsKey("datasource")) {
            routerConfig.setDatasourceRouter((Map<String, List<String>>) providers.get("datasource"));
        }
        if (providers.containsKey("readwrite")) {
            Map<String, Object> readwriteRouter = (Map<String, Object>) providers.get("readwrite");
            List<ReadWriteScopeRouter> readwriteRouters = new ArrayList<>();

            for (Map.Entry<String, Object> entry : readwriteRouter.entrySet()) {
                String routerKey = entry.getKey();
                Map<String, Object> routerProps = (Map<String, Object>) entry.getValue();

                ReadWriteScopeRouterBean scopeRouter = new ReadWriteScopeRouterBean(routerKey);
                if (routerProps.get("read") != null) {
                    scopeRouter.addDataSourceKeyScope(ReadWriteScope.READ, (String) routerProps.get("read"));
                }
                if (routerProps.get("write") != null) {
                    scopeRouter.addDataSourceKeyScope(ReadWriteScope.WRITE, (String) routerProps.get("write"));
                }
                if (routerProps.get("any") != null) {
                    scopeRouter.addDataSourceKeyScope(ReadWriteScope.ANY, (String) routerProps.get("any"));
                }
                readwriteRouters.add(scopeRouter);
            }
            routerConfig.setReadwriteRouter(readwriteRouters);
        }

        config.setRouters(routerConfig);
    }

}