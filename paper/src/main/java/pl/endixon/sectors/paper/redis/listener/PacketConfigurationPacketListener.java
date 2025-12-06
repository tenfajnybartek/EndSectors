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
import pl.endixon.sectors.common.packet.object.PacketConfiguration;
import pl.endixon.sectors.common.redis.RedisPacketListener;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.util.Logger;

public class PacketConfigurationPacketListener extends RedisPacketListener<PacketConfiguration> {

    private final PaperSector PaperSector;

    public PacketConfigurationPacketListener(PaperSector PaperSector) {
        super(PacketConfiguration.class);

        this.PaperSector = PaperSector;
    }

    @Override
    public void handle(PacketConfiguration packet) {
        Logger.info("Otrzymano pakiet konfiguracji od serwera proxy!");
        this.PaperSector.getSectorManager().loadSectorsData(packet.getSectorsData());
        Bukkit.getScheduler().runTask(PaperSector, PaperSector::init);
    }

}

