package io.github.taoganio.provider.test.service;

import io.github.taoganio.datasource.provider.ReadWriteScope;
import io.github.taoganio.mongodb.provider.spring.boot.MongoDS;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Service
public class MongodbSwitchService {

    private static final Logger log = LoggerFactory.getLogger(MongodbSwitchService.class);
    private final MongoOperations mongoOperations;
    private final MongodbSwitchNestedService nestedService;

    public MongodbSwitchService(MongoOperations mongoOperations,
                                MongodbSwitchNestedService nestedService) {
        this.mongoOperations = mongoOperations;
        this.nestedService = nestedService;
    }

    @MongoDS(routing = true, key = "'terminal'", database = "'terminalinfo'")
    public String getTerminfo() {
        String malicePicture = nestedService.getMalicePicture();
        log.debug(malicePicture);
        return mongoOperations.execute(d -> {
            MongoCollection<Document> collection = d.getCollection("terminalinfo");
            return collection.find(Filters.eq("_id", "50099920000009_600292B4AEE8")).first().toJson();
        });
    }

    @MongoDS(routing = true, key = "'cert'", database = "'certimage'", scope = ReadWriteScope.READ)
    public String getCertImage() {
        String qqFriend = nestedService.getQQFriend();
        log.debug(qqFriend);
        return mongoOperations.execute(d -> {
            MongoCollection<Document> collection = d.getCollection("fs.files");
            return collection.find(Filters.eq("_id", new ObjectId("683e60841e85500f906dbbed"))).first().toJson();
        });
    }
}
