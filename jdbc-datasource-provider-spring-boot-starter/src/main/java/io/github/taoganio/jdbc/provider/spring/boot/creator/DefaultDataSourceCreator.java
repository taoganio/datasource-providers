package io.github.taoganio.jdbc.provider.spring.boot.creator;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultDataSourceCreator implements DataSourceCreator {

    private final List<DataSourcePoolCreator> poolCreators;
    private JndiDataSourceCreator jndiDataSourceCreator = new JndiDataSourceCreator();

    public DefaultDataSourceCreator(List<DataSourcePoolCreator> poolCreators) {
        this.poolCreators = poolCreators == null ? Collections.emptyList() : poolCreators;
    }

    /**
     * 创建顺序：JNDI -> Pool -> Hikari
     */
    @Override
    public DataSource create(DataSourceDefinition definition) throws Exception {

        // JNDI datasource
        String jndiName = definition.getJndiName();
        if (jndiName != null) {
            return jndiDataSourceCreator.create(definition);
        }

        // pool
        Class<? extends DataSource> type = definition.getType();
        if (type != null) {
            DataSourcePoolCreator poolCreator = getPoolCreator(type);
            if (poolCreator != null) {
                return poolCreator.create(definition,
                        Optional.ofNullable(definition.getPool())
                                .map(DataSourceDefinition.Pool::getProperties).orElse(null));
            }
        }

        // hikari
        HikariDataSource hikari = definition.getHikari();
        if (hikari != null) {
            HikariDataSourceCreator creator = new HikariDataSourceCreator(hikari);
            creator.setCopyDataSource(false);
            return creator.create(definition);
        }

        return null;
    }

    protected DataSourcePoolCreator getPoolCreator(Class<? extends DataSource> type) {
        return poolCreators.stream()
                .filter(creator -> creator.supports(type))
                .findFirst()
                .orElse(null);
    }

    public void setJndiDataSourceCreator(JndiDataSourceCreator jndiDataSourceCreator) {
        Assert.notNull(jndiDataSourceCreator, "JndiDataSourceCreator must not be null");
        this.jndiDataSourceCreator = jndiDataSourceCreator;
    }
}
