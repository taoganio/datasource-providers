package io.github.taoganio.provider.test;

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
@MapperScan("io.github.taoganio.provider.test.mapper")
@EnableTransactionManagement
public class ProviderTestApplication {

    private static final Logger log = LoggerFactory.getLogger(ProviderTestApplication.class);

    private final DataSource dataSource;

    public ProviderTestApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void check() {
        log.debug("---- DataSource 实际类型 ----");
        log.debug(dataSource.getClass().getName());
    }

    public static void main(String[] args) {
        SpringApplication.run(ProviderTestApplication.class, args);
    }


}
