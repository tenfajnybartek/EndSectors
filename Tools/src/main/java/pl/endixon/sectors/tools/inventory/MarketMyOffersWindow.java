package pl.endixon.sectors.tools.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.endixon.sectors.tools.Main;
import pl.endixon.sectors.tools.inventory.api.WindowUI;
import pl.endixon.sectors.tools.inventory.api.builder.StackBuilder;
import pl.endixon.sectors.tools.user.profile.PlayerProfile;
import pl.endixon.sectors.tools.user.profile.PlayerMarketProfile;
import pl.endixon.sectors.tools.utils.PlayerDataSerializerUtil;

import java.util.List;
import java.util.Map;

public class MarketMyOffersWindow {
    private final Player player;
    private final PlayerProfile profile;
    private final Main plugin = Main.getInstance();

    public MarketMyOffersWindow(Player player, PlayerProfile profile) {
        this.player = player;
        this.profile = profile;
        open();
    }

    public void open() {
        WindowUI window = new WindowUI("Twoje aukcje", 3);

        List<PlayerMarketProfile> myOffers = plugin.getMarketRepository().findBySeller(player.getUniqueId());

        int slot = 0;
        for (PlayerMarketProfile offer : myOffers) {
            if (slot >= 18) break;

            ItemStack originalItem = PlayerDataSerializerUtil.deserializeItemStacksFromBase64(offer.getItemData())[0];

            StackBuilder builder = new StackBuilder(originalItem);
            builder.lore("§8§m-----------------------");
            builder.lore("§7Cena: §a" + offer.getPrice() + "$");
            builder.lore(" ");
            builder.lore("§cKliknij, aby wycofać!");
            builder.lore("§8§m-----------------------");

            window.setSlot(slot, builder.build(), event -> {
                if (plugin.getMarketRepository().cancelOffer(offer.getId(), player.getUniqueId())) {
                    ItemStack returnedItem = PlayerDataSerializerUtil.deserializeItemStacksFromBase64(offer.getItemData())[0];
                    Map<Integer, ItemStack> leftOver = player.getInventory().addItem(returnedItem);

                    if (!leftOver.isEmpty()) {
                        leftOver.values().forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i));
                        player.sendMessage("§8[§6Rynek§8] §7Brak miejsca, wyrzucono przedmiot pod nogi!");
                    }

                    player.sendMessage("§8[§6Rynek§8] §aPomyślnie wycofano ofertę!");
                    open();
                } else {
                    player.sendMessage("§8[§6Rynek§8] §cNie można wycofać oferty (może została sprzedana?).");
                    player.closeInventory();
                }
            });
            slot++;
        }

        window.setSlot(22, new StackBuilder(new ItemStack(Material.ARROW)).name("§e« Powrót").build(),
                event -> new MarketWindow(player, profile, "ALL", 0));

        player.openInventory(window.getInventory());
    }
}