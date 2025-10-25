package io.github.taoganio.jdbc.provider.spring.boot.creator;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.springframework.beans.factory.BeanClassLoaderAware;

import javax.sql.DataSource;
import java.util.Properties;

public class AtomikosDataSourceCreator implements DataSourcePoolCreator, BeanClassLoaderAware {

    private ClassLoader classLoader;

    @Override
    public boolean supports(Class<? extends DataSource> type) {
        return AtomikosDataSourceBean.class.isAssignableFrom(type);
    }

    @Override
    public DataSource create(DataSourceDefinition definition, Properties properties) throws Exception {
        AtomikosDataSourceBean xaDataSource =
                DataSourceUtils.bindProperty(AtomikosDataSourceBean::new, properties);
        xaDataSource.setUniqueResourceName(definition.determineDatabaseName());
        XADataSourceCreator creator = new XADataSourceCreator(dataSource -> {
            xaDataSource.setXaDataSource(dataSource);
            return xaDataSource;
        });
        creator.setBeanClassLoader(classLoader);
        return creator.create(definition);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
