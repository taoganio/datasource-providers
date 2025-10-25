package io.github.taoganio.datasource.provider.test;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@SpringBootApplication
@EnableCaching
@MapperScan("io.github.taoganio.datasource.provider.test.mapper")
@EnableTransactionManagement
public class DataSourceProviderTestApplication {

    private static final Logger log = LoggerFactory.getLogger(DataSourceProviderTestApplication.class);

    private final DataSource dataSource;

    public DataSourceProviderTestApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void check() {
        log.debug("---- DataSource 实际类型 ----");
        log.debug(dataSource.getClass().getName());
    }

    public static void main(String[] args) {
        SpringApplication.run(DataSourceProviderTestApplication.class, args);
    }


}
