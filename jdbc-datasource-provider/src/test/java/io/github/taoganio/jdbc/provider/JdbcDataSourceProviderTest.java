package io.github.taoganio.jdbc.provider;

import io.github.taoganio.datasource.provider.ReadWriteScope;
import io.github.taoganio.datasource.provider.ReadWriteScopeRouter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * JDBC 数据源提供程序测试
 *
 * @author Tao Gan
 * @date 2025/10/10
 */
public class JdbcDataSourceProviderTest {

    private DataSourceConfig config;
    private JdbcDataSourceProvider dataSourceProvider;

    @BeforeEach
    void setUp() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("datasource.yml");
        config = DataSourceConfigLoader.load(resourceAsStream);

        String primary = config.getPrimary();
        Map<String, DataSource> definitions = config.getDefinitions();
        DataSource primaryDataSource = definitions.get(primary);
        JdbcSimpleDataSourceProvider provider = new JdbcSimpleDataSourceProvider(primaryDataSource);
        for (Map.Entry<String, DataSource> entry : definitions.entrySet()) {
            provider.addDataSourceMapping(entry.getKey(), entry.getValue());
        }
        this.dataSourceProvider = provider;
    }


    @Test
    void simpleDataSourceProvider() throws SQLException {
        DataSource dataSource = dataSourceProvider.getDataSource("slave2");
        Connection connection = dataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        System.out.println(metaData.getDatabaseProductName());
    }


    @Test
    void routerDataSourceProviderTest() throws SQLException {
        JdbcMultipleDataSourceRoutingProvider
                provider = new JdbcMultipleDataSourceRoutingProvider(dataSourceProvider);

        Map<String, List<String>> datasourceRouter = config.getRouters().getDatasourceRouter();
        for (Map.Entry<String, List<String>> entry : datasourceRouter.entrySet()) {
            provider.addRouterMapping(entry.getKey(), entry.getValue());
        }

        DataSource term = provider.getDataSource("term");
        Assertions.assertNotNull(term);

        DataSource mailfriend = provider.getDataSource("mailfriend");
        Assertions.assertNotNull(mailfriend);

        List<DataSource> mailfriends = provider.getDataSources("mailfriend");
        System.out.println(mailfriends.size());
    }


    @Test
    void readWriteDataSourceProviderTest() {
        JdbcReadWriteDataSourceRoutingProvider provider =
                new JdbcReadWriteDataSourceRoutingProvider(dataSourceProvider);
        List<ReadWriteScopeRouter> readwriteRouter = config.getRouters().getReadwriteRouter();
        readwriteRouter.forEach(provider::addRouter);

        DataSource mailfriend = provider.getDataSource("mailfriend", ReadWriteScope.READ);
        Assertions.assertNotNull(mailfriend);

    }

}
