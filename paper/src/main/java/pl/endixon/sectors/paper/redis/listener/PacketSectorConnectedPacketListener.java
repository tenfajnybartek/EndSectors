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
import pl.endixon.sectors.common.packet.object.PacketSectorConnected;
import pl.endixon.sectors.common.redis.RedisPacketListener;
import pl.endixon.sectors.common.util.ChatUtil;
import pl.endixon.sectors.paper.sector.SectorManager;
import pl.endixon.sectors.paper.util.Logger;

public class PacketSectorConnectedPacketListener extends RedisPacketListener<PacketSectorConnected> {

    private final SectorManager sectorManager;

    public PacketSectorConnectedPacketListener(SectorManager sectorManager) {
        super(PacketSectorConnected.class);
        this.sectorManager = sectorManager;
    }

    @Override
    public void handle(PacketSectorConnected packet) {
        String sectorName = packet.getSender();

        if (!sectorName.equalsIgnoreCase(this.sectorManager.getCurrentSectorName())) {
            String message = String.format("&aSektor &e%s &azostał uruchomiony i jest dostępny!", sectorName);
            Logger.info(message);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("endsectors.messages")) {
                    player.sendMessage(ChatUtil.fixColors(message));
                }
            }
        }
        this.sectorManager.getSector(sectorName).setOnline(true);
    }
}

