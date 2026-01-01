package pl.endixon.sectors.tools.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.endixon.sectors.tools.EndSectorsToolsPlugin;
import pl.endixon.sectors.tools.market.utils.MarketItemUtil;
import pl.endixon.sectors.tools.user.profile.PlayerMarketProfile;
import pl.endixon.sectors.tools.user.profile.PlayerProfile;
import pl.endixon.sectors.tools.user.profile.ProfileCache;
import pl.endixon.sectors.tools.utils.PlayerDataSerializerUtil;

import java.util.List;

public class MarketSellCommand implements CommandExecutor {

    private final EndSectorsToolsPlugin plugin = EndSectorsToolsPlugin.getInstance();
    private static final String MARKET_PREFIX = "§8[§6Market§8] ";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Komenda tylko dla graczy.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(MARKET_PREFIX + "§7Poprawne użycie: §e/wystaw <cena>");
            return true;
        }

        final PlayerProfile profile = ProfileCache.get(player.getUniqueId());
        if (profile == null) {
            player.sendMessage("§cTwój profil nie został jeszcze załadowany.");
            return true;
        }

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
            if (price > 1_000_000_000) {
                player.sendMessage(MARKET_PREFIX + "§cCena jest zbyt wysoka!");
                return;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(MARKET_PREFIX + "§cPodana cena nie jest poprawną liczbą!");
            return;
        }


        final List<PlayerMarketProfile> activeOffers = plugin.getMarketRepository().findBySeller(player.getUniqueId());
        final int limit = plugin.getMarketService().getMarketLimit(player);

        if (activeOffers.size() >= limit) {
            player.sendMessage(MARKET_PREFIX + "§cOsiągnąłeś limit ofert (§e" + activeOffers.size() + "§8/§e" + limit + "§c)!");
            return;
        }

        final String resolvedName = MarketItemUtil.resolveItemName(itemInHand);
        final String category = MarketItemUtil.determineCategory(itemInHand.getType());
        final String itemData = PlayerDataSerializerUtil.serializeItemStacksToBase64(new ItemStack[]{itemInHand});

        plugin.getMarketService().listOffer(profile, itemData, resolvedName, category, price);
        player.getInventory().setItemInMainHand(null);

        player.sendMessage(MARKET_PREFIX + "§aPomyślnie wystawiono przedmiot: §f" + resolvedName);
        player.sendMessage(MARKET_PREFIX + "§7Cena: §e" + price + "$ §8| §7Kategoria: §b" + category);
    }
}