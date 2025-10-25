package io.github.taoganio.jdbc.provider.spring.boot.creator;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;

public class DefaultDataSourceCreatorProvider implements DataSourceCreatorProvider {

    private final List<DataSourcePoolCreator> dataSourcePoolCreators;
    private final DefaultDataSourceCreator dataSourceCreator;
    private List<DataSourceWrapper> dataSourceWrappers;

    public DefaultDataSourceCreatorProvider(List<DataSourcePoolCreator> dataSourcePoolCreators) {
        this.dataSourcePoolCreators = dataSourcePoolCreators;
        this.dataSourceCreator = new DefaultDataSourceCreator(dataSourcePoolCreators);
    }

    /**
     * 默认数据源： Pool → Hikari
     */
    @Override
    public DataSourceCreator getCreator(DataSourceDefinition definition,
                                        DataSourcePool defaultPool, HikariDataSource defaultHikari) {

        return d -> {
            DataSource dataSource = dataSourceCreator.create(d);
            // Default pool
            if (dataSource == null && defaultPool != null && defaultPool.getType() != null) {
                DataSourcePoolCreator poolCreator = dataSourcePoolCreators
                        .stream().filter(c -> c.supports(defaultPool.getType())).findFirst().orElse(null);
                if (poolCreator != null) {
                    dataSource = poolCreator.create(definition, defaultPool.getProperties());
                }
            }

            // Default hikari
            if (dataSource == null) {
                HikariDataSourceCreator hikariDataSourceCreator
                        = new HikariDataSourceCreator(defaultHikari != null ? defaultHikari : new HikariDataSource());
                hikariDataSourceCreator.setCopyDataSource(defaultHikari != null);
                dataSource = hikariDataSourceCreator.create(d);
            }
            if (CollectionUtils.isEmpty(dataSourceWrappers)) {
                return dataSource;
            }
            DataSource wrapped = dataSource;
            for (DataSourceWrapper wrapper : dataSourceWrappers) {
                wrapped = wrapper.wrap(wrapped);
            }
            return wrapped;
        };
    }

    public void setDataSourceWrappers(List<DataSourceWrapper> dataSourceWrappers) {
        this.dataSourceWrappers = dataSourceWrappers;
    }
}
