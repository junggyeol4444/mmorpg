package com.multiverse.economy.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

import java.util.List;
import com.multiverse.economy.models.BankAccount;
import com.multiverse.economy.utils.MessageUtil;

public class BankGUI {

    public static Inventory createBankInventory(Player player, List<BankAccount> accounts) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Bank");

        int slot = 0;
        for (BankAccount account : accounts) {
            ItemStack item = MessageUtil.createBankAccountItem(account);
            inv.setItem(slot, item);
            slot++;
            if (slot >= inv.getSize()) break;
        }
        return inv;
    }
}