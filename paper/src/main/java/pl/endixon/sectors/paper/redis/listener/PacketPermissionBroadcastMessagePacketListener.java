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
import pl.endixon.sectors.paper.redis.packet.PacketPermissionBroadcastMessage;
import pl.endixon.sectors.common.redis.RedisPacketListener;

public class PacketPermissionBroadcastMessagePacketListener extends RedisPacketListener<PacketPermissionBroadcastMessage> {

    public PacketPermissionBroadcastMessagePacketListener() {
        super(PacketPermissionBroadcastMessage.class);
    }

    @Override
    public void handle(PacketPermissionBroadcastMessage packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(packet.getPermission())) {
                player.sendMessage(packet.getMessage());
            }
        }
    }
}

