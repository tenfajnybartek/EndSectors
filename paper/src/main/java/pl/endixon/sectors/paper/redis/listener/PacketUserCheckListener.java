package pl.endixon.sectors.paper.redis.listener;

import org.bson.Document;
import org.bukkit.Bukkit;
import pl.endixon.sectors.common.packet.PacketChannel;
import pl.endixon.sectors.common.packet.object.PacketUserCheck;
import pl.endixon.sectors.common.redis.RedisPacketListener;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.user.UserManager;
import pl.endixon.sectors.paper.user.UserMongo;

public class PacketUserCheckListener extends RedisPacketListener<PacketUserCheck> {

    private final PaperSector paperSector;

    public PacketUserCheckListener(PaperSector paperSector) {
        super(PacketUserCheck.class);
        this.paperSector = paperSector;
    }

    @Override
    public void handle(PacketUserCheck packet) {
        String username = packet.getUsername();

        UserMongo cached = UserManager.getUsers().get(username.toLowerCase());
        if (cached != null) {
            PacketUserCheck response = new PacketUserCheck(username, true, cached.getSectorName());
            paperSector.getRedisManager().publish(PacketChannel.PAPER_TO_PROXY, response);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(paperSector, () -> {
            Document doc = paperSector.getMongoManager()
                    .getUsersCollection()
                    .find(new Document("Name", username))
                    .first();

            boolean exists = doc != null;
            String sector = exists ? doc.getString("sectorName") : null;
            PacketUserCheck response = new PacketUserCheck(username, exists, sector);
            paperSector.getRedisManager().publish(PacketChannel.PAPER_TO_PROXY, response);
        });
    }
}