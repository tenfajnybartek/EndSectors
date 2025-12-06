/*
 *
 *  EndSectors  Non-Commercial License
 *  (c) 2025 Endixon
 *
 *  Permission is granted to use, copy, and
 *  modify this software **only** for personal
 *  or educational purposes.
 *
 *   Commercial use, redistribution, claiming
 *  this work as your own, or copying code
 *  without explicit permission is strictly
 *  prohibited.
 *
 *  Visit https://github.com/Endixon/EndSectors
 *  for more info.
 *
 */

package pl.endixon.sectors.common.redis;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoManager {

    private final MongoCollection<Document> users;

    public MongoManager() {
        MongoClient client = new MongoClient("localhost", 27017);
        MongoDatabase db = client.getDatabase("endsectors");
        this.users = db.getCollection("users");
    }

    public Document getUserDoc(String name) {
        return users.find(new Document("_id", name)).first();
    }

    public MongoCollection<Document> getUsersCollection() {
        return this.users;
    }

    public void updateUserField(String name, String key, Object value) {
        users.updateOne(
                new Document("_id", name),
                new Document("$set", new Document(key, value))
        );
    }


    public void saveUserDoc(String name, Document doc) {
        doc.put("_id", name);
        users.insertOne(doc);
    }
}

