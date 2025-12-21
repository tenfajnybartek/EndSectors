package pl.endixon.sectors.proxy.queue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;

public class Queue {

    @Getter
    private String sector;
    private final List<UUID> players;

    public Queue(String sector) {
        this.sector = sector;
        this.players = new CopyOnWriteArrayList<>();
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public synchronized boolean addPlayer(UUID uuid) {
        if (players.contains(uuid)) {
            return false;
        }
        players.add(uuid);
        return true;
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    public boolean hasPlayer(UUID uuid) {
        return players.contains(uuid);
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
}
