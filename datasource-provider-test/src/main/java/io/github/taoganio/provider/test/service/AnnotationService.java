package io.github.taoganio.provider.test.service;

import io.github.taoganio.jdbc.provider.spring.boot.JdbcDS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

@Component
@JdbcDS(key = "'db1'")
public class AnnotationService {

    private static final Logger log = LoggerFactory.getLogger(AnnotationService.class);
    private final ApplicationContext context;
    private final JdbcOperations jdbcOperations;
    private final MongoOperations mongoOperations;

    public AnnotationService(ApplicationContext context, JdbcOperations jdbcOperations, MongoOperations mongoOperations) {
        this.context = context;
        this.jdbcOperations = jdbcOperations;
        this.mongoOperations = mongoOperations;
    }

    @JdbcDS(key = "'db2'")
    public void selectUser1() {
        context.getBean(AnnotationService.class).selectUser2();
        internalSelectUser();
    }

    public void selectUser2() {
        internalSelectUser();
    }

    private void internalSelectUser() {
        jdbcOperations.query("SELECT * FROM test.user FETCH FIRST 10 ROWS ONLY",
                r -> {
                    int id = r.getInt("id");
                    String name = r.getString("name");
                    log.debug("id: {}, name: {}", id, name);
                });
    }
}
