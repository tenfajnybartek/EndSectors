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

package pl.endixon.sectors.common.packet.object;

import pl.endixon.sectors.common.packet.Packet;
import pl.endixon.sectors.common.sector.SectorData;

public class PacketConfiguration implements Packet {

    private final SectorData[] sectorsData;

    public PacketConfiguration(SectorData[] sectorsData) {
        this.sectorsData = sectorsData;
    }

    public SectorData[] getSectorsData() {
        return sectorsData;
    }
}
