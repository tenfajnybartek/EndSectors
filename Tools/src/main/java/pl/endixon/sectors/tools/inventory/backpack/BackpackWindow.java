package pl.endixon.sectors.tools.inventory.backpack;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.ClickType;
import pl.endixon.sectors.tools.backpack.BackpackService;
import pl.endixon.sectors.tools.backpack.type.BackpackUpgradeResult;
import pl.endixon.sectors.tools.backpack.utils.BackpackUtils;
import pl.endixon.sectors.tools.inventory.api.WindowUI;
import pl.endixon.sectors.tools.inventory.api.builder.StackBuilder;
import pl.endixon.sectors.tools.backpack.render.BackpackItemRenderer;
import pl.endixon.sectors.tools.user.profile.player.PlayerBackpackProfile;
import pl.endixon.sectors.tools.user.profile.player.PlayerProfile;
import pl.endixon.sectors.tools.utils.PlayerDataSerializerUtil;

public class BackpackWindow {

    private final Player player;
    private final PlayerProfile profile;
    private final PlayerBackpackProfile backpack;
    private final int page;
    private final BackpackService service;
    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final double PAGE_COST = 50000.0;
    private static final double RENEW_COST = 25000.0;

    public BackpackWindow(Player player, PlayerProfile profile, PlayerBackpackProfile backpack, int page, BackpackService service) {
        this.player = player;
        this.profile = profile;
        this.backpack = backpack;
        this.page = page;
        this.service = service;
        this.open();
    }

    public void open() {
        final int unlocked = this.service.getMaxPages(this.player, this.backpack);
        final int currentPage = Math.min(this.page, unlocked + 1);

        boolean isActive = (currentPage == 1) || (currentPage <= unlocked && this.service.isPageActive(this.backpack, currentPage));
        if (this.player.hasPermission("endsectors.backpack.admin")) isActive = true;

        final WindowUI window = new WindowUI("<gradient:#00d2ff:#3a7bd5><bold>BACKPACK</bold></gradient> <#a8a8a8>P:" + currentPage, 6);

        window.setOnClose(event -> {
            this.handleClose(event.getInventory());
        });

        window.setInteractionAllowed(false);

        if (isActive && currentPage <= unlocked) {
            final String base64 = this.backpack.getPages().getOrDefault(String.valueOf(currentPage), "");
            final ItemStack[] items = PlayerDataSerializerUtil.deserializeItemStacksFromBase64(base64);
            for (int i = 0; i < 45; i++) {
                if (items.length > i && items[i] != null) window.getInventory().setItem(i, items[i]);
            }
        }

        this.setupNavigation(window, unlocked, isActive, currentPage);
        window.openFor(this.player);
    }

    public void handleClose(Inventory inventory) {
        int unlocked = this.service.getMaxPages(this.player, this.backpack);
        boolean isActive = (page == 1) || (page <= unlocked && this.service.isPageActive(this.backpack, page));
        if (this.player.hasPermission("endsectors.backpack.admin")) isActive = true;

        if (isActive && inventory != null) {
            BackpackUtils.updateBackpackFromInventory(inventory, this.backpack, page);
            this.service.saveBackpack(this.backpack);
        }
    }

    private void setupNavigation(WindowUI window, int maxPages, boolean isActive, int currentPage) {
        final ItemStack filler = new StackBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)).name(" ").build();
        for (int i = 45; i < 54; i++) window.setSlot(i, filler, e -> e.setCancelled(true));

        if (currentPage > 1) {
            window.setSlot(45, new StackBuilder(new ItemStack(Material.ARROW)).name("<#00d2ff>← Poprzednia").build(), e -> {
                if (isActive) {
                    BackpackUtils.updateBackpackFromInventory(e.getInventory(), this.backpack, currentPage);
                    this.service.saveBackpack(this.backpack);
                }
                new BackpackWindow(this.player, this.profile, this.backpack, currentPage - 1, this.service);
            });
        }


        if (isActive) {
            window.setSlot(48, BackpackItemRenderer.prepareEditModeButton(false).build(), e -> {
                window.setInteractionAllowed(true);
                this.player.playSound(this.player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1.0f, 1.2f);
                this.player.sendMessage(MM.deserialize("<#00ff87>[!] Plecak został odblokowany do edycji."));
                window.setSlot(48, BackpackItemRenderer.prepareEditModeButton(true).build(), click -> {
                    this.player.closeInventory();
                    this.player.sendMessage(MM.deserialize("<#00ff87>[!] Zmiany zostały zapisane pomyślnie."));
                });
            });
        }


        int tempExpiredCount = 0;
        for (int i = 2; i <= maxPages; i++) {
            if (!this.service.isPageActive(this.backpack, i)) tempExpiredCount++;
        }
        final int expiredCount = tempExpiredCount;
        final double bulkCost = expiredCount * RENEW_COST;
        final long exp = this.backpack.getPageExpirations().getOrDefault(String.valueOf(currentPage), 0L);
        final boolean needsRenew = (currentPage != 1) && !isActive && currentPage <= maxPages && !this.player.hasPermission("endsectors.backpack.admin");

        window.setSlot(49, BackpackItemRenderer.prepareInfoIcon(currentPage, maxPages, this.profile.getBalance(), exp, expiredCount, bulkCost).build(), e -> {
            if (expiredCount > 0 && (e.getClick() == ClickType.MIDDLE || e.isShiftClick())) {
                if (this.service.processBulkSubscription(this.player, this.profile, this.backpack, RENEW_COST) == BackpackUpgradeResult.SUCCESS) {
                    this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                    this.open();
                }
            }
        });

        window.setSlot(50, BackpackItemRenderer.prepareBreachWarning(needsRenew, RENEW_COST, this.profile.getBalance(), expiredCount).build(), e -> {
            if (needsRenew && e.isLeftClick()) {
                if (this.service.processSubscription(this.profile, this.backpack, currentPage, RENEW_COST) == BackpackUpgradeResult.SUCCESS) {
                    this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                    this.open();
                }
            }
        });

        if (currentPage < maxPages) {
            window.setSlot(53, new StackBuilder(new ItemStack(Material.ARROW)).name("<#00d2ff>Następna →").build(), e -> {
                if (isActive) {
                    BackpackUtils.updateBackpackFromInventory(e.getInventory(), this.backpack, currentPage);
                    this.service.saveBackpack(this.backpack);
                }
                new BackpackWindow(this.player, this.profile, this.backpack, currentPage + 1, this.service);
            });
        } else if (currentPage == maxPages && maxPages < 18) {
            window.setSlot(53, BackpackItemRenderer.prepareUpgradeButton(maxPages + 1, PAGE_COST, this.profile.getBalance()).build(), e -> {
                if (this.service.processPageUpgrade(this.profile, this.backpack, PAGE_COST, maxPages) == BackpackUpgradeResult.SUCCESS) {
                    this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                    new BackpackWindow(this.player, this.profile, this.backpack, maxPages + 1, this.service);
                }
            });
        }
    }
}