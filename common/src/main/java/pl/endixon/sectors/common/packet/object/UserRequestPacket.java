package pl.endixon.sectors.common.packet.object;

import pl.endixon.sectors.common.packet.Packet;

public class UserRequestPacket implements Packet {

    private final String uuid;
    private final String requestId;

    public UserRequestPacket(String uuid, String requestId) {
        this.uuid = uuid;
        this.requestId = requestId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRequestId() {
        return requestId;
    }
}
