package io.github.taoganio.jdbc.provider.spring.boot;

import io.github.taoganio.datasource.provider.ReadWriteScope;
import io.github.taoganio.jdbc.provider.JdbcDataSourceProvider;
import io.github.taoganio.jdbc.provider.JdbcDataSourceRoutingProvider;
import io.github.taoganio.jdbc.provider.JdbcReadWriteDataSourceProvider;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * JDBC 动态数据源，支持多种数据源切换策略的 Spring Boot 数据源实现。
 *
 * <p>该类通过 {@link JdbcDSContextHolder} 获取当前线程的数据源上下文，根据上下文信息动态选择合适的数据源。
 * 支持以下数据源切换策略：</p>
 *
 * <h3>数据源选择优先级：</h3>
 * <ol>
 *   <li><strong>无上下文或空 key</strong> → 使用主数据源（{@code dataSourceProvider.getPrimaryDataSource()}）</li>
 *   <li><strong>非路由模式</strong> → 直接从数据源提供者获取指定 key 的数据源</li>
 *   <li><strong>读写分离路由</strong> → 根据读写范围（ReadWriteScope）选择对应的读写数据源
 *       <ul>
 *         <li>优先使用指定范围的数据源（READ/WRITE）</li>
 *         <li>如果指定范围无数据源，则降级使用 ANY 范围的数据源</li>
 *       </ul>
 *   </li>
 *   <li><strong>数据源路由</strong> → 通过路由提供者获取数据源</li>
 *   <li><strong>容错处理</strong> → 如果未找到数据源且非严格模式，则回退到主数据源</li>
 * </ol>
 *
 * <h3>核心特性：</h3>
 * <ul>
 *   <li><strong>线程安全</strong>：基于 ThreadLocal 的上下文管理，支持多线程并发访问</li>
 *   <li><strong>读写分离</strong>：支持根据操作类型（读/写）自动选择合适的数据源</li>
 *   <li><strong>路由支持</strong>：支持自定义数据源路由策略</li>
 *   <li><strong>容错机制</strong>：支持严格模式和宽松模式，宽松模式下未找到数据源时自动回退</li>
 *   <li><strong>资源管理</strong>：实现 DisposableBean 接口，支持应用关闭时自动清理所有数据源资源</li>
 * </ul>
 *
 * <h3>使用方式：</h3>
 * <p>通过 {@link JdbcDS} 注解在方法或类上指定数据源切换策略，或通过 {@link JdbcDSContextHolder} 手动管理上下文。</p>
 *
 * @see JdbcDSContextHolder
 * @see JdbcDS
 * @see ReadWriteScope
 */
public class DynamicJdbcDataSource extends AbstractDataSource implements DisposableBean {

    private boolean strict = true;
    private final JdbcDataSourceProvider dataSourceProvider;
    @Nullable
    private JdbcDataSourceRoutingProvider dataSourceRoutingProvider;
    @Nullable
    private JdbcReadWriteDataSourceProvider readWriteDataSourceProvider;

    public DynamicJdbcDataSource(JdbcDataSourceProvider dataSourceProvider) {
        Assert.notNull(dataSourceProvider, "JdbcDataSourceProvider must not be null");
        this.dataSourceProvider = dataSourceProvider;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getDataSource().getConnection(username, password);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return getDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return super.isWrapperFor(iface) || getDataSource().isWrapperFor(iface);
    }

    /**
     * 根据当前上下文获取数据源
     *
     * @return 数据源
     */
    protected DataSource getDataSource() {
        JdbcDSContext context = JdbcDSContextHolder.peek();
        if (context == null || !StringUtils.hasLength(context.getKey())) {
            return dataSourceProvider.getPrimaryDataSource();
        }

        DataSource dataSource = null;
        String key = context.getKey();

        if (!context.isRouting()) {
            dataSource = dataSourceProvider.getDataSource(key);
        }
        // 读写分离路由
        else if (context.getReadWriteScope() != ReadWriteScope.UNKNOWN && readWriteDataSourceProvider != null) {
            dataSource = readWriteDataSourceProvider.getDataSource(key, context.getReadWriteScope());
            if (dataSource == null) {
                dataSource = readWriteDataSourceProvider.getDataSource(key, ReadWriteScope.ANY);
            }
        }
        // 数据源路由
        else if (dataSourceRoutingProvider != null) {
            dataSource = dataSourceRoutingProvider.getDataSource(key);
        }
        if (dataSource != null) {
            return dataSource;
        }
        if (!isStrict()) {
            return dataSourceProvider.getPrimaryDataSource();
        }
        throw new IllegalArgumentException("Cannot find data source [" + key + "]");
    }

    public void setDataSourceRoutingProvider(@Nullable JdbcDataSourceRoutingProvider dataSourceRoutingProvider) {
        this.dataSourceRoutingProvider = dataSourceRoutingProvider;
    }

    public void setReadWriteDataSourceProvider(@Nullable JdbcReadWriteDataSourceProvider readWriteDataSourceProvider) {
        this.readWriteDataSourceProvider = readWriteDataSourceProvider;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    @Override
    public void destroy() throws Exception {
        List<DataSource> allDataSources = dataSourceProvider.getAllDataSources();
        for (DataSource dataSource : allDataSources) {
            try {
                if (dataSource instanceof DisposableBean) {
                    ((DisposableBean) dataSource).destroy();
                }
                if (dataSource instanceof AutoCloseable) {
                    ((AutoCloseable) dataSource).close();
                }
                Method method = ReflectionUtils.findMethod(dataSource.getClass(), "close");
                if (method != null) {
                    ReflectionUtils.invokeMethod(method, dataSource);
                }
            } catch (Exception ignored) {

            }
        }
    }
}
