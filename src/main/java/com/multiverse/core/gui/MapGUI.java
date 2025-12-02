package com.multiverse.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MapGUI {

    private static final String GUI_TITLE = "월드 맵";

    public static Inventory createGUI(Player player, List<String> discoveredRegions) {
        int slots = 27;
        Inventory inv = Bukkit.createInventory(player, slots, GUI_TITLE);

        // 모든 지역 아이템을 생성하여 인벤토리에 배치
        int slot = 0;
        for (String regionName : discoveredRegions) {
            ItemStack regionItem = new ItemStack(Material.MAP);
            ItemMeta meta = regionItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(regionName);
                regionItem.setItemMeta(meta);
            }
            inv.setItem(slot, regionItem);
            slot++;
            if (slot >= slots) break;
        }
        return inv;
    }
}