# MongoDB Provider Spring Boot Starter 使用教程

## 概述

`mongodb-provider-spring-boot-starter` 是一个基于Spring Boot的多MongoDB数据源管理框架，支持通过注解在类或方法级别动态切换MongoDB数据源，同时也支持编程方式获取数据源。该框架提供了简单易用的注解配置，支持数据源路由、读写分离和数据库级别的切换功能。

## 核心特性

- **多数据源管理**：支持配置多个MongoDB连接
- **注解驱动**：通过 @MongoDS 注解实现声明式数据源切换
- **编程方式**：通过接口编程方式获取数据源
- **多种切换策略**：支持直接切换、路由切换、读写分离
- **数据库切换**：支持在同一MongoDB实例中切换不同数据库
- **SpEL表达式支持**：支持动态表达式计算数据源标识
- **自动配置**：Spring Boot自动配置，兼容 spring-boot-starter-data-mongodb，开箱即用

## 快速开始

### 1. 添加依赖

在您的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>io.github.taoganio</groupId>
    <artifactId>mongodb-provider-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置数据源

在 `application.yml` 中配置多MongoDB数据源：

```yaml
spring:
  datasource:
    mongodb:
      ds-aop: true
      strict: true
      primary: master
      definitions:
        master:
          host: localhost
          port: 27017
          username: admin
          password: password
          database: main_db
          authenticationDatabase: admin
        slave1:
          host: localhost
          port: 27018
          username: admin
          password: password
          database: backup_db
          authenticationDatabase: admin
        slave2:
          host: localhost
          port: 27019
          username: admin
          password: password
          database: log_db
          authenticationDatabase: admin
```

### 4. 配置项说明

#### 主配置项

| 配置项 | 是否必填 | 默认值 | 说明 |
|--------|----------|--------|------|
| `spring.datasource.jdbc.ds-aop` | 否 | true | 启动 MongoDS AOP 切面 |
| `spring.datasource.mongodb.strict` | 否 | false | 严格模式，为true时找不到数据源会抛出异常 |
| `spring.datasource.mongodb.primary` | 否 | 第一个定义的数据源 | 主数据源标识 |
| `spring.datasource.mongodb.definitions` | 是 | - | 数据源定义映射 |

#### 数据源定义配置项

| 配置项 | 是否必填 | 默认值 | 说明 |
|--------|----------|--------|------|
| `definitions.{key}.host` | 是 | - | MongoDB主机地址 |
| `definitions.{key}.port` | 否 | 27017 | MongoDB端口 |
| `definitions.{key}.username` | 否 | - | 用户名 |
| `definitions.{key}.password` | 否 | - | 密码 |
| `definitions.{key}.database` | 否 | - | 默认数据库名称 |
| `definitions.{key}.authenticationDatabase` | 否 | - | 认证数据库名称 |
| `definitions.{key}.uri` | 否 | - | MongoDB连接URI（优先级高于host/port等配置） |

#### 包含 org.springframework.boot.autoconfigure.mongo.MongoProperties 的所有属性


## 注解使用

### @MongoDS 注解

`@MongoDS` 注解用于在方法、接口方法、类及其父类级别指定要使用的数据源。

#### 注解参数

- `routing`: 是否启用路由模式（默认：false）
- `key`: 数据源标识或路由键（默认：空字符串）
- `database`: 数据库名称（默认：空字符串）
- `scope`: 读写范围（默认：UNKNOWN）

#### 使用示例

##### 1. 直接指定数据源

```java
@Service
public class UserService {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    // 使用 master 数据源
    @MongoDS(key = "'master'")
    public void queryFromMaster() {
        // 查询操作
    }
    
    // 使用 slave1 数据源
    @MongoDS(key = "'slave1'")
    public void queryFromSlave() {
        // 查询操作
    }
}
```

##### 2. 指定数据库

```java
@Service
public class LogService {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    // 使用 master 数据源，但切换到 log_db 数据库
    @MongoDS(key = "'master'", database = "'log_db'")
    public void queryFromLogDb() {
        // 查询操作
    }
    
    // 使用 slave1 数据源，切换到 audit_db 数据库
    @MongoDS(key = "'slave1'", database = "'audit_db'")
    public void queryFromAuditDb() {
        // 查询操作
    }
}
```

##### 3. 类级别注解

```java
@Service
@MongoDS(key = "'master'", database = "'user_db'")  // 整个类默认使用 master 数据源的 user_db 数据库
public class UserService {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public void defaultOperation() {
        // 使用类级别指定的数据源和数据库
    }
    
    // 方法级别覆盖类级别配置
    @MongoDS(key = "'slave1'", database = "'backup_db'")
    public void backupOperation() {
        // 使用 slave1 数据源的 backup_db 数据库
    }
}
```

## 数据源路由

### 配置路由规则

在 `application.yml` 中配置MongoDB数据源路由：

```yaml
spring:
  datasource:
    mongodb:
      # ... 数据源定义 ...
      
      routers:
        # 数据源路由配置
        datasource:
          enabled: true
          routes:
            user_service: master      # 用户服务使用 master
            log_service: slave1       # 日志服务使用 slave1
            audit_service: slave2     # 审计服务使用 slave2
            # 支持数组形式，表示多个数据源
            backup_service: [slave1, slave2]
```

### 使用路由

```java
@Service
public class BusinessService {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    // 使用路由键选择数据源
    @MongoDS(routing = true, key = "'user_service'")
    public List<User> getUsers() {
        return mongoTemplate.findAll(User.class);
    }
    
    @MongoDS(routing = true, key = "'log_service'")
    public List<LogEntry> getLogs() {
        return mongoTemplate.findAll(LogEntry.class);
    }
    
    @MongoDS(routing = true, key = "'audit_service'", database = "'audit_db'")
    public List<AuditLog> getAuditLogs() {
        return mongoTemplate.findAll(AuditLog.class);
    }
}
```

## 读写分离

### 配置读写分离

```yaml
spring:
  datasource:
    mongodb:
      # ... 数据源定义 ...
      
      routers:
        # 读写分离配置
        readwrite:
          enabled: true
          routes:
            user_service:
              read: slave1    # 读操作使用 slave1
              write: master   # 写操作使用 master
            log_service:
              read: slave2    # 读操作使用 slave2
              write: slave1   # 写操作使用 slave1
            audit_service:
              any: master     # 任意操作使用 master
            backup_service:
              read: slave1
              write: master
              any: slave2
```

### 使用读写分离

```java
@Service
public class UserService {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    // 读操作，自动路由到读数据源
    @MongoDS(routing = true, key = "'user_service'", scope = ReadWriteScope.READ)
    public List<User> getUsers() {
        return mongoTemplate.findAll(User.class);
    }
    
    // 写操作，自动路由到写数据源
    @MongoDS(routing = true, key = "'user_service'", scope = ReadWriteScope.WRITE)
    public void insertUser(User user) {
        mongoTemplate.save(user);
    }
    
    // 任意操作，根据配置使用指定数据源
    @MongoDS(routing = true, key = "'backup_service'", scope = ReadWriteScope.ANY)
    public void backupUsers() {
        List<User> users = mongoTemplate.findAll(User.class);
        // 备份逻辑
    }
}
```

## 编程方式获取数据源

除了使用注解方式，还可以通过编程方式获取数据源：

### 使用 MongoDataSourceProvider

```java
@Service
public class DataSourceService {
    
    @Autowired
    private MongoDataSourceProvider dataSourceProvider;
    
    public void useDataSource() {
        // 获取主数据源
        MongoDataSource primaryDataSource = dataSourceProvider.getPrimaryDataSource();
        
        // 根据key获取指定数据源
        MongoDataSource masterDataSource = dataSourceProvider.getDataSource("master");
        MongoDataSource slaveDataSource = dataSourceProvider.getDataSource("slave1");
        
        // 使用数据源进行操作
        MongoDatabase masterDb = masterDataSource.getDatabase();
        MongoDatabase slaveDb = slaveDataSource.getDatabase("custom_db");
    }
}
```

### 使用 MongoDataSourceRoutingProvider

```java
@Service
public class RoutingService {
    
    @Autowired
    private MongoDataSourceRoutingProvider routingProvider;
    
    public void useRoutingDataSource() {
        // 根据路由键获取数据源列表
        List<MongoDataSource> userDataSources = routingProvider.getDataSources("user_service");
        List<MongoDataSource> logDataSources = routingProvider.getDataSources("log_service");
        
        // 获取第一个数据源
        MongoDataSource firstDataSource = routingProvider.getDataSource("user_service");
        
        // 使用数据源进行操作
        if (firstDataSource != null) {
            MongoDatabase database = firstDataSource.getDatabase();
            // 执行操作
        }
    }
}
```

### 使用 MongoReadWriteDataSourceProvider

```java
@Service
public class ReadWriteService {
    
    @Autowired
    private MongoReadWriteDataSourceProvider readWriteProvider;
    
    public void useReadWriteDataSource() {
        // 根据key和读写范围获取数据源
        MongoDataSource readDataSource = readWriteProvider.getDataSource("user_service", ReadWriteScope.READ);
        MongoDataSource writeDataSource = readWriteProvider.getDataSource("user_service", ReadWriteScope.WRITE);
        
        // 使用数据源进行操作
        if (readDataSource != null) {
            MongoDatabase readDb = readDataSource.getDatabase();
            // 读操作
        }
        
        if (writeDataSource != null) {
            MongoDatabase writeDb = writeDataSource.getDatabase();
            // 写操作
        }
    }
}
```
## 最佳实践
- 为了更灵活的获取数据源，注解支持 SpEL 表达式，例如作者在实践中通过 @MongoDS(database = "'db_' + #month") 切换月库，固定数据源标识或路由键时，请使用单引号包裹字符串，如 `@MongoDS(key = "'db0'")`。