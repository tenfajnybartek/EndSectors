package pl.endixon.sectors.tools.command;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.endixon.sectors.common.sector.SectorType;
import pl.endixon.sectors.common.util.ChatUtil;
import pl.endixon.sectors.paper.sector.Sector;
import pl.endixon.sectors.paper.sector.SectorManager;
import pl.endixon.sectors.paper.user.UserMongo;
import pl.endixon.sectors.paper.user.UserManager;
import pl.endixon.sectors.paper.event.sector.SectorChangeEvent;
import pl.endixon.sectors.tools.Main;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTPCommand implements CommandExecutor {

    private final SectorManager sectorManager;
    private static final long RANDOM_TP_COOLDOWN = 10_000L; // 10 sekund

    public RandomTPCommand(SectorManager sectorManager) {
        this.sectorManager = sectorManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Tylko gracz, konsolo spierdalaj");
            return true;
        }

        UserMongo user = UserManager.getUser(player);
        long now = System.currentTimeMillis();

        List<Sector> sectors = sectorManager.getSectors().stream()
                .filter(Sector::isOnline)
                .filter(s -> s.getType() != SectorType.SPAWN &&
                        s.getType() != SectorType.QUEUE &&
                        s.getType() != SectorType.NETHER &&
                        s.getType() != SectorType.END)
                .toList();


        if (sectors.isEmpty()) {
            player.sendTitle(
                    ChatUtil.fixColors("&6RandomTP"),
                    ChatUtil.fixColors("&cBrak dostępnych sektorów do teleportacji!"),
                    10, 40, 10
            );
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.8f);
            return true;
        }

        Sector sector = sectors.get(ThreadLocalRandom.current().nextInt(sectors.size()));

        if (!sector.isOnline()) {
            player.sendTitle(
                    ChatUtil.fixColors("&6RandomTP"),
                    ChatUtil.fixColors("&cSektor jest offline, teleportacja przerwana!"),
                    10, 40, 10
            );
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.8f);
            return true;
        }

        var loc = sectorManager.randomLocation(sector);
        if (loc == null) {
            player.sendTitle(
                    ChatUtil.fixColors("&6RandomTP"),
                    ChatUtil.fixColors("&cNie udało się wylosować lokacji!"),
                    10, 40, 10
            );
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.8f);
            return true;
        }

        user.setLastTransferTimestamp(now);
        player.sendTitle(ChatUtil.fixColors("&6RandomTP"), ChatUtil.fixColors("&7Losowanie sektora..."), 0, 9999, 0);

        new BukkitRunnable() {
            int countdown = 10;

            @Override
            public void run() {
                if (countdown > 0) {
                    player.sendTitle(
                            ChatUtil.fixColors("&6RandomTP"),
                            ChatUtil.fixColors("&7Teleportacja na &e" + sector.getName() + " &7za &f" + countdown + "s"),
                            0, 20, 0
                    );
                    countdown--;
                    return;
                }

                if (!sector.isOnline() || loc == null) {
                    player.sendTitle(
                            ChatUtil.fixColors("&6RandomTP"),
                            ChatUtil.fixColors("&cSektor stał się offline, teleportacja przerwana!"),
                            10, 40, 10
                    );
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.8f);
                    cancel();
                    return;
                }

                SectorChangeEvent ev = new SectorChangeEvent(player, sector);
                Main.getInstance().getServer().getPluginManager().callEvent(ev);

                if (!ev.isCancelled()) {
                    player.teleport(loc);
                    user.setLastSectorTransfer(true);
                    user.updatePlayerData(player, sector);

                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                        Vector forward = player.getLocation().getDirection().normalize();
                        player.setVelocity(forward.multiply(0.8));
                    }, 2L);


                    player.sendTitle(
                            ChatUtil.fixColors("&aTeleportacja!"),
                            ChatUtil.fixColors("&7Sektor: &e" + sector.getName()),
                            5, 40, 10
                    );
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                }

                cancel();
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);

        return true;
    }

}