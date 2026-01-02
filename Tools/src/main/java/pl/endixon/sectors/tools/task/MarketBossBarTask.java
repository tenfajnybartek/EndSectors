package pl.endixon.sectors.tools.task;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.endixon.sectors.tools.EndSectorsToolsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class MarketBossBarTask extends BukkitRunnable {

    private final EndSectorsToolsPlugin plugin;
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final Map<UUID, BossBar> activeBars = new HashMap<>();

    @Override
    public void run() {
        activeBars.keySet().removeIf(uuid -> Bukkit.getPlayer(uuid) == null);
        for (Player player : Bukkit.getOnlinePlayers()) {
            checkPlayer(player);
        }
    }

    private void checkPlayer(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            UUID uuid = player.getUniqueId();
            int claimable = plugin.getMarketRepository().findClaimableBySeller(uuid).size();
            int expired = plugin.getMarketRepository().findExpiredBySeller(uuid).size();

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!player.isOnline()) return;

                if (claimable == 0 && expired == 0) {
                    if (activeBars.containsKey(uuid)) {
                        player.hideBossBar(activeBars.remove(uuid));
                    }
                    return;
                }

                Component title;
                BossBar.Color color;

                if (claimable > 0) {
                    title = MM.deserialize("<gradient:#55FFFF:#00AAAA><bold>SKRZYNKA ODBIORCZA:</bold></gradient> <gray>Masz <aqua>" + claimable + " <gray>zakupionych przedmiotów! <yellow>/market");
                    color = BossBar.Color.BLUE;
                } else {
                    title = MM.deserialize("<gradient:#FF5555:#AA0000><bold>MAGAZYN:</bold></gradient> <gray>Masz <red>" + expired + " <gray>wygasłych ofert! <yellow>/market");
                    color = BossBar.Color.RED;
                }

                BossBar bar = activeBars.get(uuid);
                if (bar == null) {
                    bar = BossBar.bossBar(title, 1.0f, color, BossBar.Overlay.PROGRESS);
                    player.showBossBar(bar);
                    activeBars.put(uuid, bar);
                } else {
                    bar.name(title);
                    bar.color(color);
                }
            });
        });
    }
}