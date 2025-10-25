package io.github.taoganio.jdbc.provider.spring.boot.autoconfigure;

import io.github.taoganio.datasource.provider.ReadWriteScope;
import io.github.taoganio.datasource.provider.ReadWriteScopeRouterBean;
import io.github.taoganio.jdbc.provider.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ConditionalOnBean(JdbcDataSourceProvider.class)
@AutoConfiguration(after = JdbcDataSourceProviderConfiguration.class)
public class JdbcDataSourceRoutingProvidersConfiguration {

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(JdbcReadWriteDataSourceRoutingProperties.class)
    @ConditionalOnProperty(prefix = "spring.datasource.jdbc.routers.readwrite", name = "enabled", havingValue = "true", matchIfMissing = true)
    public static class JdbcReadWriteDataSourceProviderConfiguration {

        @Bean
        @ConditionalOnMissingBean(JdbcReadWriteDataSourceProvider.class)
        public JdbcReadWriteDataSourceProvider jdbcReadWriteDataSourceProvider(JdbcDataSourceProvider dataSourceProvider,
                                                                               JdbcReadWriteDataSourceRoutingProperties properties) {
            JdbcReadWriteDataSourceRoutingProvider
                    provider = new JdbcReadWriteDataSourceRoutingProvider(dataSourceProvider);
            Map<String, JdbcReadWriteDataSourceRoutingProperties.ReadWriteRoute> readwrite = properties.getRoutes();
            if (!CollectionUtils.isEmpty(readwrite)) {
                for (Map.Entry<String, JdbcReadWriteDataSourceRoutingProperties.ReadWriteRoute> entry : readwrite.entrySet()) {
                    String key = entry.getKey();
                    JdbcReadWriteDataSourceRoutingProperties.ReadWriteRoute value = entry.getValue();
                    ReadWriteScopeRouterBean router = new ReadWriteScopeRouterBean(key);
                    if (value.getWrite() != null) {
                        router.addDataSourceKeyScope(ReadWriteScope.WRITE, value.getWrite());
                    }
                    if (value.getRead() != null) {
                        router.addDataSourceKeyScope(ReadWriteScope.READ, value.getRead());
                    }
                    if (value.getAny() != null) {
                        router.addDataSourceKeyScope(ReadWriteScope.ANY, value.getAny());
                    }
                    provider.addRouter(router);
                }
            }
            return provider;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(JdbcDataSourceRoutingProperties.class)
    @ConditionalOnProperty(prefix = "spring.datasource.jdbc.routers.datasource", name = "enabled", havingValue = "true", matchIfMissing = true)
    public static class JdbcDataSourceRoutingProviderConfiguration {

        @Bean
        @ConditionalOnMissingBean(JdbcDataSourceRoutingProvider.class)
        @SuppressWarnings("unchecked")
        public JdbcDataSourceRoutingProvider jdbcDataSourceRoutingProvider(JdbcDataSourceProvider dataSourceProvider,
                                                                           JdbcDataSourceRoutingProperties properties) {
            JdbcMultipleDataSourceRoutingProvider
                    provider = new JdbcMultipleDataSourceRoutingProvider(dataSourceProvider);
            Map<String, Object> datasource = properties.getRoutes();
            if (!CollectionUtils.isEmpty(datasource)) {
                for (Map.Entry<String, Object> entry : datasource.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Collection) {
                        provider.addRouterMapping(entry.getKey(), (Collection<String>) value);
                    } else if (value instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) value;
                        if (map.size() == 1) {
                            map.entrySet().stream().findFirst()
                                    .ifPresent(e -> provider.addRouterMapping(entry.getKey(), e.getValue().toString()));
                        } else {
                            List<String> values = map.values().stream()
                                    .map(Object::toString).collect(Collectors.toList());
                            provider.addRouterMapping(entry.getKey(), values);
                        }
                    } else if (value instanceof String) {
                        provider.addRouterMapping(entry.getKey(), (String) value);
                    }
                }
            }
            return provider;
        }
    }
}
