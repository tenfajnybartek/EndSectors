package pl.endixon.sectors.paper.task;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import pl.endixon.sectors.paper.manager.SectorManager;
import pl.endixon.sectors.paper.sector.Sector;

public class FakeWorldBorderShrinkTask extends BukkitRunnable {

    private final SectorManager sectorManager;
    private final double startSize;
    private final double endSize;
    private final long durationTicks;

    public FakeWorldBorderShrinkTask(SectorManager sectorManager, double startSize, double endSize, long durationTicks) {
        this.sectorManager = sectorManager;
        this.startSize = startSize;
        this.endSize = endSize;
        this.durationTicks = durationTicks;
    }

    private double currentSize;
    private long ticksPassed = 0;

    @Override
    public void run() {
        Sector sector = sectorManager.getCurrentSector();
        if (sector == null) return;

        currentSize = startSize - ((startSize - endSize) * ticksPassed / (double) durationTicks);

        // liczymy środek sektora
        double centerX = (sector.getFirstCorner().getPosX() + sector.getSecondCorner().getPosX()) / 2.0;
        double centerZ = (sector.getFirstCorner().getPosZ() + sector.getSecondCorner().getPosZ()) / 2.0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().getName().equals(sector.getWorldName())) continue;

            try {
                ProtocolManager manager = ProtocolLibrary.getProtocolManager();

                // fake center
                PacketContainer centerPacket = new PacketContainer(PacketType.Play.Server.SET_BORDER_CENTER);
                centerPacket.getWorldBorderActions().write(0, EnumWrappers.WorldBorderAction.SET_CENTER);
                centerPacket.getDoubles().write(0, centerX);
                centerPacket.getDoubles().write(1, centerZ);
                manager.sendServerPacket(player, centerPacket);

                // fake lerp size
                PacketContainer sizePacket = new PacketContainer(PacketType.Play.Server.SET_BORDER_LERP_SIZE);
                sizePacket.getWorldBorderActions().write(0, EnumWrappers.WorldBorderAction.LERP_SIZE);
                sizePacket.getDoubles().write(0, currentSize); // start size
                sizePacket.getDoubles().write(1, currentSize); // end size – po ticku animacja będzie płynna
                sizePacket.getLongs().write(9999999, 999999999999999L); // animacja w ms
                manager.sendServerPacket(player, sizePacket);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ticksPassed++;
        if (ticksPassed > durationTicks) cancel(); // kończymy po animacji
    }
}
