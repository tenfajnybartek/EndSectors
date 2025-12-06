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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.flogger.Flogger;
import org.redisson.Redisson;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import pl.endixon.sectors.common.packet.Packet;
import pl.endixon.sectors.common.util.Logger;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RedisManager {

    @Getter
    private static RedisManager instance;

    private final ObjectMapper mapper = new ObjectMapper();

    private final RedissonClient redisson;
    private final RSet<String> onlinePlayersSet;

    private String packetSender;

    public RedisManager() {
        this("localhost", 6379, "");
    }

    public RedisManager(String address, int port, String password) {
        if (instance != null) {
            throw new RuntimeException("Only one RedisManager instance can exist at once");
        }
        instance = this;

        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress("redis://" + address + ":" + port);

        if (!password.isEmpty()) {
            serverConfig.setPassword(password);
        }

        BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("pl.endixon.sectors.common")
                .allowIfSubType("pl.endixon.sectors.paper")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setTypeFactory(mapper.getTypeFactory().withClassLoader(this.getClass().getClassLoader()));

        config.setCodec(new JsonJacksonCodec(mapper));

        this.redisson = Redisson.create(config);
        this.onlinePlayersSet = this.redisson.getSet("onlinePlayers", StringCodec.INSTANCE);
    }


    public void shutdown() {
        this.redisson.shutdown();
    }



    public void publish(String channel, Packet packet) {
        redisson.getTopic(channel).publishAsync(packet)
                .thenAccept(count -> {
                })
                .exceptionally(ex -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed to publish packet to channel ").append(channel).append(": ").append(ex.toString()).append("\n");
                    for (StackTraceElement element : ex.getStackTrace()) {
                        sb.append("    at ").append(element.toString()).append("\n");
                    }
                    Logger.error(sb.toString());
                    return null;
                });
    }

    public <T extends Packet> void subscribe(RedisPacketListener<T> listener) {
        this.subscribe(packetSender, listener);
    }

    public <T extends Packet> void subscribe(String channel, RedisPacketListener<T> listener) {
        this.redisson.getTopic(channel).addListener(listener.getPacketClass(), listener);
    }

    public void addOnlinePlayer(String playerName) {
        if (playerName != null && !playerName.isEmpty()) {
            onlinePlayersSet.add(playerName);
        }
    }

    public void removeOnlinePlayer(String playerName) {
        if (playerName != null && !playerName.isEmpty()) {
            onlinePlayersSet.remove(playerName);
        }
    }

    public List<String> getOnlinePlayers() {
        return new ArrayList<>(onlinePlayersSet.readAll());
    }

    public boolean isPlayerOnline(String playerName) {
        return onlinePlayersSet.contains(playerName);
    }
}

