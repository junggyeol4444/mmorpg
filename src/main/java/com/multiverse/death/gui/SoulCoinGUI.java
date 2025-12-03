package com.multiverse.death.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * 소울 코인 상점 GUI를 생성 및 관리하는 클래스
 */
public class SoulCoinShopGUI {

    public static Inventory createShopGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "소울 코인 상점");
        // TODO: 구매 가능한 아이템(예: 부활권, 보험권 등)을 채워넣는다.
        gui.setItem(11, new ItemStack(Material.TOTEM_OF_UNDYING)); // 예시: 부활권
        gui.setItem(13, new ItemStack(Material.GOLD_INGOT));       // 예시: 소울 코인 번들
        gui.setItem(15, new ItemStack(Material.SHIELD));           // 예시: 보험권
        return gui;
    }

    public static void openShop(Player player) {
        Inventory gui = createShopGUI(player);
        player.openInventory(gui);
    }
}