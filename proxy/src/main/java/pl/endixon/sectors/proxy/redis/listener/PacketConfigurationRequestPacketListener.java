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

import pl.endixon.sectors.common.packet.object.PacketConfiguration;
import pl.endixon.sectors.common.packet.object.PacketConfigurationRequest;
import pl.endixon.sectors.common.redis.RedisPacketListener;
import pl.endixon.sectors.common.redis.RedisManager;
import pl.endixon.sectors.common.sector.SectorData;
import pl.endixon.sectors.proxy.manager.SectorManager;
import pl.endixon.sectors.proxy.util.Logger;

public class PacketConfigurationRequestPacketListener extends RedisPacketListener<PacketConfigurationRequest> {

    private final SectorManager sectorManager;

    public PacketConfigurationRequestPacketListener(SectorManager sectorManager) {
        super(PacketConfigurationRequest.class);

        this.sectorManager = sectorManager;
    }

    @Override
    public void handle(PacketConfigurationRequest packet) {
        Logger.info("Otrzymano zapytanie o pakiet konfiguracji od sektora " + packet.getSender());

        PacketConfiguration packetConfiguration = new PacketConfiguration(
                this.sectorManager.getSectorsData().toArray(new SectorData[0])
        );

        RedisManager.getInstance().publish(packet.getSender(), packetConfiguration);
    }
}

