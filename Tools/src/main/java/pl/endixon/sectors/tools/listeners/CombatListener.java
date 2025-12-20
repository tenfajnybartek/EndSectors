package pl.endixon.sectors.tools.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pl.endixon.sectors.tools.Main;
import pl.endixon.sectors.tools.manager.CombatManager;
import pl.endixon.sectors.tools.task.CombatTask;
import pl.endixon.sectors.tools.utils.ChatAdventureUtil;
import pl.endixon.sectors.tools.utils.Messages;

public class CombatListener implements Listener {

    private final CombatManager combatManager;

    public CombatListener(CombatManager combatManager) {
        this.combatManager = combatManager;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        if (victim.getGameMode() != org.bukkit.GameMode.SURVIVAL &&
                victim.getGameMode() != org.bukkit.GameMode.ADVENTURE) return;

        if (attacker.getGameMode() != org.bukkit.GameMode.SURVIVAL &&
                attacker.getGameMode() != org.bukkit.GameMode.ADVENTURE) return;


        if (combatManager.isInCombat(attacker)) combatManager.endCombat(attacker);
        if (combatManager.isInCombat(victim)) combatManager.endCombat(victim);

        combatManager.startCombat(attacker, victim);

        new CombatTask(Main.getInstance(), combatManager, attacker).start();
        new CombatTask(Main.getInstance(), combatManager, victim).start();
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (combatManager.isInCombat(player)) {
            player.sendMessage((Messages.COMBAT_NO_COMMAND.get()));
            event.setCancelled(true);
        }
    }
}
