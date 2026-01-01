package pl.endixon.sectors.tools.nats.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.endixon.sectors.common.packet.Packet;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PacketMarketJanitor implements Packet {
    private long expirationThreshold;
}