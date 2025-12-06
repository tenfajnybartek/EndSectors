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


package pl.endixon.sectors.proxy.listener;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import org.bson.Document;
import pl.endixon.sectors.common.redis.MongoExecutor;
import pl.endixon.sectors.common.redis.MongoManager;
import pl.endixon.sectors.proxy.VelocitySectorPlugin;
import pl.endixon.sectors.proxy.manager.TeleportationManager;
import pl.endixon.sectors.proxy.queue.Queue;
import pl.endixon.sectors.proxy.queue.QueueManager;


import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class LastSectorConnectListener {

    private final VelocitySectorPlugin plugin;
    private final MongoManager mongo;
    private final TeleportationManager teleportManager;

    @Inject
    public LastSectorConnectListener(VelocitySectorPlugin plugin, TeleportationManager teleportManager) {
        this.plugin = plugin;
        this.mongo = plugin.getMongoManager();
        this.teleportManager = teleportManager;
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        String connectedServer = event.getServer().getServerInfo().getName();

        if (!connectedServer.equalsIgnoreCase("queue")) return;
        QueueManager queueService = plugin.getQueueManager();
        queueService.findQueueByPlayer(player).ifPresent(queue -> queue.removePlayer(player));
        pollForUser(player, queueService);
    }

    private void pollForUser(Player player, QueueManager queueService) {
        CompletableFuture.runAsync(() -> {
            Document doc = mongo.getUsersCollection()
                    .find(new Document("Name", player.getUsername()))
                    .first();

            if (doc == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                pollForUser(player, queueService);
                return;
            }

            String lastSector = doc.getString("sectorName");
            if (lastSector == null) return;

            Queue queue = queueService.getMap().computeIfAbsent(lastSector, Queue::new);
            queue.addPlayer(player);

        }, MongoExecutor.EXECUTOR);
    }
}