package io.github.taoganio.jdbc.provider.spring.boot.creator;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.jdbc.XADataSourceWrapper;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.util.HashMap;
import java.util.Map;

public class XADataSourceCreator implements DataSourceCreator, BeanClassLoaderAware {

    private ClassLoader classLoader;
    private final XADataSourceWrapper xaDataSourceWrapper;

    public XADataSourceCreator(XADataSourceWrapper xaDataSourceWrapper) {
        Assert.notNull(xaDataSourceWrapper, "XADataSourceWrapper must not be null");
        this.xaDataSourceWrapper = xaDataSourceWrapper;
    }

    @Override
    public DataSource create(DataSourceDefinition definition) throws Exception {
        return xaDataSourceWrapper.wrapDataSource(createXaDataSource(definition));
    }

    protected XADataSource createXaDataSource(DataSourceProperties properties) {
        String className = properties.getXa().getDataSourceClassName();
        if (!StringUtils.hasLength(className)) {
            className = DatabaseDriver.fromJdbcUrl(properties.determineUrl()).getXaDataSourceClassName();
        }
        Assert.hasLength(className, "No XA DataSource class name specified");
        XADataSource dataSource = createXaDataSourceInstance(className);
        bindXaProperties(dataSource, properties);
        return dataSource;
    }

    protected XADataSource createXaDataSourceInstance(String className) {
        try {
            Class<?> dataSourceClass = ClassUtils.forName(className, this.classLoader);
            Object instance = BeanUtils.instantiateClass(dataSourceClass);
            Assert.isInstanceOf(XADataSource.class, instance);
            return (XADataSource) instance;
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create XADataSource instance from '" + className + "'");
        }
    }

    protected void bindXaProperties(XADataSource target, DataSourceProperties dataSourceProperties) {
        Binder binder = new Binder(getBinderSource(dataSourceProperties));
        binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(target));
    }

    protected ConfigurationPropertySource getBinderSource(DataSourceProperties dataSourceProperties) {
        Map<Object, Object> properties = new HashMap<>(dataSourceProperties.getXa().getProperties());
        properties.computeIfAbsent("user", (key) -> dataSourceProperties.determineUsername());
        properties.computeIfAbsent("password", (key) -> dataSourceProperties.determinePassword());
        try {
            properties.computeIfAbsent("url", (key) -> dataSourceProperties.determineUrl());
        } catch (BeanCreationException ex) {
            // Continue as not all XA DataSource's require a URL
        }
        MapConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();
        aliases.addAliases("user", "username");
        return source.withAliases(aliases);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
