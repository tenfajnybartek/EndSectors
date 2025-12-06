package pl.endixon.sectors.paper.task;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;
import pl.endixon.sectors.common.util.Corner;
import pl.endixon.sectors.paper.sector.Sector;
import pl.endixon.sectors.paper.sector.SectorManager;

public class SectorWorldBorderTask extends BukkitRunnable {

    private final SectorManager sectorManager;

    public SectorWorldBorderTask(SectorManager sectorManager) {
        this.sectorManager = sectorManager;
    }

    @Override
    public void run() {
        Sector sector = sectorManager.getCurrentSector();
        if (sector == null) return;
        Corner c1 = sector.getFirstCorner();
        Corner c2 = sector.getSecondCorner();

        double centerX = (c1.getPosX() + c2.getPosX()) / 2.0 + 0.5;
        double centerZ = (c1.getPosZ() + c2.getPosZ()) / 2.0 + 0.5;
        double diameter = Math.abs(c2.getPosX() - c1.getPosX()) + 1.5;

        World world = Bukkit.getWorld(sector.getWorldName());
        if (world == null) return;

        WorldBorder border = world.getWorldBorder();
        border.setCenter(centerX, centerZ);
        border.setSize(diameter);
    }
}
