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
import lombok.Getter;
import pl.endixon.sectors.common.packet.Packet;
import pl.endixon.sectors.common.sector.SectorData;

@Getter
public class PacketConfiguration extends Packet {

    private final SectorData[] sectorsData;

    @JsonCreator
    public PacketConfiguration(@JsonProperty("sectorsData") SectorData[] sectorsData) {
        this.sectorsData = sectorsData;
    }
}

