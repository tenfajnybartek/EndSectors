package pl.endixon.sectors.tools.market.render;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import pl.endixon.sectors.tools.inventory.api.builder.StackBuilder;
import pl.endixon.sectors.tools.market.utils.MarketItemUtil;
import pl.endixon.sectors.tools.user.profile.PlayerMarketProfile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class MarketItemRenderer {


    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacySection();

    private MarketItemRenderer() {}


    private static String hex(String text) {
        return SERIALIZER.serialize(MM.deserialize(text));
    }

    public static StackBuilder prepareBuyItem(@NotNull PlayerMarketProfile offer, @NotNull ItemStack originalItem) {
        StackBuilder builder = new StackBuilder(originalItem);


        String sep = hex("  ");
        builder.lore(sep);
        builder.lore(hex("<gray>Sprzedawca: <yellow>" + offer.getSellerName()));
        builder.lore(hex("<gray>Cena: <gradient:#55ff55:#00aa00><bold>" + offer.getPrice() + "$</bold></gradient>"));
        builder.lore(hex("<gray>Wygasa za: ") + MarketItemUtil.formatTimeLeft(offer.getCreatedAt()));
        builder.lore(" ");
        builder.lore(hex("<gradient:#ffaa00:#ffff55>Kliknij, aby zakupić!</gradient>"));
        builder.lore(sep);
        return builder;
    }

    public static StackBuilder prepareManageItem(@NotNull PlayerMarketProfile offer, @NotNull ItemStack originalItem) {
        StackBuilder builder = new StackBuilder(originalItem);
        String sep = hex("  ");

        builder.lore(sep);
        builder.lore(hex("<gray>Cena: <green>" + offer.getPrice() + "$"));
        builder.lore(hex("<gray>Wystawiono: <white>" + MarketItemUtil.formatElapsedTime(offer.getCreatedAt())));
        builder.lore(hex("<gray>Pozostało: ") + MarketItemUtil.formatTimeLeft(offer.getCreatedAt()));
        builder.lore(" ");
        builder.lore(hex("<red>Kliknij, aby wycofać ofertę!"));
        builder.lore(sep);
        return builder;
    }

    public static StackBuilder prepareStorageItem(@NotNull PlayerMarketProfile offer, @NotNull ItemStack originalItem) {
        StackBuilder builder = new StackBuilder(originalItem);
        String sep = hex("  ");

        builder.lore(sep);
        builder.lore(hex("<gray>Status: <#ff3333><bold>Przedmiot wygasł</bold>"));
        builder.lore(hex("<gray>Wystawiono: <white>" + MarketItemUtil.formatElapsedTime(offer.getCreatedAt())));
        builder.lore(" ");
        builder.lore(hex("<yellow>Kliknij, aby odebrać przedmiot!"));
        builder.lore(sep);
        return builder;
    }


    public static StackBuilder prepareMyOffersIcon(int activeOffersCount) {
        return new StackBuilder(new ItemStack(Material.BOOK))
                .name(hex("<gold>Twoje aukcje"))
                .lore(hex("<gray>Aktywne: <white>" + activeOffersCount))
                .lore(" ")
                .lore(hex("<yellow>Kliknij, aby zarządzać!"));
    }


    public static StackBuilder prepareClaimableItem(@NotNull PlayerMarketProfile offer, @NotNull ItemStack originalItem) {
        StackBuilder builder = new StackBuilder(originalItem);
        builder.lore("  ");
        builder.lore("§7Status: §bDo odebrania");
        builder.lore("§7Powód: §fZakup / Brak miejsca");
        builder.lore(" ");
        builder.lore("§aKliknij, aby odebrać przedmiot!");
        builder.lore("  ");
        return builder;
    }

    public static StackBuilder prepareClaimableIcon(int claimableCount) {
        String countColor = claimableCount > 0 ? "§b" : "§7";

        return new StackBuilder(new ItemStack(Material.ENDER_CHEST))
                .name("§bSkrzynka Odbiorcza")
                .lore("§7Do odebrania: " + countColor + claimableCount)
                .lore(" ")
                .lore("§eKliknij, aby odebrać!");
    }


    public static StackBuilder prepareStorageIcon(int expiredCount) {
        String countColor = expiredCount > 0 ? "<#ff3333>" : "<white>";
        return new StackBuilder(new ItemStack(Material.CHEST))
                .name(hex("<#ff5555>Magazyn (Wygasłe)"))
                .lore(hex("<gray>Do odebrania: " + countColor + expiredCount))
                .lore(" ")
                .lore(hex("<yellow>Kliknij, aby odebrać!"));
    }
}