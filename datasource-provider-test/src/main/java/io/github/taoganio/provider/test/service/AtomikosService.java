package io.github.taoganio.provider.test.service;

import io.github.taoganio.jdbc.provider.JdbcDataSourceProvider;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AtomikosService {

    private final JdbcOperations jdbcOperations1;
    private final JdbcOperations jdbcOperations2;

    public AtomikosService(JdbcDataSourceProvider jdbcDataSourceProvider) {
        this.jdbcOperations1 = new JdbcTemplate
                (Objects.requireNonNull(jdbcDataSourceProvider.getDataSource("db1")));
        this.jdbcOperations2 = new JdbcTemplate
                (Objects.requireNonNull(jdbcDataSourceProvider.getDataSource("db2")));
    }

    @Transactional(rollbackFor = Exception.class)
    public void update() {
        jdbcOperations1.update("INSERT INTO test.user(id, name) values (?,?)", 21, "dish21");
        jdbcOperations2.update("INSERT INTO test.customer(id, name, id_type, id_number) values (?,?,?,?)",
                3, "大侠", 111, "789473917832");
        if (true) {
            throw new RuntimeException("Rollback Test");
        }
    }

}
