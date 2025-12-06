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

import pl.endixon.sectors.common.packet.object.PacketSectorChatBroadcast;
import pl.endixon.sectors.common.redis.RedisPacketListener;
import pl.endixon.sectors.common.util.ChatUtil;
import pl.endixon.sectors.paper.PaperSector;

public class PacketSectorChatBroadcastPacketListener extends RedisPacketListener<PacketSectorChatBroadcast> {

    private final PaperSector paperSector;

    public PacketSectorChatBroadcastPacketListener(PaperSector paperSector) {
        super(PacketSectorChatBroadcast.class);
        this.paperSector = paperSector;
    }

    @Override
    public void handle(PacketSectorChatBroadcast packet) {
        this.paperSector.getServer().broadcastMessage(ChatUtil.fixColors("&7" + packet.getSenderName() + ": " + packet.getMessage()));
        }

}

