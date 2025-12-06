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
import org.bukkit.command.ConsoleCommandSender;
import pl.endixon.sectors.paper.redis.packet.PacketExecuteCommand;
import pl.endixon.sectors.common.redis.RedisPacketListener;
import pl.endixon.sectors.paper.PaperSector;

public class PacketExecuteCommandPacketListener extends RedisPacketListener<PacketExecuteCommand> {

    private final PaperSector paperSector;

    public PacketExecuteCommandPacketListener(PaperSector paperSector) {
        super(PacketExecuteCommand.class);
        this.paperSector = paperSector;
    }

    @Override
    public void handle(PacketExecuteCommand packet) {
        ConsoleCommandSender console = paperSector.getServer().getConsoleSender();
        Bukkit.getScheduler().runTask(paperSector, () -> {
            paperSector.getServer().dispatchCommand(console, packet.getCommand());
        });
    }
}

