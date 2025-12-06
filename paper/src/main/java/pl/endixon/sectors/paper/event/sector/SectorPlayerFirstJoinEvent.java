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


package pl.endixon.sectors.paper.event.sector;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;
import lombok.Getter;
import pl.endixon.sectors.paper.sector.Sector;

@Getter
public class SectorPlayerFirstJoinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Sector sector;

    public SectorPlayerFirstJoinEvent(Player player, Sector sector) {
        this.player = player;
        this.sector = sector;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

