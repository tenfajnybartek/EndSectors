/*
 *
 * EndSectors – Non-Commercial License
 * (c) 2025 Endixon
 *
 * Permission is granted to use, copy, and
 * modify this software **only** for personal
 * or educational purposes.
 *
 * Commercial use, redistribution, claiming
 * this work as your own, or copying code
 * without explicit permission is strictly
 * prohibited.
 *
 * Visit https://github.com/Endixon/EndSectors
 * for more info.
 *
 */

package pl.endixon.sectors.paper.nats.listener;

import net.kyori.adventure.text.Component;
import pl.endixon.sectors.common.packet.PacketListener;
import pl.endixon.sectors.common.packet.object.PacketSectorChatBroadcast;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.util.ChatAdventureUtil;

public class PacketSectorChatBroadcastPacketListener implements PacketListener<PacketSectorChatBroadcast> {


    private static final ChatAdventureUtil CHAT_HELPER = new ChatAdventureUtil();

    @Override
    public void handle(PacketSectorChatBroadcast packet) {
        String formattedMessage = "<gray>" + packet.getSenderName() + ": <white>" + packet.getMessage();
        Component componentMessage = CHAT_HELPER.toComponent(formattedMessage);
        PaperSector.getInstance().getServer().sendMessage(componentMessage);
    }
}