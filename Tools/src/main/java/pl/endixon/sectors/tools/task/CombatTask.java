package pl.endixon.sectors.tools.task;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.endixon.sectors.tools.manager.CombatManager;

public class CombatTask implements Runnable {

    private final JavaPlugin plugin;
    private final CombatManager combatManager;
    private final Player player;
    private int timeLeft = 30;
    private BossBar bossBar;
    private int taskId;

    public CombatTask(JavaPlugin plugin, CombatManager combatManager, Player player) {
        this.plugin = plugin;
        this.combatManager = combatManager;
        this.player = player;
        createBossBar();
    }

    private void createBossBar() {
        bossBar = Bukkit.createBossBar(
                "Jesteś w walce " + timeLeft + "s",
                BarColor.RED,
                BarStyle.SEGMENTED_10
        );
        bossBar.addPlayer(player);
        bossBar.setProgress(1.0);
    }

    @Override
    public void run() {
        if (!combatManager.isInCombat(player) || timeLeft <= 0) {
            if (bossBar != null) bossBar.removeAll();
            combatManager.endCombat(player);
            Bukkit.getScheduler().cancelTask(taskId);
            return;
        }

        if (bossBar != null) {
            bossBar.setProgress(timeLeft / 30.0);
            bossBar.setTitle("Jesteś w walce " + timeLeft + "s");
        }

        timeLeft--;
    }

    public void start() {
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 20L).getTaskId();
    }
}
