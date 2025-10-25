package io.github.taoganio.mongodb.provider.spring.boot;

import io.github.taoganio.datasource.provider.ReadWriteScope;
import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.github.taoganio.mongodb.MongoDataSource;
import io.github.taoganio.mongodb.provider.MongoDataSourceProvider;
import io.github.taoganio.mongodb.provider.MongoDataSourceRoutingProvider;
import io.github.taoganio.mongodb.provider.MongoReadWriteDataSourceProvider;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoDatabaseFactorySupport;
import org.springframework.data.mongodb.core.MongoExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * MongoDB 动态数据库工厂，支持多种数据源切换策略的 Spring Data MongoDB 数据库工厂实现。
 *
 * <p>该类通过 {@link MongoDSContextHolder} 获取当前线程的数据源上下文，根据上下文信息动态选择合适的数据源和数据库。
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
 * <h3>数据库选择逻辑：</h3>
 * <ul>
 *   <li><strong>无数据库指定</strong> → 使用数据源的默认数据库</li>
 *   <li><strong>指定数据库</strong> → 使用指定数据库名称，通过 {@code MongoClient.getDatabase(databaseName)} 获取</li>
 * </ul>
 *
 * <h3>核心特性：</h3>
 * <ul>
 *   <li><strong>线程安全</strong>：基于 ThreadLocal 的上下文管理，支持多线程并发访问</li>
 *   <li><strong>读写分离</strong>：支持根据操作类型（读/写）自动选择合适的数据源</li>
 *   <li><strong>路由支持</strong>：支持自定义数据源路由策略</li>
 *   <li><strong>数据库切换</strong>：支持在同一数据源下切换不同的数据库</li>
 *   <li><strong>容错机制</strong>：支持严格模式和宽松模式，宽松模式下未找到数据源时自动回退</li>
 *   <li><strong>资源管理</strong>：支持应用关闭时自动清理所有数据源资源</li>
 * </ul>
 *
 * <h3>使用方式：</h3>
 * <p>通过 {@link MongoDS} 注解在方法或类上指定数据源切换策略，或通过 {@link MongoDSContextHolder} 手动管理上下文。</p>
 *
 * @see MongoDSContextHolder
 * @see MongoDS
 * @see ReadWriteScope
 */
public class DynamicMongoDatabaseFactory extends MongoDatabaseFactorySupport<MongoClient> {

    private boolean strict = true;
    private final MongoDataSourceProvider dataSourceProvider;
    @Nullable
    private MongoDataSourceRoutingProvider dataSourceRoutingProvider;
    @Nullable
    private MongoReadWriteDataSourceProvider readWriteDataSourceProvider;

    public DynamicMongoDatabaseFactory(MongoDataSourceProvider dataSourceProvider) {
        super(dataSourceProvider.getPrimaryDataSource().getMongoClient(),
                dataSourceProvider.getPrimaryDataSource().getDatabase().getName(),
                true, new MongoExceptionTranslator());
        this.dataSourceProvider = dataSourceProvider;
    }

    @Override
    protected MongoClient getMongoClient() {
        return getDataSource().getMongoClient();
    }

    /**
     * 同时指定 key 和 database 返回根据 key 查找连接和连接下 database 数据库。
     * 指定 key, 根据 key 查找连接, 返回连接下默认数据库。
     * 指定 database, 返回默认连接下 database 数据库, 否则返回默认连接下默认数据库
     *
     * @return {@link MongoDatabase}
     * @throws DataAccessException 数据访问异常
     */
    @Override
    public MongoDatabase getMongoDatabase() throws DataAccessException {
        MongoDataSource dataSource = getDataSource();
        MongoDSContext context = MongoDSContextHolder.peek();
        if (context == null || !StringUtils.hasText(context.getDatabase())) {
            return dataSource.getDatabase();
        }
        return dataSource.getMongoClient().getDatabase(context.getDatabase());
    }

    @Override
    protected MongoDatabase doGetMongoDatabase(String dbName) {
        Assert.hasText(dbName, "Database name must not be empty!");
        return getMongoClient().getDatabase(dbName);
    }

    @Override
    public ClientSession getSession(ClientSessionOptions options) {
        return getMongoClient().startSession(options);
    }

    @Override
    protected void closeClient() {
        List<MongoDataSource> allDataSources = dataSourceProvider.getAllDataSources();
        for (MongoDataSource dataSource : allDataSources) {
            try {
                dataSource.close();
            } catch (IOException ignored) {
            }
        }
    }

    protected MongoDataSource getDataSource() {
        MongoDSContext context = MongoDSContextHolder.peek();
        if (context == null || !StringUtils.hasLength(context.getKey())) {
            return dataSourceProvider.getPrimaryDataSource();
        }
        MongoDataSource dataSource = null;
        String key = context.getKey();
        if (!context.isRouting()) {
            dataSource = dataSourceProvider.getDataSource(key);
        }
        //
        else if (context.getReadWriteScope() != ReadWriteScope.UNKNOWN && readWriteDataSourceProvider != null) {
            dataSource = readWriteDataSourceProvider.getDataSource(key, context.getReadWriteScope());
            if (dataSource == null) {
                dataSource = readWriteDataSourceProvider.getDataSource(key, ReadWriteScope.ANY);
            }
        }
        //
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

    public void setDataSourceRoutingProvider(@Nullable MongoDataSourceRoutingProvider dataSourceRoutingProvider) {
        this.dataSourceRoutingProvider = dataSourceRoutingProvider;
    }

    public void setReadWriteDataSourceProvider(@Nullable MongoReadWriteDataSourceProvider readWriteDataSourceProvider) {
        this.readWriteDataSourceProvider = readWriteDataSourceProvider;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }
}
