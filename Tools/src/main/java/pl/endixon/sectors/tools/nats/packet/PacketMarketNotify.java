package pl.endixon.sectors.tools.nats.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.endixon.sectors.common.packet.Packet;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PacketMarketNotify implements Packet {
    private final UUID sellerUuid;
    private final String buyerName;
    private final String itemName;
    private final double price;
}