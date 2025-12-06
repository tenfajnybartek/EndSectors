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


package pl.endixon.sectors.paper.redis.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import pl.endixon.sectors.common.packet.Packet;

@Getter
public class PacketSectorInfo extends Packet {
    @Getter

    private final float tps;
    private final int playerCount;
    private final int maxPlayers;

    @JsonCreator
    public PacketSectorInfo(
            @JsonProperty("tps") float tps,
            @JsonProperty("playerCount") int playerCount,
            @JsonProperty("maxPlayers") int maxPlayers
    ) {
        this.tps = tps;
        this.playerCount = playerCount;
        this.maxPlayers = maxPlayers;
    }

    public float getTPS() {
        return tps;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}

