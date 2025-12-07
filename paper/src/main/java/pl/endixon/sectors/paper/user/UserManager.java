package pl.endixon.sectors.paper.user;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.NonNull;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.endixon.sectors.common.redis.MongoExecutor;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.util.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    @Getter
    private static final ConcurrentHashMap<String, UserMongo> users = new ConcurrentHashMap<>();

    public static UserMongo getUser(@NonNull Player player) {
        return users.computeIfAbsent(player.getName().toLowerCase(), k -> new UserMongo(player));
    }




    public static CompletableFuture<UserMongo> getUser(@NonNull String name) {
        String key = name.toLowerCase();
        UserMongo cached = users.get(key);

        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> collection = PaperSector.getInstance()
                    .getMongoManager()
                    .getUsersCollection();
            try {
                Document doc = collection.find(Filters.eq("Name", name)).first();

                if (doc == null) {
                    return cached;
                }

                UserMongo result;
                if (cached == null) {
                    result = new UserMongo(doc);
                    users.put(key, result);
                } else {
                    result = cached;
                    if (cached.needsUpdate(doc)) {
                        cached.updateFromMongo(doc);
                    }
                }

                return result;
            } catch (Exception e) {
                Logger.info("[UserMongo] Błąd przy ładowaniu danych z Mongo dla: " + name + " -> " + e.getMessage());
                return cached;
            }
        }, MongoExecutor.EXECUTOR);
    }
}
