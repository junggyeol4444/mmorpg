package com.multiverse.death.gui;

import com.multiverse.death.models.enums.InsuranceType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * 보험 관련 GUI를 생성 및 관리하는 클래스
 */
public class InsuranceGUI {

    public static Inventory createInsurancePurchaseGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "보험 구매");
        // TODO: 각 보험 종류에 따라 아이템을 채워넣는다.
        gui.setItem(11, new ItemStack(Material.PAPER)); // 예시: BASIC 보험
        gui.setItem(13, new ItemStack(Material.DIAMOND)); // 예시: PREMIUM 보험
        gui.setItem(15, new ItemStack(Material.TOTEM_OF_UNDYING)); // 예시: REVIVAL 보험
        return gui;
    }

    public static void openInsurancePurchase(Player player) {
        Inventory gui = createInsurancePurchaseGUI(player);
        player.openInventory(gui);
    }
}