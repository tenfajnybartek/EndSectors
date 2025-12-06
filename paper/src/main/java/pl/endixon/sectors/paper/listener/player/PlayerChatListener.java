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


package pl.endixon.sectors.paper.listener.player;

import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.endixon.sectors.common.packet.object.PacketSectorChatBroadcast;
import pl.endixon.sectors.common.sector.SectorType;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.sector.Sector;
import pl.endixon.sectors.paper.sector.SectorManager;



@AllArgsConstructor
public class PlayerChatListener implements Listener {

    private final PaperSector paperSector;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        SectorManager sectorManager = this.paperSector.getSectorManager();
        Sector currentSector = sectorManager.getCurrentSector();
        if (currentSector != null && currentSector.getType() == SectorType.QUEUE) {
            event.setCancelled(true);
            return;
        }
        String sender = event.getPlayer().getName();
        String message = event.getMessage();
        event.setCancelled(true);
        currentSector.sendPacketSectors(new PacketSectorChatBroadcast(sender, message));
    }
}

