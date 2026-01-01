package pl.endixon.sectors.tools.market.render;

import org.jetbrains.annotations.NotNull;
import pl.endixon.sectors.tools.inventory.api.builder.StackBuilder;
import pl.endixon.sectors.tools.market.utils.MarketItemUtil;
import pl.endixon.sectors.tools.user.profile.PlayerMarketProfile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class MarketItemRenderer {

    private MarketItemRenderer() {}

    public static StackBuilder prepareBuyItem(@NotNull PlayerMarketProfile offer, @NotNull ItemStack originalItem) {
        StackBuilder builder = new StackBuilder(originalItem);
        builder.lore("§8§m-----------------------");
        builder.lore("§7Sprzedawca: §e" + offer.getSellerName());
        builder.lore("§7Cena: §a" + offer.getPrice() + "$");
        builder.lore("§7Wygasa za: " + MarketItemUtil.formatTimeLeft(offer.getCreatedAt()));
        builder.lore(" ");
        builder.lore("§eKliknij, aby zakupić!");
        builder.lore("§8§m-----------------------");
        return builder;
    }

    public static StackBuilder prepareManageItem(@NotNull PlayerMarketProfile offer, @NotNull ItemStack originalItem) {
        StackBuilder builder = new StackBuilder(originalItem);
        builder.lore("§8§m-----------------------");
        builder.lore("§7Cena: §a" + offer.getPrice() + "$");
        builder.lore("§7Wystawiono: §f" + MarketItemUtil.formatElapsedTime(offer.getCreatedAt()));
        builder.lore("§7Pozostało: " + MarketItemUtil.formatTimeLeft(offer.getCreatedAt()));
        builder.lore(" ");
        builder.lore("§cKliknij, aby wycofać ofertę!");
        builder.lore("§8§m-----------------------");
        return builder;
    }

    public static StackBuilder prepareStorageItem(@NotNull PlayerMarketProfile offer, @NotNull ItemStack originalItem) {
        StackBuilder builder = new StackBuilder(originalItem);
        builder.lore("§8§m-----------------------");
        builder.lore("§cStatus: §4Oferta wygasła");
        builder.lore("§7Wystawiono: §f" + MarketItemUtil.formatElapsedTime(offer.getCreatedAt()));
        builder.lore(" ");
        builder.lore("§eKliknij, aby odebrać przedmiot!");
        builder.lore("§8§m-----------------------");
        return builder;
    }
    public static StackBuilder prepareMyOffersIcon(int activeOffersCount) {
        return new StackBuilder(new ItemStack(Material.BOOK))
                .name("§eTwoje aukcje")
                .lore("§7Aktywne: §f" + activeOffersCount)
                .lore(" ")
                .lore("§eKliknij, aby zarządzać!");
    }

    public static StackBuilder prepareStorageIcon(int expiredCount) {
        return new StackBuilder(new ItemStack(Material.CHEST))
                .name("§cMagazyn (Wygasłe)")
                .lore("§7Do odebrania: §c" + expiredCount)
                .lore(" ")
                .lore("§eKliknij, aby odebrać!");
    }
}