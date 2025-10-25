package io.github.taoganio.jdbc.provider.spring.boot.creator;

import org.springframework.lang.Nullable;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据源池创建者
 */
public interface DataSourcePoolCreator {

    boolean supports(Class<? extends DataSource> type);

    DataSource create(DataSourceDefinition definition, @Nullable Properties properties) throws Exception;

}
