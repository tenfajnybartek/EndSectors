package pl.endixon.sectors.tools.manager;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import pl.endixon.sectors.tools.utils.LoggerUtil;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Getter
public class MongoManager {

    private static final int CURRENT_SCHEMA_VERSION = 1;
    private MongoClient client;
    private MongoDatabase database;

    public void connect(String uri, String databaseName) {
        LoggerUtil.info("Initializing MongoDB connection provider...");

        try {
            CodecRegistry pojoCodecRegistry = fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(uri))
                    .codecRegistry(pojoCodecRegistry)
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .applicationName("EndSectors-Tools")
                    .build();

            this.client = MongoClients.create(settings);
            this.database = client.getDatabase(databaseName);
            LoggerUtil.info("Successfully established connection to database: {}", databaseName);
            this.handleSchemaMigration();

        } catch (MongoException e) {
            LoggerUtil.error("--------------------------------------------------");
            LoggerUtil.error("CRITICAL: MongoDB Authentication or Connectivity failure!");
            LoggerUtil.error("Details: " + e.getMessage());
            LoggerUtil.error("--------------------------------------------------");
            this.disconnect();
        }
    }

    private void handleSchemaMigration() {
        MongoCollection<Document> metadata = database.getCollection("_metadata");
        Document versionDoc = metadata.find(eq("_id", "schema_version")).first();

        int dbVersion = (versionDoc != null) ? versionDoc.getInteger("version", 0) : 0;

        if (dbVersion < CURRENT_SCHEMA_VERSION) {
            LoggerUtil.warn("==================================================");
            LoggerUtil.warn("SCHEMA CHANGE DETECTED!");
            LoggerUtil.warn("Code Version: " + CURRENT_SCHEMA_VERSION + " | DB Version: " + dbVersion);
            LoggerUtil.warn("Executing FULL DATABASE WIPE to prevent conflicts...");
            LoggerUtil.warn("==================================================");

            for (String collectionName : database.listCollectionNames()) {
                database.getCollection(collectionName).drop();
                LoggerUtil.info("Dropped collection: " + collectionName);
            }

            Document newVersionDoc = new Document("_id", "schema_version").append("version", CURRENT_SCHEMA_VERSION).append("updatedAt", System.currentTimeMillis());

            metadata.replaceOne(eq("_id", "schema_version"), newVersionDoc, new ReplaceOptions().upsert(true));

            LoggerUtil.info("Database migration completed. New schema version set to: " + CURRENT_SCHEMA_VERSION);
        } else {
            LoggerUtil.info("Database schema is up to date (v" + dbVersion + "). No wipe needed.");
        }
    }

    public void disconnect() {
        if (client != null) {
            client.close();
        }
    }
}