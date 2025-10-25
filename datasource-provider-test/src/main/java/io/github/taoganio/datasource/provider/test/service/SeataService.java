package io.github.taoganio.datasource.provider.test.service;

import io.github.taoganio.datasource.provider.test.mapper.MybatisDataSourceMapper;
import io.github.taoganio.jdbc.provider.spring.boot.JdbcDS;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class SeataService {

    private final ApplicationContext context;
    private final MybatisDataSourceMapper mybatisDataSourceMapper;

    public SeataService(ApplicationContext context, MybatisDataSourceMapper mybatisDataSourceMapper) {
        this.context = context;
        this.mybatisDataSourceMapper = mybatisDataSourceMapper;
    }

    @GlobalTransactional(name = "mybatis-ds-test", rollbackFor = Exception.class)
    public void update() {
        SeataService bean = context.getBean(SeataService.class);
        bean.insertCustomer();
        bean.insertUser();
        if (true) {
            throw new RuntimeException("test rollback");
        }
    }

    @JdbcDS(routing = true, key = "'customer'")
    public void insertCustomer() {
        mybatisDataSourceMapper.insertCustomer();
    }

    @JdbcDS(routing = true, key = "'user'")
    public void insertUser() {
        mybatisDataSourceMapper.insertUser();
    }
}
