package io.github.taoganio.provider.test.config;

import io.github.taoganio.jdbc.provider.spring.boot.creator.DataSourceWrapper;
import org.apache.seata.rm.datasource.DataSourceProxy;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class SeataDataSourceWrapper implements DataSourceWrapper {

    @Override
    public DataSource wrap(DataSource dataSource) {
        if (dataSource instanceof DataSourceProxy) {
            return dataSource;
        }
        return new DataSourceProxy(dataSource);
    }
}
