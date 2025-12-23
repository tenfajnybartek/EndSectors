package pl.endixon.sectors.proxy.redis.listener;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import pl.endixon.sectors.common.packet.PacketListener;
import pl.endixon.sectors.common.packet.object.PacketRequestTeleportSector;
import pl.endixon.sectors.proxy.VelocitySectorPlugin;
import pl.endixon.sectors.proxy.util.Logger;

public class TeleportToSectorListener implements PacketListener<PacketRequestTeleportSector> {

    @Override
    public void handle(PacketRequestTeleportSector packet) {
        final String playerName = packet.getPlayerName();
        final String sectorName = packet.getSector();

        final Optional<Player> playerOptional = VelocitySectorPlugin.getInstance().getServer().getPlayer(playerName);
        if (playerOptional.isEmpty()) {
            return;
        }

        final Player player = playerOptional.get();
        final Optional<RegisteredServer> serverOptional = VelocitySectorPlugin.getInstance().getServer().getServer(sectorName);

        if (serverOptional.isEmpty()) {
            player.disconnect(Component.text("Target sector server is currently unavailable."));
            Logger.info("[CRITICAL] Teleport failed for " + playerName + ". Server " + sectorName + " not found.");
            return;
        }

        final RegisteredServer server = serverOptional.get();
        player.createConnectionRequest(server).fireAndForget();
    }
}