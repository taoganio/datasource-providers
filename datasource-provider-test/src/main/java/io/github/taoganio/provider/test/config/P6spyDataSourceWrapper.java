package io.github.taoganio.provider.test.config;

import com.p6spy.engine.spy.P6DataSource;
import io.github.taoganio.jdbc.provider.spring.boot.creator.DataSourceWrapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class P6spyDataSourceWrapper implements DataSourceWrapper {

    @Override
    public DataSource wrap(DataSource dataSource) {
        if (dataSource instanceof P6DataSource) {
            return dataSource;
        }
        return new P6DataSource(dataSource);
    }
}
