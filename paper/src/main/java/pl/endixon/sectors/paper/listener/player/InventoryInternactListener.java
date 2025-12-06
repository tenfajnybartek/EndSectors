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

package pl.endixon.sectors.paper.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import pl.endixon.sectors.paper.inventory.api.builder.WindowHolder;

public class InventoryInternactListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if (!isWindowInventory(inv) && !isWindowInventory(event.getInventory())) return;

        WindowHolder holder = (WindowHolder) event.getInventory().getHolder();
        event.setCancelled(true);
        holder.processClick(event);
    }

    @EventHandler
    public void onInteract(InventoryInteractEvent event) {
        if (!isWindowInventory(event.getInventory())) return;
        event.setCancelled(true);
    }

    private boolean isWindowInventory(Inventory inventory) {
        return inventory != null
                && inventory.getType() == InventoryType.CHEST
                && inventory.getHolder() instanceof WindowHolder
                && inventory.getHolder().getClass() == WindowHolder.class;
    }
}
