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


package pl.endixon.sectors.paper.redis.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.endixon.sectors.common.packet.object.PacketSectorDisconnected;
import pl.endixon.sectors.common.redis.RedisPacketListener;
import pl.endixon.sectors.common.util.ChatUtil;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.sector.Sector;
import pl.endixon.sectors.paper.sector.SectorManager;
import pl.endixon.sectors.paper.user.UserManager;
import pl.endixon.sectors.paper.user.UserMongo;
import pl.endixon.sectors.paper.util.Logger;

public class PacketSectorDisconnectedPacketListener extends RedisPacketListener<PacketSectorDisconnected> {

    private final SectorManager sectorManager;

    public PacketSectorDisconnectedPacketListener(SectorManager sectorManager) {
        super(PacketSectorDisconnected.class);
        this.sectorManager = sectorManager;
    }

    @Override
    public void handle(PacketSectorDisconnected packet) {
        String sectorName = packet.getSender();

        Sector disconnectedSector = this.sectorManager.getSector(sectorName);
        if (disconnectedSector == null) return;
        disconnectedSector.setOnline(false);

        if (!sectorName.equalsIgnoreCase(this.sectorManager.getCurrentSectorName())) {
            String message = String.format("&cSektor &e%s &czostał zamknięty i jest niedostępny!", sectorName);
            Logger.info(message);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("endsectors.messages")) continue;
                player.sendMessage(ChatUtil.fixColors(message));
            }
        }
    }
}

