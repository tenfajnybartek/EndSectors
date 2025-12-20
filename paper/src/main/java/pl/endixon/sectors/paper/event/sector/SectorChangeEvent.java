package pl.endixon.sectors.paper.event.sector;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import lombok.Getter;
import pl.endixon.sectors.paper.sector.Sector;

@Getter
public class SectorChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Sector sector;
    private boolean cancelled = false;

    public SectorChangeEvent(Player player, Sector sector) {
        this.player = player;
        this.sector = sector;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Sector getTo() {
        return this.sector;
    }
}
