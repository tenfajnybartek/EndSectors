package pl.endixon.sectors.tools.command;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.endixon.sectors.tools.EndSectorsToolsPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EconomyCommand implements CommandExecutor, TabCompleter {

    private final Economy economy = EndSectorsToolsPlugin.getInstance().getEconomy();
    private static final String ECO_PREFIX = "§8[§aEkonomia§8] ";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player player)) return true;
            this.sendBalance(player, player);
            return true;
        }

        if (args[0].equalsIgnoreCase("pay") && args.length == 3) {
            if (!(sender instanceof Player player)) return true;
            this.handlePay(player, args[1], args[2]);
            return true;
        }

        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            this.sendBalance(sender, target);
            return true;
        }

        if (sender.hasPermission("economy.admin")) {
            return this.handleAdminActions(sender, args);
        }

        return true;
    }

    private void sendBalance(CommandSender viewer, OfflinePlayer target) {
        double bal = economy.getBalance(target);
        viewer.sendMessage(ECO_PREFIX + "§7Stan konta §f" + target.getName() + "§7: §e" + economy.format(bal));
    }

    private void handlePay(Player sender, String targetName, String amountRaw) {
        double amount;
        try {
            amount = Double.parseDouble(amountRaw);
            if (amount < 0.01) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ECO_PREFIX + "§cPodaj poprawną kwotę (min. 0.01)!");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        if (!economy.has(sender, amount)) {
            sender.sendMessage(ECO_PREFIX + "§cNie masz wystarczających środków!");
            return;
        }

        economy.withdrawPlayer(sender, amount);
        economy.depositPlayer(target, amount);

        sender.sendMessage(ECO_PREFIX + "§aPrzelałeś §e" + economy.format(amount) + " §7do §f" + target.getName());
        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(ECO_PREFIX + "§aOtrzymałeś §e" + economy.format(amount) + " §7od §f" + sender.getName());
        }
    }

    private boolean handleAdminActions(CommandSender sender, String[] args) {
        if (args.length < 3) return false;

        String action = args[0].toLowerCase();
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        if (!economy.hasAccount(target)) {
            sender.sendMessage(ECO_PREFIX + "§cGracz nie istnieje w bazie danych!");
            return true;
        }

        double value;
        try { value = Double.parseDouble(args[2]); } catch (Exception e) { return false; }

        switch (action) {
            case "set" -> {
                double current = economy.getBalance(target);
                economy.withdrawPlayer(target, current);
                economy.depositPlayer(target, value);
            }
            case "add" -> economy.depositPlayer(target, value);
            case "take" -> economy.withdrawPlayer(target, value);
            default -> { return false; }
        }

        sender.sendMessage(ECO_PREFIX + "§7Zaktualizowano balans §f" + target.getName() + "§7: §e" + economy.format(economy.getBalance(target)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> matches = new ArrayList<>();
            matches.add("pay");
            if (sender.hasPermission("economy.admin")) {
                matches.addAll(List.of("set", "add", "take"));
            }
            return matches.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        return (args.length == 2) ? null : Collections.emptyList();
    }
}