package pl.endixon.sectors.paper.listener.player;

import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.endixon.sectors.paper.PaperSector;
import pl.endixon.sectors.paper.event.sector.SectorChangeEvent;
import pl.endixon.sectors.paper.sector.Sector;
import pl.endixon.sectors.paper.sector.SectorManager;
import pl.endixon.sectors.paper.user.UserManager;
import pl.endixon.sectors.common.sector.SectorType;
import pl.endixon.sectors.paper.user.UserRedis;
import pl.endixon.sectors.paper.util.ChatAdventureUtil;
import pl.endixon.sectors.paper.util.Configuration;

import java.time.Duration;

public class PlayerTeleportListener implements Listener {

    private final PaperSector paperSector;
    private static final long TRANSFER_DELAY = 5000L;
    private static final double KNOCK_BORDER_FORCE = 1.35;



    public PlayerTeleportListener(PaperSector paperSector) {
        this.paperSector = paperSector;
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;


        Sector queue = paperSector.getSectorManager().getCurrentSector();
        if (queue.getType() == SectorType.QUEUE) return;
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;

        Player player = event.getPlayer();
        Location to = event.getTo();


        UserRedis user = UserManager.getUser(player).orElse(null);
        if (user == null) return;

        SectorManager sectorManager = paperSector.getSectorManager();
        Sector currentSector = sectorManager.getSector(player.getLocation());
        Sector targetSector = sectorManager.getSector(to);
        if (currentSector == null || targetSector == null) return;

        if (targetSector.getType() == SectorType.SPAWN) {
            targetSector = sectorManager.find(SectorType.SPAWN);
        }

        SectorChangeEvent ev = new SectorChangeEvent(player, targetSector);
        Bukkit.getPluginManager().callEvent(ev);
        if (ev.isCancelled()) return;

        if (!targetSector.isOnline()) {
            player.showTitle(Title.title(
                    ChatAdventureUtil.toComponent(Configuration.SECTOR_ERROR_TITLE),
                    ChatAdventureUtil.toComponent(Configuration.SECTOR_DISABLED_SUBTITLE),

                    Title.Times.times(
                            Duration.ofMillis(500),
                            Duration.ofMillis(2000),
                            Duration.ofMillis(500)
                    )
            ));
            currentSector.knockBorder(player, KNOCK_BORDER_FORCE);
            return;
        }

        if (Sector.isSectorFull(targetSector)) {
            player.showTitle(Title.title(
                    ChatAdventureUtil.toComponent(Configuration.SECTOR_ERROR_TITLE),
                    ChatAdventureUtil.toComponent(Configuration.SECTOR_FULL_SUBTITLE),
                    Title.Times.times(
                            Duration.ofMillis(500),
                            Duration.ofMillis(2000),
                            Duration.ofMillis(500)
                    )
            ));
            currentSector.knockBorder(player, KNOCK_BORDER_FORCE);
            return;
        }

        boolean inTransfer = user.getLastSectorTransfer() > 0;
        if (System.currentTimeMillis() < user.getTransferOffsetUntil() && !inTransfer) {
            long remaining = user.getTransferOffsetUntil() - System.currentTimeMillis();
            player.showTitle(Title.title(
                    ChatAdventureUtil.toComponent(Configuration.SECTOR_ERROR_TITLE),
                    ChatAdventureUtil.toComponent(Configuration.TITLE_WAIT_TIME.replace("{SECONDS}", String.valueOf(remaining / 1000 + 1))),
                    Title.Times.times(java.time.Duration.ofMillis(500),
                            java.time.Duration.ofMillis(2000),
                            java.time.Duration.ofMillis(500))
            ));
            currentSector.knockBorder(player, KNOCK_BORDER_FORCE);
            return;
        }


        if (System.currentTimeMillis() - user.getLastSectorTransfer() < TRANSFER_DELAY) return;

        user.setLastSectorTransfer(true);
        user.activateTransferOffset();
        user.setLastTransferTimestamp(System.currentTimeMillis());

        paperSector.getSectorTeleportService().teleportToSector(player, user, targetSector, false,false);
    }
}
