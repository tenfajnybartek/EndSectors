/*
 *
 *  EndSectors  Non-Commercial License
 *  (c) 2025 Endixon
 *
 *  Permission is granted to use, copy, and
 *  modify this software **only** for personal
 *  or educational purposes.
 *
 *  Commercial use, redistribution, claiming
 *  this work as your own, or copying code
 *  without explicit permission is strictly
 *  prohibited.
 *
 *  Visit https://github.com/Endixon/EndSectors
 *  for more info.
 *
 */

package pl.endixon.sectors.paper.inventory.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.endixon.sectors.common.util.ChatUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class WindowUI {

    private final Inventory inventory;
    private final Map<Integer, Consumer<InventoryClickEvent>> actions = new HashMap<>();

    public WindowUI(String title, int rows) {
        this.inventory = Bukkit.createInventory(null, Math.min(rows * 9, 54), ChatUtil.fixColors(title));
    }

    public void setSlot(int slot, ItemStack item, Consumer<InventoryClickEvent> onClick) {
        actions.put(slot, onClick);
        inventory.setItem(slot, item);
    }

    public void handleClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (actions.containsKey(slot)) {
            actions.get(slot).accept(event);
        }
    }

    public void openFor(HumanEntity player) {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
