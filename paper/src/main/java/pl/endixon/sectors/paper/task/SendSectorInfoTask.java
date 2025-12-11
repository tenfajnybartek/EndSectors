package pl.endixon.sectors.paper.task;

import org.bukkit.Bukkit;
import pl.endixon.sectors.common.packet.PacketChannel;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.redis.packet.PacketSectorInfo;
import pl.endixon.sectors.paper.util.TpsUtil;

public class SendSectorInfoTask implements Runnable {

    private final PaperSector paperSector;

    public SendSectorInfoTask(PaperSector paperSector) {
        this.paperSector = paperSector;
    }

    @Override
    public void run() {
        int online = Bukkit.getOnlinePlayers().size();
        int max = Bukkit.getMaxPlayers();
        float tps = (float) TpsUtil.getTPS();

        PacketSectorInfo info = new PacketSectorInfo(tps, online, max);
        paperSector.getRedisManager().publish(PacketChannel.SECTORS, info);
        paperSector.getRedisManager().publish(PacketChannel.QUEUE, info);
    }
}
