package pl.endixon.sectors.proxy.queue;

import com.velocitypowered.api.proxy.Player;
import java.util.Optional;
import java.util.UUID;

public class QueueManager extends SimpleQueueManager<String, Queue> {

    public Optional<Queue> findQueueByPlayer(UUID uuid) {
        return this.getMap().values().stream()
                .filter(queue -> queue.hasPlayer(uuid))
                .findFirst();
    }

    public Optional<Queue> findQueueByPlayer(Player player) {
        return findQueueByPlayer(player.getUniqueId());
    }
}
