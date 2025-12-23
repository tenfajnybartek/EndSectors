package pl.endixon.sectors.common.redis;

import com.google.gson.Gson;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.resource.DefaultClientResources;
import java.io.Serializable;
import java.nio.CharBuffer;
import java.util.*;
import java.util.function.Consumer;
import pl.endixon.sectors.common.packet.Packet;
import pl.endixon.sectors.common.packet.PacketListener;
import pl.endixon.sectors.common.util.Logger;

public class RedisManager {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private StatefulRedisPubSubConnection<String, String> pubSubConnection;


    private RedisCommands<String, String> syncCommands;
    private RedisAsyncCommands<String, String> asyncCommands;

    private final Gson gson = new Gson();
    private final Set<String> onlinePlayers = Collections.synchronizedSet(new HashSet<>());

    public void initialize(String host, int port, String password) {
        try {
            RedisURI uri = RedisURI.builder()
                    .withHost(host)
                    .withPort(port)
                    .withPassword(CharBuffer.wrap(password))
                    .withDatabase(0)
                    .build();

            DefaultClientResources resources = DefaultClientResources.builder()
                    .ioThreadPoolSize(4)
                    .build();

            this.redisClient = RedisClient.create(resources, uri);

            ClientOptions options = ClientOptions.builder()
                    .autoReconnect(true)
                    .publishOnScheduler(true)
                    .build();
            this.redisClient.setOptions(options);

            this.connection = redisClient.connect();
            this.syncCommands = connection.sync();
            this.asyncCommands = connection.async();

            this.pubSubConnection = redisClient.connectPubSub();

            Logger.info("RedisManager został pomyślnie zainicjalizowany (Sync/Async).");

        } catch (Exception e) {
            Logger.info("Błąd inicjalizacji Redis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public <T extends Serializable> void subscribe(String channel, PacketListener<T> listener, Class<T> type) {
        if (pubSubConnection == null) return;
        try {
            pubSubConnection.addListener(new RedisPubSubAdapter<>() {
                @Override
                public void message(String ch, String msg) {
                    if (ch.equals(channel)) {
                        try {
                            T packet = gson.fromJson(msg, type);
                            listener.handle(packet);
                        } catch (Exception e) {
                            Logger.info("Błąd w PubSub listenerze: " + e.getMessage());
                        }
                    }
                }
            });

            pubSubConnection.async().subscribe(channel);
        } catch (Exception e) {
            Logger.info("Błąd podczas subskrypcji Redis: " + e.getMessage());
        }
    }

    public void publish(String channel, Packet packet) {
        if (asyncCommands == null) return;
        String json = gson.toJson(packet);
        asyncCommands.publish(channel, json);
    }

    public void hset(String key, Map<String, String> map) {
        if (map == null || map.isEmpty() || asyncCommands == null) return;
        asyncCommands.hset(key, map);
    }

    public Map<String, String> hgetAll(String key) {
        if (key == null || syncCommands == null) {
            return Collections.emptyMap();
        }
        try {
            // Używamy natywnych komend synchronicznych - koniec z .async().get()!
            Map<String, String> result = syncCommands.hgetall(key);
            return (result == null) ? Collections.emptyMap() : result;
        } catch (Exception e) {
            Logger.info("Redis hgetAll failed for key: " + key + " | " + e.getMessage());
            return Collections.emptyMap();
        }
    }


    public void addOnlinePlayer(String name) {
        if (name == null || name.isEmpty() || asyncCommands == null) return;
        onlinePlayers.add(name);
        asyncCommands.sadd("online_players", name);
    }

    public void removeOnlinePlayer(String name) {
        if (name == null || name.isEmpty() || asyncCommands == null) return;
        onlinePlayers.remove(name);
        asyncCommands.srem("online_players", name);
    }

    public void getOnlinePlayers(Consumer<List<String>> callback) {
        if (asyncCommands == null) return;
        asyncCommands.smembers("online_players")
                .thenAccept(players -> callback.accept(new ArrayList<>(players)));
    }

    public void isPlayerOnline(String name, Consumer<Boolean> callback) {
        if (asyncCommands == null) return;
        asyncCommands.sismember("online_players", name).thenAccept(callback);
    }

    public void shutdown() {
        if (pubSubConnection != null) pubSubConnection.close();
        if (connection != null) connection.close();
        if (redisClient != null) redisClient.shutdown();
    }
}