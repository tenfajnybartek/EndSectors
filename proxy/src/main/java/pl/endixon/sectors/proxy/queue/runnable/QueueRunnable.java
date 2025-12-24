package pl.endixon.sectors.proxy.queue.runnable;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import pl.endixon.sectors.common.sector.SectorData;
import pl.endixon.sectors.common.util.Logger;
import pl.endixon.sectors.proxy.VelocitySectorPlugin;
import pl.endixon.sectors.proxy.queue.Queue;
import pl.endixon.sectors.proxy.queue.QueueManager;
import pl.endixon.sectors.proxy.manager.SectorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class QueueRunnable implements Runnable {

    private static final String QUEUE_SERVER_NAME = "queue";
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final Component TITLE_CACHE = MM.deserialize("<gradient:#00d2ff:#3a7bd5><bold>KOLEJKA</bold></gradient>");
    private static final Map<String, Component> SUBTITLE_CACHE = new ConcurrentHashMap<>();

    private final ProxyServer proxyServer = VelocitySectorPlugin.getInstance().getServer();
    private final QueueManager queueManager = VelocitySectorPlugin.getInstance().getQueueManager();
    private final SectorManager sectorManager = VelocitySectorPlugin.getInstance().getSectorManager();

    private static final int MAX_RELEASE_PER_TICK = 3;

    @Override
    public void run() {
        for (final Queue queue : this.queueManager.getMap().values()) {
            this.processQueue(queue);
        }
    }

    private void processQueue(final Queue queue) {
        final List<Player> allPlayers = queue.getPlayers();
        if (allPlayers.isEmpty()) {
            return;
        }

        this.cleanupQueue(allPlayers);
        if (allPlayers.isEmpty()) {
            return;
        }

        final String sectorName = queue.getSector();
        final SectorData sectorData = this.sectorManager.getSectorData(sectorName);
        final boolean online = (sectorData != null && sectorData.isOnline());
        final List<Player> sortedQueue = this.sortQueueByPriority(allPlayers);
        this.processPlayersInQueue(sortedQueue, allPlayers, sectorName, sectorData, online);
    }

    private void cleanupQueue(final List<Player> players) {
        players.removeIf(player -> {
            if (player == null || !player.isActive()) {
                return true;
            }

            return player.getCurrentServer().map(server -> {
                final String currentServerName = server.getServerInfo().getName();
                return !currentServerName.equalsIgnoreCase(QUEUE_SERVER_NAME);
            }).orElse(false);
        });
    }

    private void processPlayersInQueue(final List<Player> sortedQueue, final List<Player> originalList, final String sectorName, final SectorData sectorData, final boolean online) {

        int releasedThisTick = 0;
        final int totalInQueue = sortedQueue.size();
        final List<Player> toRemoveFromQueue = new ArrayList<>();

        int virtualOccupiedSlots = (sectorData != null) ? sectorData.getPlayerCount() : 0;
        int maxSlots = (sectorData != null) ? sectorData.getMaxPlayers() : 0;

        for (int i = 0; i < totalInQueue; i++) {
            final Player player = sortedQueue.get(i);
            final int positionInQueue = i + 1;
            boolean isFull = virtualOccupiedSlots >= maxSlots;

            if (online && !isFull && releasedThisTick < MAX_RELEASE_PER_TICK) {
                this.sendPlayerToSector(player, sectorName, toRemoveFromQueue);
                releasedThisTick++;
                virtualOccupiedSlots++;
                continue;
            }
            this.dispatchTitle(player, sectorName, online, positionInQueue, totalInQueue, isFull);
        }

        if (!toRemoveFromQueue.isEmpty()) {
            originalList.removeAll(toRemoveFromQueue);
        }
    }

    private void sendPlayerToSector(final Player player, final String sectorName, final List<Player> toRemove) {
        this.proxyServer.getServer(sectorName).ifPresent(server -> {
            Logger.info(String.format("[Queue] Release: %s -> %s", player.getUsername(), sectorName));
            player.createConnectionRequest(server).fireAndForget();
        });
    }

    private List<Player> sortQueueByPriority(final List<Player> players) {
        final List<Player> admins = new ArrayList<>();
        final List<Player> vips = new ArrayList<>();
        final List<Player> regulars = new ArrayList<>();

        for (final Player p : players) {
            if (p.hasPermission("queue.admin")) {
                admins.add(p);
            } else if (p.hasPermission("queue.vip")) {
                vips.add(p);
            } else {
                regulars.add(p);
            }
        }

        final List<Player> sorted = new ArrayList<>(admins.size() + vips.size() + regulars.size());
        sorted.addAll(admins);
        sorted.addAll(vips);
        sorted.addAll(regulars);
        return sorted;
    }

    private void dispatchTitle(final Player player, final String sector, final boolean online, final int pos, final int total, final boolean full) {
        final String cacheKey = String.format("q_sys_%s_%b_%b_%d_%d", sector, online, full, pos, total);
        final Component subtitle = SUBTITLE_CACHE.computeIfAbsent(cacheKey, k -> this.buildSubtitle(sector, online, pos, total, full));
        player.showTitle(Title.title(TITLE_CACHE, subtitle));
    }

    private Component buildSubtitle(final String sector, final boolean online, final int pos, final int total, final boolean full) {
        if (!online) {
            return MM.deserialize("<gradient:#ff4b2b:#ff416c>Sektor <white>" + sector + "</white> jest obecnie <bold>OFFLINE</bold></gradient>");
        }

        if (full) {
            return MM.deserialize("<gradient:#f8ff00:#f8ff00>Sektor <white>" + sector + "</white> jest <bold>PELNY</bold></gradient> <gray>(" + pos + "/" + total + ")</gray>");
        }

        return MM.deserialize("<gradient:#e0e0e0:#ffffff>Twoja pozycja: </gradient>" +
                "<gradient:#00d2ff:#3a7bd5><bold>" + pos + "</bold></gradient>" +
                "<white><bold> / </bold></white>" +
                "<gradient:#3a7bd5:#00d2ff>" + total + "</gradient>");
    }
}