package com.multiverse.economy.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

import com.multiverse.economy.models.EconomyStatistics;
import com.multiverse.economy.utils.MessageUtil;

public class StatisticsGUI {

    public static Inventory createStatisticsInventory(Player player, EconomyStatistics statistics) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "Economy Statistics");

        ItemStack statsItem = MessageUtil.createStatisticsItem(statistics);
        inv.setItem(13, statsItem); // 중앙 슬롯에 배치

        return inv;
    }
}