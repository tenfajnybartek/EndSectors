/*
 *
 *  EndSectors  Non-Commercial License
 *  (c) 2025 Endixon
 *
 *  Permission is granted to use, copy, and
 *  modify this software **only** for personal
 *  or educational purposes.
 *
 *   Commercial use, redistribution, claiming
 *  this work as your own, or copying code
 *  without explicit permission is strictly
 *  prohibited.
 *
 *  Visit https://github.com/Endixon/EndSectors
 *  for more info.
 *
 */

package pl.endixon.sectors.tools.inventory.api.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import pl.endixon.sectors.common.util.ChatUtil;

public class StackBuilder {

    private final ItemStack stack;

    public StackBuilder(ItemStack stack) {
        this.stack = stack;
    }

    public StackBuilder type(Material material) {
        stack.setType(material);
        return this;
    }

    public StackBuilder amount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public StackBuilder durability(int dur) {
        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof Damageable dmg) {
            dmg.setDamage(dur);
            stack.setItemMeta(meta);
        }
        return this;
    }

    public StackBuilder addUnsafe(Enchantment enchant, int level) {
        stack.addUnsafeEnchantment(enchant, level);
        return this;
    }

    public StackBuilder removeEnchant(Enchantment enchant) {
        stack.removeEnchantment(enchant);
        return this;
    }

    public StackBuilder addEnchant(Enchantment enchant, int level) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.addEnchant(enchant, level, true);
            stack.setItemMeta(meta);
        }
        return this;
    }

    public StackBuilder addAllEnchants(Map<Enchantment, Integer> enchants) {
        stack.addEnchantments(enchants);
        return this;
    }

    public StackBuilder name(String name) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            Component display = Component.text(ChatUtil.fixColors(name));
            meta.displayName(display);
            stack.setItemMeta(meta);
        }
        return this;
    }

    public String getName() {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return "";

        Component display = meta.displayName();
        if (display == null) return "";

        return LegacyComponentSerializer.legacyAmpersand().serialize(display);
    }


    public StackBuilder lore(String line) {
        return lore(line, -1);
    }

    public StackBuilder lore(String line, int index) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return this;

        List<Component> currentLore = meta.lore();
        List<Component> lore;
        if (currentLore != null) {
            lore = new ArrayList<>(currentLore);
        } else {
            lore = new ArrayList<>();
        }

        Component coloredLine = Component.text(ChatUtil.fixColors(line));

        if (index >= 0 && index < lore.size()) {
            lore.set(index, coloredLine);
        } else {
            lore.add(coloredLine);
        }

        meta.lore(lore);
        stack.setItemMeta(meta);
        return this;
    }


    public StackBuilder lores(List<String> lines) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return this;

        List<Component> loreComponents = lines.stream()
                .map(line -> Component.text(ChatUtil.fixColors(line)))
                .collect(Collectors.toList());

        meta.lore(loreComponents);
        stack.setItemMeta(meta);
        return this;
    }


    public StackBuilder glow(boolean enable) {
        if (!enable) return this;

        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            stack.setItemMeta(meta);
        }
        return this;
    }

    public StackBuilder addFlag(ItemFlag flag) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(flag);
            stack.setItemMeta(meta);
        }
        return this;
    }

    public ItemStack build() {
        return stack;
    }
}
