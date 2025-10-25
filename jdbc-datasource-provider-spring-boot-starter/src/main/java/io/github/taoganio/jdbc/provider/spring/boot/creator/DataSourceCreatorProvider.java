package io.github.taoganio.jdbc.provider.spring.boot.creator;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.lang.Nullable;

/**
 * 数据源创建者提供程序
 */
public interface DataSourceCreatorProvider {

    /**
     * 获取数据源创建者
     *
     * @param definition    数据源定义
     * @param defaultPool   自定义默认连接池
     * @param defaultHikari 默认的Hikari连接池
     * @return 数据源创建者
     */
    DataSourceCreator getCreator(DataSourceDefinition definition,
                                 @Nullable DataSourcePool defaultPool,
                                 @Nullable HikariDataSource defaultHikari) throws Exception;
}
