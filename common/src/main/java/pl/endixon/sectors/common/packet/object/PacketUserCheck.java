package pl.endixon.sectors.common.packet.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import pl.endixon.sectors.common.packet.Packet;

@Getter
public class PacketUserCheck extends Packet {

    private final String username;
    private final Boolean exists;
    private final String lastSector;

    public PacketUserCheck(String username) {
        this.username = username;
        this.exists = null;
        this.lastSector = null;
    }

    public PacketUserCheck(String username, Boolean exists) {
        this.username = username;
        this.exists = exists;
        this.lastSector = null;
    }


    @JsonCreator
    public PacketUserCheck(
            @JsonProperty("username") String username,
            @JsonProperty("exists") Boolean exists,
            @JsonProperty("lastSector") String lastSector
    ) {
        this.username = username;
        this.exists = exists;
        this.lastSector = lastSector;
    }
}
