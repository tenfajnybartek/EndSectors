package pl.endixon.sectors.tools.nats.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.endixon.sectors.common.packet.Packet;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PacketMarketExpirationNotify implements Packet {
    private UUID ownerUuid;
    private int expiredCount;
}