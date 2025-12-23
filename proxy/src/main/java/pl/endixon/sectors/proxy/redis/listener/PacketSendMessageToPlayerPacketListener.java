package pl.endixon.sectors.proxy.redis.listener;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import pl.endixon.sectors.common.packet.PacketListener;
import pl.endixon.sectors.common.packet.object.PacketSendMessageToPlayer;
import pl.endixon.sectors.proxy.VelocitySectorPlugin;

public class PacketSendMessageToPlayerPacketListener implements PacketListener<PacketSendMessageToPlayer> {

    @Override
    public void handle(PacketSendMessageToPlayer packet) {
        final ProxyServer server = VelocitySectorPlugin.getInstance().getServer();
        if (server == null) {
            return;
        }

        final String playerName = packet.getPlayerName();
        if (playerName == null) {
            return;
        }

        server.getPlayer(playerName).ifPresent(player ->
                player.sendMessage(Component.text(packet.getMessage()))
        );
    }
}