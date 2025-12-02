package com.multiverse.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import net.milkbowl.vault.economy.Economy;

public class EconomyUtil {
    private static Economy getEconomy() {
        return (Economy) Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

    public static boolean hasEnoughBalance(OfflinePlayer player, double amount) {
        Economy eco = getEconomy();
        return eco != null && eco.has(player, amount);
    }

    public static boolean withdraw(OfflinePlayer player, double amount) {
        Economy eco = getEconomy();
        if (eco != null && eco.has(player, amount)) {
            return eco.withdrawPlayer(player, amount).transactionSuccess();
        }
        return false;
    }

    public static boolean deposit(OfflinePlayer player, double amount) {
        Economy eco = getEconomy();
        if (eco != null) {
            return eco.depositPlayer(player, amount).transactionSuccess();
        }
        return false;
    }

    public static double getBalance(OfflinePlayer player) {
        Economy eco = getEconomy();
        return eco != null ? eco.getBalance(player) : 0.0;
    }
}