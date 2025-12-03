package com.multiverse.economy.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

import java.util.List;
import com.multiverse.economy.models.Currency;
import com.multiverse.economy.models.ExchangeRate;
import com.multiverse.economy.utils.MessageUtil;

public class ExchangeGUI {

    public static Inventory createExchangeInventory(Player player, List<Currency> currencies, List<ExchangeRate> exchangeRates) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.BLUE + "Currency Exchange");

        int slot = 0;
        for (ExchangeRate rate : exchangeRates) {
            ItemStack item = MessageUtil.createExchangeRateItem(rate, currencies);
            inv.setItem(slot, item);
            slot++;
            if (slot >= inv.getSize()) break;
        }
        return inv;
    }
}