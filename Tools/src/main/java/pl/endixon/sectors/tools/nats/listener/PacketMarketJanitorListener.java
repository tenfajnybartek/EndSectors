package pl.endixon.sectors.tools.nats.listener;

import pl.endixon.sectors.common.packet.PacketListener;
import pl.endixon.sectors.tools.EndSectorsToolsPlugin;
import pl.endixon.sectors.tools.nats.packet.PacketMarketJanitor;

public class PacketMarketJanitorListener implements PacketListener<PacketMarketJanitor> {

    @Override
    public void handle(PacketMarketJanitor packet) {
        EndSectorsToolsPlugin.getInstance().getMarketRepository().cleanupLocalCache(packet.getExpirationThreshold());
    }
}