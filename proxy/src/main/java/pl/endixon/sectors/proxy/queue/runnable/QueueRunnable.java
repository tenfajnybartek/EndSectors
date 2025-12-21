package pl.endixon.sectors.proxy.queue.runnable;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.Optional;
import java.util.UUID;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import pl.endixon.sectors.common.sector.SectorData;
import pl.endixon.sectors.common.util.ChatUtil;
import pl.endixon.sectors.proxy.VelocitySectorPlugin;
import pl.endixon.sectors.proxy.manager.SectorManager;
import pl.endixon.sectors.proxy.queue.Queue;
import pl.endixon.sectors.proxy.queue.QueueManager;
import pl.endixon.sectors.proxy.user.RedisUserService;

public class QueueRunnable implements Runnable {

    private final ProxyServer proxyServer = VelocitySectorPlugin.getInstance().getServer();
    private final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacy(LegacyComponentSerializer.SECTION_CHAR);
    private final QueueManager queueService = VelocitySectorPlugin.getInstance().getQueueManager();
    private final SectorManager sectorManager = VelocitySectorPlugin.getInstance().getSectorManager();
    private final RedisUserService redisUserService = new RedisUserService(VelocitySectorPlugin.getInstance());

    @Override
    public void run() {
        for (Queue queue : queueService.getMap().values()) {

            if (queue.getPlayers().isEmpty()) continue;


            for (UUID uuid : queue.getPlayers().toArray(new UUID[0])) {
                Optional<Player> optionalPlayer = proxyServer.getPlayer(uuid);
                if (optionalPlayer.isEmpty()) {
                    queue.removePlayer(uuid);
                    continue;
                }

                Player player = optionalPlayer.get();

                if (player.getCurrentServer().isEmpty() || !player.getCurrentServer().get().getServerInfo().getName().equalsIgnoreCase("queue")) {
                    queue.removePlayer(uuid);
                    continue;
                }

                Optional<String> optionalSectorName = redisUserService.getSectorName(player.getUsername());
                String lastSectorName = optionalSectorName.orElse(queue.getSector());
                SectorData sector = null;

                if (lastSectorName != null && !lastSectorName.equalsIgnoreCase("null")) {
                    sector = sectorManager.getSectorData(lastSectorName);
                }

                if (sector == null || !sector.isOnline()) {
                    Optional<SectorData> randomOnline = sectorManager.getRandomNonQueueSector();
                    if (randomOnline.isPresent()) {
                        sector = randomOnline.get();
                        lastSectorName = sector.getName();
                        queue.setSector(lastSectorName);
                        redisUserService.setSectorName(player.getUsername(), lastSectorName);
                    } else {

                        continue;
                    }
                }

                proxyServer.getServer(lastSectorName).ifPresent(server -> player.createConnectionRequest(server).fireAndForget());

                int idx = queue.getPlayers().indexOf(uuid) + 1;
                int totalPlayers = queue.getPlayers().size();
                String subtitle = String.format("&#FFFFFFJesteś aktualnie w kolejce &#FFAA00: &#FFDD55%d &#FFFFFF/ &#55FFDD%d", idx, totalPlayers
                );

                player.showTitle(Title.title(
                        LEGACY_SERIALIZER.deserialize(ChatUtil.fixHexColors("&#00FFFF➤ &#FFD700&lKolejka")),
                        LEGACY_SERIALIZER.deserialize(ChatUtil.fixHexColors(subtitle))
                ));


            }
        }
    }
}
