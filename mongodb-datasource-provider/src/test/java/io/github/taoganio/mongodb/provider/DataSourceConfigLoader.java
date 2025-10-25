package io.github.taoganio.mongodb.provider;

import io.github.taoganio.datasource.provider.ReadWriteScope;
import io.github.taoganio.datasource.provider.ReadWriteScopeRouter;
import io.github.taoganio.datasource.provider.ReadWriteScopeRouterBean;
import io.github.taoganio.mongodb.MongoProperties;
import org.yaml.snakeyaml.Yaml;

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
        Map<String, MongoProperties> datasourceMap = new java.util.HashMap<>();
        for (Map.Entry<String, Object> entry : datasources.entrySet()) {
            String datasourceName = entry.getKey();
            Map<String, Object> datasourceProps = (Map<String, Object>) entry.getValue();

            MongoProperties mongoConfig = new MongoProperties();
            mongoConfig.setHost((String) datasourceProps.get("host"));
            mongoConfig.setPort((Integer) datasourceProps.get("port"));
            mongoConfig.setUsername((String) datasourceProps.get("username"));
            mongoConfig.setPassword(((String) datasourceProps.get("password")).toCharArray());
            mongoConfig.setDatabase((String) datasourceProps.get("database"));
            mongoConfig.setAuthenticationDatabase((String) datasourceProps.get("authenticationDatabase"));
            mongoConfig.setServerSelectionTimeout((Integer) datasourceProps.get("serverSelectionTimeout"));

            datasourceMap.put(datasourceName, mongoConfig);
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
            routerConfig.setDatasourceRouter((Map<String, List<String>>)  providers.get("datasource"));
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