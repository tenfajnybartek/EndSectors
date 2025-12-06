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


package pl.endixon.sectors.paper.util;

import lombok.experimental.UtilityClass;
import pl.endixon.sectors.common.packet.object.PacketBroadcastMessage;
import pl.endixon.sectors.common.packet.object.PacketBroadcastTitle;
import pl.endixon.sectors.common.packet.PacketChannel;
import pl.endixon.sectors.common.redis.RedisManager;
import pl.endixon.sectors.paper.redis.packet.PacketPermissionBroadcastMessage;
import pl.endixon.sectors.common.packet.object.PacketSendMessageToPlayer;

@UtilityClass
public class MessageUtil {

    private static final RedisManager redisManager = RedisManager.getInstance();

    public static void sendBroadcastMessage(String message) {
        redisManager.publish(PacketChannel.PROXY, new PacketBroadcastMessage(message));
    }

    public static void sendBroadcastPermissionMessage(String permission, String message) {
        redisManager.publish(PacketChannel.SECTORS, new PacketPermissionBroadcastMessage(permission, message));
    }

    public static void sendMessageToPlayer(String playerName, String message) {
        redisManager.publish(PacketChannel.PROXY, new PacketSendMessageToPlayer(playerName, message));
    }

    public static void sendBroadcastTitle(String title, String subTitle) {
        sendBroadcastTitle(title, subTitle, 20, 20, 20);
    }

    public static void sendBroadcastTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        redisManager.publish(PacketChannel.PROXY, new PacketBroadcastTitle(title, subTitle, fadeIn, stay, fadeOut));
    }
}

