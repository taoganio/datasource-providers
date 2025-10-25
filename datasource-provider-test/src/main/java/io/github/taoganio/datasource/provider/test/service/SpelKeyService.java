package io.github.taoganio.datasource.provider.test.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import io.github.taoganio.jdbc.provider.spring.boot.JdbcDS;
import io.github.taoganio.mongodb.provider.spring.boot.MongoDS;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

@Component
public class SpelKeyService {

    private static final Logger log = LoggerFactory.getLogger(SpelKeyService.class);
    private final JdbcOperations jdbcOperations;
    private final MongoOperations mongoOperations;

    public SpelKeyService(JdbcOperations jdbcOperations, MongoOperations mongoOperations) {
        this.jdbcOperations = jdbcOperations;
        this.mongoOperations = mongoOperations;
    }

    @JdbcDS("#db % 2 == 0 ? 'db1' : 'db2'")
    public void selectByDb(int db) {
        jdbcOperations.query("SELECT * FROM test.user",
                r -> {
                    log.debug(r.getString("name"));
                });
    }

    @MongoDS(key = "#root.args[0]", database = "'cameraimage_' + #p1")
    public void selectMongoByDb(String db, String month) {
        mongoOperations.execute(d -> {
            MongoCollection<Document> collection = d.getCollection("fs.files");
            String json = collection.find(Filters.eq("_id", new ObjectId("68f1d3dcbd53874ce21919b1"))).first().toJson();
            log.debug(json);
            return null;
        });
    }
}
