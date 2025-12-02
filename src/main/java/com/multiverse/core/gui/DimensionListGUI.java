package com.multiverse.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class DimensionListGUI {

    private static final String GUI_TITLE = "디멘션 목록";

    /**
     * 디멘션 리스트 GUI를 생성합니다.
     * @param player 대상 플레이어
     * @param dimensions 표시할 디멘션 이름 목록
     * @return 생성된 GUI 인벤토리
     */
    public static Inventory createGUI(Player player, List<String> dimensions) {
        int slots = Math.max(9, Math.min(dimensions.size(), 54));
        Inventory inv = Bukkit.createInventory(player, slots, GUI_TITLE);

        int slot = 0;
        for (String dimName : dimensions) {
            ItemStack item = new ItemStack(Material.ENDER_PEARL);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(dimName);
                item.setItemMeta(meta);
            }
            inv.setItem(slot, item);
            slot++;
            if (slot >= slots) break;
        }
        return inv;
    }
}