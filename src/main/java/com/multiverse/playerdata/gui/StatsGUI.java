package com.multiverse.playerdata.gui;

import com.multiverse.playerdata.models.PlayerStats;
import com.multiverse.playerdata.models.enums.StatType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;

import java.util.*;

public class StatsGUI implements Listener {

    private final String GUI_TITLE = "플레이어 스탯";
    private final int SIZE = 36;

    public void open(Player player, PlayerStats stats) {
        Inventory inv = Bukkit.createInventory(player, SIZE, GUI_TITLE);

        int slot = 10;
        for (StatType type : StatType.values()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e" + type.name());
            List<String> lore = new ArrayList<>();
            lore.add("현재 값: " + stats.getBaseStats().getOrDefault(type, 0));
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);
            // 클릭 시 스탯 증감/설명 등 확장 가능
        }
    }
}