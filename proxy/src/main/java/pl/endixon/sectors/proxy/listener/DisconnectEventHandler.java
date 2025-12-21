package pl.endixon.sectors.proxy.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import pl.endixon.sectors.proxy.VelocitySectorPlugin;
import pl.endixon.sectors.proxy.queue.Queue;

public class DisconnectEventHandler {

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        for (Queue queue : VelocitySectorPlugin.getInstance().getQueueManager().getMap().values()) {
            queue.removePlayer(player.getUniqueId());
        }
    }
}
