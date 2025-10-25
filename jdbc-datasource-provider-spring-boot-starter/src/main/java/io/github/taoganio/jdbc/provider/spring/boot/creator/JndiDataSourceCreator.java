package io.github.taoganio.jdbc.provider.spring.boot.creator;

import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.Assert;

import javax.sql.DataSource;

public class JndiDataSourceCreator implements DataSourceCreator {

    private JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();

    @Override
    public DataSource create(DataSourceDefinition definition) {
        return jndiDataSourceLookup.getDataSource(definition.getJndiName());
    }

    public void setJndiDataSourceLookup(JndiDataSourceLookup jndiDataSourceLookup) {
        Assert.notNull(jndiDataSourceLookup, "JndiDataSourceLookup must not be null");
        this.jndiDataSourceLookup = jndiDataSourceLookup;
    }
}
