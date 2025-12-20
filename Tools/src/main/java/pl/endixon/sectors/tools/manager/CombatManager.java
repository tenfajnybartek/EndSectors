package pl.endixon.sectors.tools.manager;

import org.bukkit.entity.Player;
import pl.endixon.sectors.tools.Main;
import pl.endixon.sectors.tools.task.CombatTask;

import java.util.HashMap;
import java.util.Map;

public class CombatManager {

    private final Main plugin;
    private final Map<Player, Player> inCombat = new HashMap<>();

    public CombatManager(Main plugin) {
        this.plugin = plugin;
    }


    public void startCombat(Player attacker, Player victim) {
        inCombat.put(attacker, victim);
        inCombat.put(victim, attacker);
        new CombatTask(plugin, this, attacker).start();
        new CombatTask(plugin, this, victim).start();
    }

    public void endCombat(Player player) {
        Player other = inCombat.remove(player);
        if (other != null) inCombat.remove(other);
    }

    public boolean isInCombat(Player player) {
        return inCombat.containsKey(player);
    }

    public Player getCombatPartner(Player player) {
        return inCombat.get(player);
    }
}
