/*
 *
 * EndSectors – Non-Commercial License
 * (c) 2025 Endixon
 *
 * Permission is granted to use, copy, and
 * modify this software **only** for personal
 * or educational purposes.
 *
 * Commercial use, redistribution, claiming
 * this work as your own, or copying code
 * without explicit permission is strictly
 * prohibited.
 *
 * Visit https://github.com/Endixon/EndSectors
 * for more info.
 *
 */

package pl.endixon.sectors.paper.nats.listener;

import org.bukkit.Bukkit;
import pl.endixon.sectors.common.packet.PacketListener;
import pl.endixon.sectors.common.packet.object.PacketSectorConnected;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.sector.Sector;
import pl.endixon.sectors.paper.util.MessagesUtil;
import pl.endixon.sectors.paper.util.LoggerUtil;

public class PacketSectorConnectedPacketListener implements PacketListener<PacketSectorConnected> {

    @Override
    public void handle(PacketSectorConnected packet) {
        final String sectorName = packet.getSector();
        final String currentSectorName = PaperSector.getInstance().getSectorManager().getCurrentSectorName();

        if (!sectorName.equalsIgnoreCase(currentSectorName)) {
            LoggerUtil.info("Sector " + sectorName + " connected to the cluster.");
            Bukkit.broadcast(MessagesUtil.SECTOR_STARTED_NOTIFICATION.get("{SECTOR}", sectorName), "endsectors.messages");
        }
        final Sector sector = PaperSector.getInstance().getSectorManager().getSector(sectorName);
        if (sector != null) {
            sector.setOnline(true);
        }
    }
}