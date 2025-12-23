package pl.endixon.sectors.proxy.queue;

import com.velocitypowered.api.proxy.Player;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Queue {

    @Setter
    private String sector;
    private final List<Player> players;

    public Queue(final String sector) {
        this.sector = sector;
        this.players = new CopyOnWriteArrayList<>();
    }

    public void addPlayer(final Player player) {
        if (this.hasPlayer(player)) {
            return;
        }
        this.players.add(player);
    }

    public void removePlayer(final Player player) {
        this.players.removeIf(p -> p.getUniqueId().equals(player.getUniqueId()));
    }

    public boolean hasPlayer(final Player player) {
        return this.players.stream()
                .anyMatch(p -> p.getUniqueId().equals(player.getUniqueId()));
    }
}