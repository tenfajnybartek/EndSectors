package pl.endixon.sectors.tools.manager;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.*;

@Getter
public class MongoManager {

    private MongoClient client;
    private MongoDatabase database;

    public void connect(String uri, String databaseName) {
        var pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        client = MongoClients.create(uri);
        database = client.getDatabase(databaseName)
                .withCodecRegistry(pojoCodecRegistry);
    }

    public void disconnect() {
        if (client != null) {
            client.close();
        }
    }
}
