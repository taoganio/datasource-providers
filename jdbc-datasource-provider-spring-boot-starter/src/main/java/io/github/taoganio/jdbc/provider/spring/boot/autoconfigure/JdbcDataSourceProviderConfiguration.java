package io.github.taoganio.jdbc.provider.spring.boot.autoconfigure;

import io.github.taoganio.jdbc.provider.JdbcDataSourceProvider;
import io.github.taoganio.jdbc.provider.JdbcSimpleDataSourceProvider;
import io.github.taoganio.jdbc.provider.spring.boot.creator.DataSourceCreatorProvider;
import io.github.taoganio.jdbc.provider.spring.boot.creator.DataSourceDefinition;
import io.github.taoganio.jdbc.provider.spring.boot.creator.DataSourcePool;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;

@AutoConfiguration
@Import(DataSourceCreatorConfiguration.class)
@EnableConfigurationProperties(JdbcDataSourceProperties.class)
public class JdbcDataSourceProviderConfiguration {

    private final DataSourceCreatorProvider dataSourceCreatorProvider;

    public JdbcDataSourceProviderConfiguration(DataSourceCreatorProvider dataSourceCreatorProvider) {
        this.dataSourceCreatorProvider = dataSourceCreatorProvider;
    }

    @Bean
    @ConditionalOnMissingBean(JdbcDataSourceProvider.class)
    public JdbcDataSourceProvider jdbcDataSourceProvider(JdbcDataSourceProperties properties) throws Exception {
        Map<String, DataSourceDefinition> definitions = properties.getDefinitions();
        Assert.notEmpty(definitions, "No jdbc data source definitions found");

        String primaryKey = properties.getPrimary();

        DataSourceDefinition primaryDefinition = StringUtils.hasLength(primaryKey) ? definitions.get(primaryKey) : null;
        if (primaryDefinition == null) {
            Map.Entry<String, DataSourceDefinition> first = definitions.entrySet().iterator().next();
            primaryKey = first.getKey();
            primaryDefinition = first.getValue();
        }

        DataSourcePool pool = properties.getPool();
        HikariDataSource hikari = properties.getHikari();
        DataSource primaryDataSource =
                dataSourceCreatorProvider.getCreator(primaryDefinition, pool, hikari).create(primaryDefinition);
        JdbcSimpleDataSourceProvider provider = new JdbcSimpleDataSourceProvider(primaryDataSource);

        for (Map.Entry<String, DataSourceDefinition> entry : definitions.entrySet()) {
            String key = entry.getKey();
            if (key.equals(primaryKey)) {
                provider.addDataSourceMapping(key, primaryDataSource);
                continue;
            }
            DataSourceDefinition definition = entry.getValue();
            DataSource dataSource = dataSourceCreatorProvider.getCreator(definition, pool, hikari).create(definition);
            provider.addDataSourceMapping(key, dataSource);
        }
        return provider;
    }

}
