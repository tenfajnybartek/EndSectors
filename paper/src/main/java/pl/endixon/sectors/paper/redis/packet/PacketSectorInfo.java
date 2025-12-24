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

package pl.endixon.sectors.paper.redis.packet;

import lombok.Getter;
import pl.endixon.sectors.common.packet.Packet;

@Getter
public class PacketSectorInfo implements Packet {

    private final String sector;
    private final boolean status;
    private final float tps;
    private final int playerCount;
    private final int maxPlayers;

    public PacketSectorInfo(String sector, boolean status, float tps, int playerCount, int maxPlayers) {
        this.sector = sector;
        this.status = status;
        this.tps = tps;
        this.playerCount = playerCount;
        this.maxPlayers = maxPlayers;
    }
}
