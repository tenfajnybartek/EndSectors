package pl.endixon.sectors.tools.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.endixon.sectors.tools.Main;
import pl.endixon.sectors.tools.inventory.api.WindowUI;
import pl.endixon.sectors.tools.inventory.api.builder.StackBuilder;
import pl.endixon.sectors.tools.user.Repository.MarketRepository;
import pl.endixon.sectors.tools.user.profile.PlayerProfile;
import pl.endixon.sectors.tools.user.profile.PlayerMarketProfile;
import pl.endixon.sectors.tools.user.profile.ProfileMarketCache;
import pl.endixon.sectors.tools.utils.PlayerDataSerializerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarketWindow {
    private final Player player;
    private final PlayerProfile profile;
    private final Main plugin = Main.getInstance();
    private final String category;
    private final int page;

    public MarketWindow(Player player, PlayerProfile profile, String category, int page) {
        this.player = player;
        this.profile = profile;
        this.category = category;
        this.page = page;
        open();
    }

    public void open() {
        WindowUI window = new WindowUI("Rynek: " + category + " (" + (page + 1) + ")", 6);

        List<PlayerMarketProfile> offers = category.equalsIgnoreCase("ALL")
                ? new ArrayList<>(ProfileMarketCache.getValues())
                : ProfileMarketCache.getByCategory(category);

        offers.sort((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));

        int start = page * 45;
        int end = Math.min(start + 45, offers.size());

        for (int i = start; i < end; i++) {
            PlayerMarketProfile offer = offers.get(i);
            int slot = i - start;
            ItemStack originalItem = PlayerDataSerializerUtil.deserializeItemStacksFromBase64(offer.getItemData())[0];
            StackBuilder builder = getStackBuilder(offer, originalItem);
            window.setSlot(slot, builder.build(), event -> {
                MarketRepository.PurchaseResult result = plugin.getMarketRepository().buyOffer(
                        offer.getId(), profile, plugin.getRepository()
                );

                switch (result) {
                    case SUCCESS -> {
                        ItemStack purchasedItem = PlayerDataSerializerUtil.deserializeItemStacksFromBase64(offer.getItemData())[0];
                        Map<Integer, ItemStack> leftOver = player.getInventory().addItem(purchasedItem);
                        if (!leftOver.isEmpty()) {
                            leftOver.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
                            player.sendMessage("§8[§6Rynek§8] §7Ekwipunek pełny! Przedmiot wyrzucono pod nogi.");
                        }

                        player.sendMessage("§8[§6Rynek§8] §aZakupiono: §f" + offer.getItemName());
                        player.closeInventory();
                    }
                    case NOT_ENOUGH_MONEY ->
                            player.sendMessage("§8[§6Rynek§8] §cNie masz tyle pieniędzy!");
                    case ALREADY_SOLD -> {
                        player.sendMessage("§8[§6Rynek§8] §cPrzedmiot został już sprzedany!");
                        open();
                    }
                    case NOT_FOUND -> {
                        player.sendMessage("§8[§6Rynek§8] §cOferta wygasła.");
                        open();
                    }
                }
            });
        }

        if (page > 0) {
            window.setSlot(45, new StackBuilder(new ItemStack(Material.ARROW))
                            .name("§c« Poprzednia strona").build(),
                    event -> new MarketWindow(player, profile, category, page - 1));
        }

        window.setSlot(46, new StackBuilder(new ItemStack(Material.GRASS_BLOCK)).name("§aBloki").build(),
                event -> new MarketWindow(player, profile, "BLOCKS", 0));

        window.setSlot(47, new StackBuilder(new ItemStack(Material.DIAMOND_SWORD)).name("§bEkwipunek").build(),
                event -> new MarketWindow(player, profile, "WEAPONS", 0));

        window.setSlot(48, new StackBuilder(new ItemStack(Material.NETHER_STAR)).name("§fWszystko").build(),
                event -> new MarketWindow(player, profile, "ALL", 0));

        int myOffers = plugin.getMarketRepository().findBySeller(player.getUniqueId()).size();
        window.setSlot(49, new StackBuilder(new ItemStack(Material.BOOK))
                .name("§eTwoje aukcje")
                .lore("§7Aktywne: §f" + myOffers)
                .lore(" ")
                .lore("§eKliknij, aby zarządzać!")
                .build(), event -> new MarketMyOffersWindow(player, profile));

        window.setSlot(50, new StackBuilder(new ItemStack(Material.CHEST)).name("§6Inne").build(),
                event -> new MarketWindow(player, profile, "OTHER", 0));

        window.setSlot(51, new StackBuilder(new ItemStack(Material.BARRIER)).name("§cZamknij").build(),
                event -> player.closeInventory());

        if (offers.size() > end) {
            window.setSlot(53, new StackBuilder(new ItemStack(Material.ARROW))
                            .name("§aNastępna strona »").build(),
                    event -> new MarketWindow(player, profile, category, page + 1));
        }

        player.openInventory(window.getInventory());
    }

    private static @NotNull StackBuilder getStackBuilder(PlayerMarketProfile offer, ItemStack originalItem) {
        StackBuilder builder = new StackBuilder(originalItem);
        builder.lore("§8§m-----------------------");
        builder.lore("§7Sprzedawca: §e" + offer.getSellerName());
        builder.lore("§7Cena: §a" + offer.getPrice() + "$");
        builder.lore(" ");
        builder.lore("§eKliknij, aby zakupić!");
        builder.lore("§8§m-----------------------");
        return builder;
    }
}