package com.multiverse.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class WaypointGUI {

    private static final String GUI_TITLE = "웨이포인트 목록";

    /**
     * 웨이포인트 GUI를 생성합니다.
     * @param player 대상 플레이어
     * @param waypoints 표시할 웨이포인트 이름 목록
     * @return 생성된 GUI 인벤토리
     */
    public static Inventory createGUI(Player player, List<String> waypoints) {
        int slots = Math.max(9, Math.min(waypoints.size(), 54));
        Inventory inv = Bukkit.createInventory(player, slots, GUI_TITLE);

        int slot = 0;
        for (String wpName : waypoints) {
            ItemStack item = new ItemStack(Material.COMPASS);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(wpName);
                item.setItemMeta(meta);
            }
            inv.setItem(slot, item);
            slot++;
            if (slot >= slots) break;
        }
        return inv;
    }
}