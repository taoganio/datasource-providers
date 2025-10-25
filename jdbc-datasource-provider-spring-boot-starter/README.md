# JDBC Provider Spring Boot Starter 使用教程

## 概述

`jdbc-provider-spring-boot-starter` 是一个基于Spring Boot的多数据源管理框架，支持通过注解在类或方法级别动态切换JDBC数据源，同时也支持编程方式获取数据源。该框架提供了简单易用的注解配置，支持数据源路由和读写分离功能。

## 核心特性

- **多数据源管理**：支持配置多个JDBC数据源
- **注解驱动**：通过 @JdbcDS 注解实现声明式数据源切换
- **编程方式**：通过接口编程方式获取数据源
- **多种切换策略**：支持直接切换、路由切换、读写分离
- **连接池定制**：支持自定义连接池实现，默认使用HikariCP
- **SpEL表达式支持**：支持动态表达式计算数据源标识
- **自动配置**：Spring Boot自动配置，开箱即用

## 快速开始

### 1. 添加依赖

在您的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>io.github.taoganio</groupId>
    <artifactId>jdbc-provider-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置数据源

在 `application.yml` 中配置多数据源：

```yaml
spring:
  datasource:
    jdbc:
      ds-aop: true
      strict: true
      primary: db0
      hikari:
        maximumPoolSize: 30
        minimumIdle: 10
        connectionTimeout: 30000
        idleTimeout: 600000
        maxLifetime: 1800000

      pool:
        type: com.zaxxer.hikari.HikariDataSource
        properties:
          maximumPoolSize: 30
          minimumIdle: 10
      
      definitions:
        db0:
          name: master
          url: jdbc:mysql://localhost:3306/db1?useSSL=false&serverTimezone=Asia/Shanghai
          username: root
          password: password
          driver-class-name: com.mysql.cj.jdbc.Driver
          hikari:
            maximumPoolSize: 30
            minimumIdle: 10
        db1:
          name: slave1
          url: jdbc:mysql://localhost:3306/db2?useSSL=false&serverTimezone=Asia/Shanghai
          username: root
          password: password
          driver-class-name: com.mysql.cj.jdbc.Driver
          hikari:
            maximumPoolSize: 30
            minimumIdle: 10
        db2:
          name: slave2
          url: jdbc:postgresql://localhost:5432/db3
          username: postgres
          password: password
          driver-class-name: org.postgresql.Driver
```

### 3. 配置项说明

#### 主配置项

| 配置项 | 是否必填 | 默认值 | 说明 |
|--------|----------|--------|------|
| `spring.datasource.jdbc.ds-aop` | 否 | true | 启动 JdbcDS AOP 切面 |
| `spring.datasource.jdbc.strict` | 否 | false | 严格模式，为true时找不到数据源会抛出异常 |
| `spring.datasource.jdbc.primary` | 否 | 第一个定义的数据源 | 主数据源标识 |
| `spring.datasource.jdbc.hikari` | 否 | - | 默认HikariCP连接池配置 |
| `spring.datasource.jdbc.pool` | 否 | - | 自定义连接池配置，优先级高于hikari配置 |
| `spring.datasource.jdbc.pool.type` | 是 | - | 自定义连接池类型 |
| `spring.datasource.jdbc.pool.properties` | 否 | - | 自定义连接池属性 |
| `spring.datasource.jdbc.definitions` | 是 | - | 数据源定义映射 |


#### 数据源定义配置项

| 配置项 | 是否必填 | 默认值 | 说明 |
|--------|----------|--------|------|
| `definitions.{key}.name` | 否 | - | 数据源名称 |
| `definitions.{key}.url` | 是 | - | 数据库连接URL |
| `definitions.{key}.username` | 是 | - | 数据库用户名 |
| `definitions.{key}.password` | 是 | - | 数据库密码 |
| `definitions.{key}.driver-class-name` | 否 | 自动检测 | 数据库驱动类名 |
| `definitions.{key}.jndiName` | 否 | - | 数据源的 JNDI 位置。设置时，类、url、用户名和密码将被忽略 |
| `definitions.{key}.type` | 否 | - | 该数据源的自定义连接池类型，优先级高于HikariCP |
| `definitions.{key}.pool.properties` | 否 | - | 该数据源的自定义连接池属性配置 |
| `definitions.{key}.hikari` | 否 | - | 该数据源的HikariCP配置 |

#### 包含 org.springframework.boot.autoconfigure.jdbc.DataSourceProperties 的所有属性

## 注解使用

### @JdbcDS 注解

`@JdbcDS` 注解用于在方法、接口方法、类及其父类级别指定要使用的数据源。

#### 注解参数

- `routing`: 是否启用路由模式（默认：false）
- `key`: 数据源标识或路由键（默认：空字符串）
- `scope`: 读写范围（默认：UNKNOWN）

#### 使用示例

##### 1. 直接指定数据源

```java
@Service
public class UserService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // 使用 db0 数据源
    @JdbcDS(key = "'db0'")
    public void queryFromMaster() {
        // 查询操作
    }
    
    // 使用 db1 数据源
    @JdbcDS(key = "'db1'")
    public void queryFromSlave() {
        // 查询操作
    }
}
```

##### 2. 类级别注解

```java
@Service
@JdbcDS(key = "'db0'")  // 整个类默认使用 db0 数据源
public class ProductService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public void defaultOperation() {
        // 使用类级别指定的 db0 数据源
    }
    
    // 方法级别覆盖类级别配置
    @JdbcDS(key = "'db1'")
    public void specificOperation() {
        // 使用 db1 数据源
    }
}
```

## 数据源路由

### 配置路由规则

在 `application.yml` 中配置数据源路由：

```yaml
spring:
  datasource:
    jdbc:
      # ... 数据源定义 ...
      
      routers:
        # 数据源路由配置
        datasource:
          enabled: true
          routes:
            user_service: db0      # 用户服务使用 db0
            order_service: db1     # 订单服务使用 db1
            product_service: db2   # 商品服务使用 db2
```

#### 路由配置项说明

| 配置项 | 是否必填 | 默认值 | 说明 |
|--------|----------|--------|------|
| `spring.datasource.jdbc.routers.datasource.enabled` | 否 | true | 是否启用数据源路由 |
| `spring.datasource.jdbc.routers.datasource.routes` | 否 | - | 路由映射配置 |

### 使用路由

```java
@Service
public class BusinessService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // 使用路由键选择数据源
    @JdbcDS(routing = true, key = "'user_service'")
    public void userOperation() {
        // 自动路由到 db0 数据源
    }
    
    @JdbcDS(routing = true, key = "'order_service'")
    public void orderOperation() {
        // 自动路由到 db1 数据源
    }
}
```

## 读写分离

### 配置读写分离

```yaml
spring:
  datasource:
    jdbc:
      # ... 数据源定义 ...
      
      routers:
        # 读写分离配置
        readwrite:
          enabled: true
          routes:
            user_service:
              read: db1    # 读操作使用 db1
              write: db0   # 写操作使用 db0
            order_service:
              read: db2    # 读操作使用 db2
              write: db1   # 写操作使用 db1
            product_service:
              any: db0     # 任意操作使用 db0
```

#### 读写分离配置项说明

| 配置项 | 是否必填 | 默认值 | 说明 |
|--------|----------|--------|------|
| `spring.datasource.jdbc.routers.readwrite.enabled` | 否 | true | 是否启用读写分离 |
| `spring.datasource.jdbc.routers.readwrite.routes` | 否 | - | 读写分离路由配置 |
| `routes.{service}.read` | 否 | - | 读操作使用的数据源 |
| `routes.{service}.write` | 否 | - | 写操作使用的数据源 |
| `routes.{service}.any` | 否 | - | 任意操作使用的数据源 |

### 使用读写分离

```java
@Service
public class UserService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // 读操作，自动路由到读数据源
    @JdbcDS(routing = true, key = "'user_service'", scope = ReadWriteScope.READ)
    public void readOperation() {
        // 自动路由到 db1 数据源
    }
    
    // 写操作，自动路由到写数据源
    @JdbcDS(routing = true, key = "'user_service'", scope = ReadWriteScope.WRITE)
    public void writeOperation() {
        // 自动路由到 db0 数据源
    }
    
    // 任意操作，根据配置使用指定数据源
    @JdbcDS(routing = true, key = "'product_service'", scope = ReadWriteScope.ANY)
    public void anyOperation() {
        // 自动路由到 db0 数据源
    }
}
```

## 编程方式获取数据源

除了使用注解方式，还可以通过编程方式获取数据源：

### 使用 JdbcDataSourceProvider

```java
@Service
public class DataSourceService {
    
    @Autowired
    private JdbcDataSourceProvider dataSourceProvider;
    
    public void useDataSource() {
        // 获取主数据源
        DataSource primaryDataSource = dataSourceProvider.getPrimaryDataSource();
        
        // 根据key获取指定数据源
        DataSource masterDataSource = dataSourceProvider.getDataSource("db0");
        DataSource slaveDataSource = dataSourceProvider.getDataSource("db1");
        
        // 使用数据源进行操作
        JdbcTemplate masterTemplate = new JdbcTemplate(masterDataSource);
        JdbcTemplate slaveTemplate = new JdbcTemplate(slaveDataSource);
    }
}
```

### 使用 JdbcDataSourceRoutingProvider

```java
@Service
public class RoutingService {
    
    @Autowired
    private JdbcDataSourceRoutingProvider routingProvider;
    
    public void useRoutingDataSource() {
        // 根据路由键获取数据源列表
        List<DataSource> userDataSources = routingProvider.getDataSources("user_service");
        List<DataSource> orderDataSources = routingProvider.getDataSources("order_service");
        
        // 获取第一个数据源
        DataSource firstDataSource = routingProvider.getDataSource("user_service");
        
        // 使用数据源进行操作
        if (firstDataSource != null) {
            JdbcTemplate template = new JdbcTemplate(firstDataSource);
            // 执行操作
        }
    }
}
```

## 高级配置

### 连接池配置

框架默认使用 HikariCP 作为连接池，但支持自定义连接池实现：

#### 1. 默认 HikariCP 配置

```yaml
spring:
  datasource:
    jdbc:
      # 全局默认 HikariCP 配置
      hikari:
        maximumPoolSize: 30
        minimumIdle: 10
        connectionTimeout: 30000
        idleTimeout: 600000
        maxLifetime: 1800000
```

#### 2. 自定义连接池配置

如果需要使用其他连接池，需要实现 `DataSourcePoolCreator` 接口：

```java
@Component
public class CustomDataSourcePoolCreator implements DataSourcePoolCreator {
    
    @Override
    public boolean supports(Class<? extends DataSource> type) {
        return DruidDataSource.class.isAssignableFrom(type);
    }
    
    @Override
    public DataSource create(DataSourceDefinition definition, Properties properties) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(definition.getUrl());
        dataSource.setUsername(definition.getUsername());
        dataSource.setPassword(definition.getPassword());
        
        // 设置自定义属性
        if (properties != null) {
            dataSource.setMaxActive(Integer.parseInt(properties.getProperty("maxActive", "20")));
            dataSource.setInitialSize(Integer.parseInt(properties.getProperty("initialSize", "5")));
        }
        
        return dataSource;
    }
}
```

然后在配置中指定自定义连接池：

```yaml
spring:
  datasource:
    jdbc:
      # 自定义连接池配置（优先级高于hikari）
      pool:
        type: com.alibaba.druid.pool.DruidDataSource
        properties:
          maxActive: 50
          initialSize: 10
          maxWait: 60000
```

#### 3. 自定义数据源包装器
```java
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
```

#### 4. 数据源级别连接池配置

每个数据源可以单独配置连接池：

```yaml
spring:
  datasource:
    jdbc:
      definitions:
        db0:
          name: master
          url: jdbc:mysql://localhost:3306/db1
          username: root
          password: password
          # 该数据源使用 HikariCP
          hikari:
            maximumPoolSize: 50
            minimumIdle: 20
        db1:
          name: slave
          url: jdbc:mysql://localhost:3306/db2
          username: root
          password: password
          # 该数据源使用自定义连接池
          pool:
            type: com.alibaba.druid.pool.DruidDataSource
            properties:
              maxActive: 30
              initialSize: 5
```

### 连接池优先级说明

1. **数据源级别：Jndi 配置 > pool 配置 > hikari 配置**
2. **全局 pool 配置** > **全局 hikari 配置**
3. **数据源级别配置** > **全局配置**

连接池配置的优先级从高到低为：
- 数据源级别的 `pool` 配置
- 数据源级别的 `hikari` 配置
- 全局的 `pool` 配置
- 全局的 `hikari` 配置

通过以上配置和使用方式，您可以轻松地在Spring Boot应用中实现多数据源管理和动态切换功能。


## 最佳实践
- 为了更灵活的获取数据源，注解支持 SpEL 表达式，固定数据源标识或路由键时，请使用单引号包裹字符串，如 `@JdbcDS(key = "'db0'")`。
- 框架中提供了一些扩展点修改框架行为，不在此处一一列举，感兴趣的读者可以自行阅读源码。