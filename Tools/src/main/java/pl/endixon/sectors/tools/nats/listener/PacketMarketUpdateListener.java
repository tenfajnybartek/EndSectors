package pl.endixon.sectors.tools.nats.listener;

import pl.endixon.sectors.common.packet.PacketListener;

import pl.endixon.sectors.tools.nats.packet.PacketMarketUpdate;
import pl.endixon.sectors.tools.user.profile.PlayerMarketProfile;
import pl.endixon.sectors.tools.user.profile.ProfileMarketCache;

public class PacketMarketUpdateListener implements PacketListener<PacketMarketUpdate> {

    @Override
    public void handle(PacketMarketUpdate packet) {
        if (packet.getAction().equals("REMOVE")) {
            ProfileMarketCache.remove(packet.getId());
            return;
        }
        PlayerMarketProfile offer = new PlayerMarketProfile(
                packet.getId(),
                packet.getSellerUuid(),
                packet.getSellerName(),
                packet.getItemData(),
                packet.getItemName(),
                packet.getCategory(),
                packet.getPrice(),
                packet.getCreatedAt()
        );
        ProfileMarketCache.put(offer);
    }
}