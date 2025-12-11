package pl.endixon.sectors.paper.event.sector;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.endixon.sectors.paper.util.Logger;

public class PaperSectorReadyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public PaperSectorReadyEvent() {
        super(true); // oznacza asynchroniczny
        Logger.info("PaperSectorReadyEvent został utworzony!");
    }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
