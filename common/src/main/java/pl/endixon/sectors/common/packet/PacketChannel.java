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

package pl.endixon.sectors.common.packet;

import lombok.Getter;

@Getter
public enum PacketChannel {

    PACKET_CONFIGURATION_REQUEST("packet.configuration.request"),
    PACKET_BROADCAST_MESSAGE("packet.broadcast.message"),
    PACKET_SEND_MESSAGE_TO_PLAYER("packet.send.message.player"),
    PACKET_BROADCAST_TITLE("packet.broadcast.title"),

    USER_CHECK_REQUEST("user.check.request"),
    USER_CHECK_RESPONSE("user.check.response"),

    PACKET_TELEPORT_TO_SECTOR("packet.teleport.sector"),
    PACKET_SECTOR_CONNECTED("packet.sector.connected"),
    PACKET_SECTOR_DISCONNECTED("packet.sector.disconnected"),

    PACKET_EXECUTE_COMMAND("packet.execute.command"),
    PACKET_PLAYER_INFO_REQUEST("packet.player.info.request"),
    PACKET_SECTOR_CHAT_BROADCAST("packet.sector.chat.broadcast"),
    PACKET_SECTOR_INFO("packet.sector.info"),

    MARKET_UPDATE("packet.market.update"),
    MARKET_NOTIFY("packet.market_notify"),
    MARKET_JANITOR("packet.market.janitor"),
    MARKET_EXPIRATION_NOTIFY("packet.market.expiration.notify");

    private final String subject;

    PacketChannel(String subject) {
        this.subject = subject;
    }

}

