package pl.endixon.sectors.common.packet.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import pl.endixon.sectors.common.packet.Packet;
import pl.endixon.sectors.common.sector.SectorType;

@Getter
public class PacketDynmapSector extends Packet {

    private final String sectorName;
    private final String serverName;
    private final String world;
    private final double pos1X;
    private final double pos1Z;
    private final double pos2X;
    private final double pos2Z;
    private final SectorType type;
    private final boolean online;

    @JsonCreator
    public PacketDynmapSector(
            @JsonProperty("sectorName") String sectorName,
            @JsonProperty("serverName") String serverName,
            @JsonProperty("world") String world,
            @JsonProperty("pos1X") double pos1X,
            @JsonProperty("pos1Z") double pos1Z,
            @JsonProperty("pos2X") double pos2X,
            @JsonProperty("pos2Z") double pos2Z,
            @JsonProperty("type") SectorType type,
            @JsonProperty("online") boolean online
    ) {
        this.sectorName = sectorName;
        this.serverName = serverName;
        this.world = world;
        this.pos1X = pos1X;
        this.pos1Z = pos1Z;
        this.pos2X = pos2X;
        this.pos2Z = pos2Z;
        this.type = type;
        this.online = online;
    }
}
