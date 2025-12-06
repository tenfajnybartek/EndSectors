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

import pl.endixon.sectors.common.redis.RedisPacketListener;
import pl.endixon.sectors.paper.redis.packet.PacketSectorInfo;
import pl.endixon.sectors.paper.sector.Sector;
import pl.endixon.sectors.paper.sector.SectorManager;

public class PacketSectorInfoPacketListener extends RedisPacketListener<PacketSectorInfo> {

    private final SectorManager sectorManager;

    public PacketSectorInfoPacketListener(SectorManager sectorManager) {
        super(PacketSectorInfo.class);

        this.sectorManager = sectorManager;
    }

    @Override
    public void handle(PacketSectorInfo packet) {
        Sector sector = this.sectorManager.getSector(packet.getSender());

        if (sector != null) {
            sector.setLastInfoPacket();
            sector.setTPS(packet.getTPS());
            sector.setPlayerCount(packet.getPlayerCount());
            sector.setMaxPlayers(packet.getMaxPlayers());
        }
    }
}

