/*
 *
 * EndSectors – Non-Commercial License
 * (c) 2025 Endixon
 *
 * Permission is granted to use, copy, and
 * modify this software **only** for personal
 * or educational purposes.
 *
 * Commercial use, redistribution, claiming
 * this work as your own, or copying code
 * without explicit permission is strictly
 * prohibited.
 *
 * Visit https://github.com/Endixon/EndSectors
 * for more info.
 *
 */

package pl.endixon.sectors.paper.inventory.api;

import java.util.function.Consumer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.endixon.sectors.paper.inventory.api.builder.WindowHolder;
import pl.endixon.sectors.paper.util.ChatAdventureUtil;

public class WindowUI {

    private static final ChatAdventureUtil CHAT_UTIL = new ChatAdventureUtil();
    private final WindowHolder holder;

    public WindowUI(String title, int rows) {
        this.holder = new WindowHolder();
        this.holder.setInventory(Bukkit.createInventory(holder, Math.min(rows * 9, 54), CHAT_UTIL.toComponent(title)));
    }

    public void setSlot(int slot, ItemStack item, Consumer<InventoryClickEvent> onClick) {
        holder.assignAction(slot, onClick);
        holder.getInventory().setItem(slot, item);
    }

    public void handleClick(InventoryClickEvent event) {
        holder.processClick(event);
    }

    public void openFor(HumanEntity player) {
        player.openInventory(holder.getInventory());
    }

    public Inventory getInventory() {
        return holder.getInventory();
    }
}
