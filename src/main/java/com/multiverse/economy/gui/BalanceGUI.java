package com.multiverse.economy.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

import java.util.List;
import com.multiverse.economy.models.PlayerBalance;
import com.multiverse.economy.utils.MessageUtil;

public class BalanceGUI {

    public static Inventory createBalanceInventory(Player player, List<PlayerBalance> balances) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Balance");

        int slot = 0;
        for (PlayerBalance balance : balances) {
            ItemStack item = MessageUtil.createBalanceItem(balance);
            inv.setItem(slot, item);
            slot++;
            if (slot >= inv.getSize()) break;
        }
        return inv;
    }
}