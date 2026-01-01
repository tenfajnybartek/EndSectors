package pl.endixon.sectors.tools.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.endixon.sectors.tools.Main;
import pl.endixon.sectors.tools.inventory.MarketWindow;
import pl.endixon.sectors.tools.user.profile.PlayerProfile;
import pl.endixon.sectors.tools.user.profile.ProfileCache;
import pl.endixon.sectors.tools.user.profile.PlayerMarketProfile;
import pl.endixon.sectors.tools.utils.PlayerDataSerializerUtil;
import pl.endixon.sectors.tools.utils.MessagesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MarketCommand implements CommandExecutor, TabCompleter {

    private final Main plugin = Main.getInstance();
    private static final String MARKET_PREFIX = "§8[§6Rynek§8] ";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessagesUtil.CONSOLE_BLOCK.get());
            return true;
        }

        final PlayerProfile profile = ProfileCache.get(player.getUniqueId());
        if (profile == null) {
            player.sendMessage(MessagesUtil.PLAYERDATANOT_FOUND_MESSAGE.get());
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("wystaw")) {
            this.handleListAction(player, profile, args[1]);
            return true;
        }

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("otworz"))) {
            new MarketWindow(player, profile, "ALL", 0);
            return true;
        }

        this.sendHelpMessage(player);
        return true;
    }

    private void handleListAction(@NotNull Player player, @NotNull PlayerProfile profile, @NotNull String priceRaw) {
        final ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.AIR) {
            player.sendMessage(MARKET_PREFIX + "§cMusisz trzymać przedmiot w ręce!");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceRaw);
            if (price <= 0) {
                player.sendMessage(MARKET_PREFIX + "§cCena musi być większa niż 0!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(MARKET_PREFIX + "§cPodana cena nie jest poprawną liczbą!");
            return;
        }

        final List<PlayerMarketProfile> activeOffers = plugin.getMarketRepository().findBySeller(player.getUniqueId());
        final int limit = getMarketLimit(player);

        if (activeOffers.size() >= limit) {
            player.sendMessage(MARKET_PREFIX + "§cOsiągnąłeś limit ofert (§e" + activeOffers.size() + "§8/§e" + limit + "§c)!");
            return;
        }


        final String resolvedName = this.resolveItemName(itemInHand);
        final String category = this.determineCategory(itemInHand.getType());
        final String itemData = PlayerDataSerializerUtil.serializeItemStacksToBase64(new ItemStack[]{itemInHand});

        plugin.getMarketRepository().listOffer(profile, itemData, resolvedName, category, price);
        player.getInventory().setItemInMainHand(null);
        player.sendMessage(MARKET_PREFIX + "§aWystawiono §f" + resolvedName + " §aza §e" + price + "$ §7(Kategoria: " + category + ")");
    }


    private @NotNull String resolveItemName(@NotNull ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            final Component displayName = meta.displayName();
            if (displayName != null) {
                return LegacyComponentSerializer.legacySection().serialize(displayName);
            }
        }
        return item.getType().name().replace("_", " ").toLowerCase();
    }

    private void sendHelpMessage(@NotNull Player player) {
        player.sendMessage("§8§m-------§r §6§lRYNEK §8§m-------");
        player.sendMessage(" §e/market §7- Otwiera menu główne");
        player.sendMessage(" §e/market otworz §7- Przeglądaj oferty");
        player.sendMessage(" §e/market wystaw <cena> §7- Sprzedaj przedmiot");
        player.sendMessage("§8§m-----------------------");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            final List<String> options = List.of("wystaw", "otworz");
            return options.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("wystaw")) {
            return Collections.singletonList("<cena>");
        }
        return Collections.emptyList();
    }

    public static int getMarketLimit(@NotNull Player player) {
        if (player.hasPermission("market.limit.unlimited")) return 1000;
        if (player.hasPermission("market.limit.vip")) return 15;
        if (player.hasPermission("market.limit.player")) return 10;
        return 5;
    }

    private @NotNull String determineCategory(@NotNull Material m) {
        final String name = m.name();
        if (name.contains("SWORD") || name.contains("BOW") || name.contains("AXE") || name.contains("CROSSBOW")) return "WEAPONS";
        if (name.contains("PICKAXE") || name.contains("SHOVEL") || name.contains("HOE")) return "TOOLS";
        if (name.contains("HELMET") || name.contains("CHESTPLATE") || name.contains("LEGGINGS") || name.contains("BOOTS")) return "ARMOR";
        return m.isBlock() ? "BLOCKS" : "OTHER";
    }
}