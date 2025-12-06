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


package pl.endixon.sectors.proxy.redis.listener;

import pl.endixon.sectors.common.packet.object.PacketSectorConnected;
import pl.endixon.sectors.common.redis.RedisPacketListener;
import pl.endixon.sectors.proxy.manager.SectorManager;

public class PacketSectorConnectedPacketListener extends RedisPacketListener<PacketSectorConnected> {

    private final SectorManager sectorManager;

    public PacketSectorConnectedPacketListener(SectorManager sectorManager) {
        super(PacketSectorConnected.class);

        this.sectorManager = sectorManager;
    }

    @Override
    public void handle(PacketSectorConnected packet) {
        this.sectorManager.getSectorData(packet.getSender()).setOnline(true);
    }
}

