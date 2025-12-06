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


package pl.endixon.sectors.paper.task;

import org.bukkit.scheduler.BukkitRunnable;
import pl.endixon.sectors.common.packet.PacketChannel;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.redis.packet.PacketPlayerInfoRequest;
import pl.endixon.sectors.paper.user.UserManager;
import pl.endixon.sectors.paper.user.UserMongo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SendInfoPlayerTask extends BukkitRunnable {

    private final PaperSector paperSector;

    public SendInfoPlayerTask(PaperSector paperSector) {
        this.paperSector = paperSector;
    }

    @Override
    public void run() {
        CompletableFuture.runAsync(() -> {
            List<String> allNames = PaperSector.getInstance()
                    .getMongoManager()
                    .getUsersCollection()
                    .find()
                    .map(doc -> doc.getString("Name"))
                    .into(new java.util.ArrayList<>());

            List<CompletableFuture<UserMongo>> futures = allNames.stream()
                    .map(UserManager::getUser)
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            List<UserMongo> allUsers = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

            List<PacketPlayerInfoRequest.PlayerData> allPlayerData = allUsers.stream()
                    .map(user -> new PacketPlayerInfoRequest.PlayerData(
                            user.getName(),
                            user.getSectorName(),
                            user.isFirstJoin(),
                            user.getLastSectorTransfer(),
                            user.getLastTransferTimestamp(),
                            user.isTeleportingToSector(),
                            user.getFoodLevel(),
                            user.getExperience(),
                            user.getExperienceLevel(),
                            user.getFireTicks(),
                            user.isAllowFlight(),
                            user.isFlying(),
                            user.getPlayerGameMode()
                    ))
                    .collect(Collectors.toList());
            PacketPlayerInfoRequest packet = new PacketPlayerInfoRequest(allPlayerData);
            paperSector.getRedisManager().publish(PacketChannel.SECTORS, packet);
        });
    }

}

