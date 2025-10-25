# DataSource Providers

一个基于Spring Boot的多数据源管理框架，支持JDBC和MongoDB的动态数据源切换。

## 项目概述

DataSource Providers 是一个多数据源管理框架，旨在简化Spring Boot应用中的多数据源配置和动态切换。该框架通过注解驱动的方式，为开发者提供了声明式的数据源切换能力，同时支持复杂的业务场景如读写分离、数据源路由等。

## 项目结构

```
datasource-providers/
├── datasource-provider/                           # 核心数据源提供者接口
├── datasource-provider-spring-boot-common/        # Spring Boot通用组件
├── jdbc-datasource-provider/                      # JDBC数据源提供者实现
├── jdbc-datasource-provider-spring-boot-starter/  # JDBC Spring Boot Starter
├── mongodb-datasource-provider/                   # MongoDB数据源提供者实现
├── mongodb-datasource-provider-spring-boot-starter/ # MongoDB Spring Boot Starter
└── datasource-provider-test/                      # 测试模块
```

## 核心功能

### 动态数据源切换
框架基于ThreadLocal实现线程安全的数据源切换机制，支持在运行时动态选择不同的数据源。每个线程可以独立维护自己的数据源上下文，确保多线程环境下的数据一致性。

### 注解驱动开发
通过注解，开发者可以在方法或类级别声明式地指定数据源切换策略。框架基于Spring AOP实现无侵入式的数据源切换，无需修改业务逻辑代码。

### 多种切换策略
- **直接切换**：通过数据源标识直接指定目标数据源
- **路由切换**：基于业务规则动态选择数据源
- **读写分离**：根据操作类型（读/写）选择数据源

### SpEL表达式支持
框架集成了Spring Expression Language（SpEL），支持动态表达式计算数据源标识。开发者可以根据方法参数、业务逻辑等动态决定使用哪个数据源。

### 自动配置
基于Spring Boot的自动配置机制，框架能够自动检测配置并启用相关功能，实现极少配置开箱即用。


## 业务场景支持

### 多租户系统
基于租户ID的数据源路由，不同租户使用不同的数据源，实现数据隔离。

### 分库分表
基于业务规则的数据源选择。

### 多环境部署
开发、测试、生产环境的数据源管理，支持环境隔离。

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目。
