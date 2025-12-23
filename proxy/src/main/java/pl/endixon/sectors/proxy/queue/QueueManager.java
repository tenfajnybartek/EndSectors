package pl.endixon.sectors.proxy.queue;

import com.velocitypowered.api.proxy.Player;
import java.util.Optional;


public class QueueManager extends SimpleQueueManager<String, Queue> {

    public Optional<Queue> findQueueByPlayer(Player player) {
        return this.getMap().values().stream().filter(queue -> queue.hasPlayer(player)).findFirst();
    }
}
