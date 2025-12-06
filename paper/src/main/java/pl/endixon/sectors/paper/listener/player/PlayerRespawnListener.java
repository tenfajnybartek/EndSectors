/*
 *
 *  EndSectors  Non-Commercial License
 *  (c) 2025 Endixon
 *
 *  Permission is granted to use, copy, and
 *  modify this software **only** for personal
 *  or educational purposes.
 *
 *   Commercial use, redistribution, claiming
 *  this work as your own, or copying code
 *  without explicit permission is strictly
 *  prohibited.
 *
 *  Visit https://github.com/Endixon/EndSectors
 *  for more info.
 *
 */


package pl.endixon.sectors.paper.listener.player;

import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;
import pl.endixon.sectors.common.sector.SectorType;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.sector.Sector;
import pl.endixon.sectors.paper.user.UserManager;
import pl.endixon.sectors.paper.user.UserMongo;
import pl.endixon.sectors.paper.util.Logger;

@AllArgsConstructor
public class PlayerRespawnListener implements Listener {

    private final PaperSector paperSector;

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Sector queue = paperSector.getSectorManager().getCurrentSector();
        if (queue.getType() == SectorType.QUEUE)
            return;
        if (event.getEntity() == null) return;
        final Player victim = event.getEntity();
        this.paperSector.getServer().getScheduler().scheduleSyncDelayedTask(this.paperSector, () -> victim.spigot().respawn(), 2L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Sector queue = paperSector.getSectorManager().getCurrentSector();
        if (queue.getType() == SectorType.QUEUE) return;

        Player player = event.getPlayer();
        Sector currentSector = PaperSector.getInstance().getSectorManager().getCurrentSector();
        UserManager.getUser(player.getName()).thenAccept(user -> {
            if (user == null) {
                player.kickPlayer("Brak danych gracza!");
                return;
            }
            event.setRespawnLocation(new Location(player.getWorld(), 0.5, 70, 0.5));
            user.updatePlayerData(player, currentSector);
        });
    }

}

