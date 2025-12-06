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

package pl.endixon.sectors.common.packet.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.endixon.sectors.common.packet.Packet;

public class PacketRequestTeleportSector extends Packet {

    private final String playerName;
    private final String sector;

    @JsonCreator
    public PacketRequestTeleportSector(
            @JsonProperty("playerName") String playerName,
            @JsonProperty("sector") String sector) {
        this.playerName = playerName;
        this.sector = sector;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getSector() {
        return sector;
    }
}

