package pl.endixon.sectors.tools.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Sound;

import pl.endixon.sectors.paper.SectorsAPI;
import pl.endixon.sectors.paper.event.sector.SectorChangeEvent;
import pl.endixon.sectors.paper.sector.Sector;
import pl.endixon.sectors.paper.user.UserRedis;
import pl.endixon.sectors.tools.utils.ChatAdventureUtil;
import pl.endixon.sectors.tools.utils.TeleportHelper;
import pl.endixon.sectors.common.util.ChatUtil;
import pl.endixon.sectors.tools.utils.Messages;

import java.time.Duration;

public class RandomTPCommand implements CommandExecutor {

    private static final int COUNTDOWN_TIME = 10;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.CONSOLE_BLOCK.get());
            return true;
        }

        SectorsAPI api = SectorsAPI.getInstance();
        if (api == null) return true;

        player.showTitle(Title.title(
                Messages.RANDOM_TITLE.get(),
                Messages.RANDOM_START.get(),
                Title.Times.times(
                        Duration.ofMillis(0),
                        Duration.ofMillis(9999),
                        Duration.ofMillis(0)
                )
        ));



        UserRedis user = api.getUser(player).orElse(null);
        if (user == null) {
            player.sendMessage(Messages.PLAYERDATANOT_FOUND_MESSAGE.get());
            return true;
        }
        boolean isAdmin = player.hasPermission("endsectors.admin");
        int countdown = isAdmin ? 0 : COUNTDOWN_TIME;
        user.setTransferOffsetUntil(0);
        TeleportHelper.startTeleportCountdown(player, countdown, () -> {
            api.getRandomLocation(player, user);

            Sector randomSector = api.getSectorManager().getSector(user.getSectorName());
            if (randomSector == null) {
                player.sendMessage(Messages.RANDOM_SECTOR_NOTFOUND.get());
                return;
            }
            SectorChangeEvent event = new SectorChangeEvent(player, randomSector);
            api.getPaperSector().getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            }
        });
        return true;
    }
}
