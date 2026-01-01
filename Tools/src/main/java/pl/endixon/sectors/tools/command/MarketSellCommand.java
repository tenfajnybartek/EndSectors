package pl.endixon.sectors.tools.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pl.endixon.sectors.tools.Main;
import pl.endixon.sectors.tools.user.profile.PlayerMarketProfile;
import pl.endixon.sectors.tools.user.profile.PlayerProfile;
import pl.endixon.sectors.tools.user.profile.ProfileCache;
import pl.endixon.sectors.tools.utils.PlayerDataSerializerUtil;

import java.util.List;

public class MarketSellCommand implements CommandExecutor {

    private final Main plugin = Main.getInstance();
    private static final String MARKET_PREFIX = "§8[§6Market§8] ";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length < 1) {
            player.sendMessage(MARKET_PREFIX + "§7Poprawne użycie: §e/wystaw <cena>");
            return true;
        }

        final PlayerProfile profile = ProfileCache.get(player.getUniqueId());
        if (profile == null) return true;

        this.handleSellAction(player, profile, args[0]);
        return true;
    }

    private void handleSellAction(Player player, PlayerProfile profile, String priceRaw) {
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
            player.sendMessage(MARKET_PREFIX + "§cPodana cena nie jest liczbą!");
            return;
        }

        final List<PlayerMarketProfile> activeOffers = plugin.getMarketRepository().findBySeller(player.getUniqueId());
        final int limit = this.getMarketLimit(player);

        if (activeOffers.size() >= limit) {
            player.sendMessage(MARKET_PREFIX + "§cOsiągnąłeś limit ofert (§e" + activeOffers.size() + "§8/§e" + limit + "§c)!");
            return;
        }

        final String resolvedName = resolveItemName(itemInHand);
        final String category = determineCategory(itemInHand.getType());
        final String itemData = PlayerDataSerializerUtil.serializeItemStacksToBase64(new ItemStack[]{itemInHand});

        plugin.getMarketRepository().listOffer(profile, itemData, resolvedName, category, price);
        player.getInventory().setItemInMainHand(null);
        player.sendMessage(MARKET_PREFIX + "§aWystawiono §f" + resolvedName + " §aza §e" + price + "$");
    }

    private int getMarketLimit(@NotNull Player player) {
        if (player.hasPermission("market.limit.unlimited")) return 1000;
        if (player.hasPermission("market.limit.vip")) return 15;
        if (player.hasPermission("market.limit.player")) return 10;
        return 5;
    }

    private String resolveItemName(ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            final Component displayName = meta.displayName();
            if (displayName != null) return LegacyComponentSerializer.legacySection().serialize(displayName);
        }
        return item.getType().name().replace("_", " ").toLowerCase();
    }

    private String determineCategory(Material m) {
        final String name = m.name();
        if (name.contains("SWORD") || name.contains("BOW") || name.contains("AXE")) return "WEAPONS";
        if (name.contains("PICKAXE") || name.contains("SHOVEL") || name.contains("HOE")) return "TOOLS";
        if (name.contains("HELMET") || name.contains("CHESTPLATE") || name.contains("LEGGINGS") || name.contains("BOOTS")) return "ARMOR";
        return m.isBlock() ? "BLOCKS" : "OTHER";
    }
}