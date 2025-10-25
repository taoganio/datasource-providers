package io.github.taoganio.provider.test.service;

import io.github.taoganio.datasource.provider.ReadWriteScope;
import io.github.taoganio.jdbc.provider.spring.boot.JdbcDS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

@Service
public class JdbcService {

    private static final Logger log = LoggerFactory.getLogger(JdbcService.class);
    private final JdbcOperations jdbcOperations;

    public JdbcService(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @JdbcDS(key = "'db1'")
    public void selectUser() {
        jdbcOperations.query("SELECT * FROM test.user FETCH FIRST 10 ROWS ONLY",
                r -> {
                    int id = r.getInt("id");
                    String name = r.getString("name");
                    log.debug("id: {}, name: {}", id, name);
                });
    }

    @JdbcDS(routing = true, key = "'customer'")
    public void selectCustomer() {
        jdbcOperations.query("SELECT * FROM test.customer FETCH FIRST 10 ROWS ONLY",
                r -> {
                    String idNumber = r.getString("id_number");
                    log.debug("idNumber: {}", idNumber);
                });
    }

    @JdbcDS(routing = true, key = "'site'", scope = ReadWriteScope.READ)
    public void selectSite() {
        jdbcOperations.query("SELECT * FROM test.site FETCH FIRST 10 ROWS ONLY",
                r -> {
                    String id = r.getString("id");
                    String title = r.getString("title");
                    log.debug("id: {}, title: {}", id, title);
                });
    }

}
