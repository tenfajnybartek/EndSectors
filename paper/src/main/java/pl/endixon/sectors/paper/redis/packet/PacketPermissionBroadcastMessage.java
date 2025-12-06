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

import lombok.Getter;
import pl.endixon.sectors.common.packet.Packet;

@Getter
public class PacketPermissionBroadcastMessage extends Packet {

    private String permission;
    private String message;

    public PacketPermissionBroadcastMessage() {}

    public PacketPermissionBroadcastMessage(String permission, String message) {
        this.permission = permission;
        this.message = message;
    }
}

