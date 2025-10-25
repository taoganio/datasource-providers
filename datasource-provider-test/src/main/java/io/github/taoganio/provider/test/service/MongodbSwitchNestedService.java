package io.github.taoganio.provider.test.service;

import io.github.taoganio.mongodb.provider.spring.boot.MongoDS;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Service
@MongoDS(routing = true, key = "'qq'", database = "'qqfriend'")
public class MongodbSwitchNestedService {

    private final MongoOperations mongoOperations;

    public MongodbSwitchNestedService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @MongoDS(routing = true, key = "'malice'", database = "'malicepicture'")
    public String getMalicePicture() {
        return mongoOperations.execute(d -> {
            MongoCollection<Document> collection = d.getCollection("fs.files");
            return collection.find(Filters.eq("_id", new ObjectId("66602fbd1e854f17d462c65e"))).first().toJson();
        });
    }

    // @MongoDS(routing = true, key = "qq", database = "qqfriend")
    public String getQQFriend() {
        return mongoOperations.execute(d -> {
            MongoCollection<Document> collection = d.getCollection("qqfriend");
            return collection.find(Filters.eq("_id", "2864629491_815703630")).first().toJson();
        });
    }

    public String getQQFriend2() {
        return mongoOperations.execute(d -> {
            MongoCollection<Document> collection = d.getCollection("qqfriend");
            return collection.find(Filters.eq("_id", "2864629491_815703630")).first().toJson();
        });
    }
}
