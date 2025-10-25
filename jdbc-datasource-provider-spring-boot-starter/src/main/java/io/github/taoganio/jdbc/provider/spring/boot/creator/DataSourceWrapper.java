package io.github.taoganio.jdbc.provider.spring.boot.creator;

import javax.sql.DataSource;

/**
 * 数据源包装器
 */
public interface DataSourceWrapper {

    DataSource wrap(DataSource dataSource);
}
