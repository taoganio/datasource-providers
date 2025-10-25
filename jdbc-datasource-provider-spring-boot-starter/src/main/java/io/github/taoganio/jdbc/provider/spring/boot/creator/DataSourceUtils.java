package io.github.taoganio.jdbc.provider.spring.boot.creator;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.function.Supplier;

public abstract class DataSourceUtils {

    public static <T extends DataSource> T bindProperty(Supplier<T> instanceSupplier,
                                                        @Nullable Properties properties) {
        if (properties == null) {
            return instanceSupplier.get();
        }
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addLast(new PropertiesPropertySource("dataSourceProperty", properties));
        return bindProperty(instanceSupplier, env);
    }

    public static <T extends DataSource> T bindProperty(Supplier<T> instanceSupplier,
                                                        @Nullable Environment environment) {
        T t = instanceSupplier.get();
        if (environment == null) {
            return t;
        }
        Binder binder = Binder.get(environment);
        return binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(t)).get();
    }

    public static void setHikariDataSourceProperty(HikariDataSource hikariDataSource,
                                                   DataSourceDefinition definition, boolean validate) {
        hikariDataSource.setJdbcUrl(definition.determineUrl());
        hikariDataSource.setUsername(definition.determineUsername());
        hikariDataSource.setPassword(definition.determinePassword());
        hikariDataSource.setDriverClassName(definition.determineDriverClassName());
        hikariDataSource.setPoolName(definition.determineDatabaseName());
        if (validate) {
            hikariDataSource.validate();
        }
    }
}