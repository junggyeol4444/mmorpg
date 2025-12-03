package com.multiverse.economy.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigUtil {
    private static JavaPlugin plugin;

    public static void initialize(JavaPlugin instance) {
        plugin = instance;
    }

    public static double getInterestRate() {
        FileConfiguration config = plugin.getConfig();
        return config.getDouble("interest-rate", 0.01);
    }

    public static int getTransactionCount() {
        FileConfiguration config = plugin.getConfig();
        return config.getInt("statistics.transaction-count", 0);
    }

    public static double getTotalBalance() {
        FileConfiguration config = plugin.getConfig();
        return config.getDouble("statistics.total-balance", 0.0);
    }

    public static double getTotalLoans() {
        FileConfiguration config = plugin.getConfig();
        return config.getDouble("statistics.total-loans", 0.0);
    }

    public static double getTotalBurned() {
        FileConfiguration config = plugin.getConfig();
        return config.getDouble("statistics.total-burned", 0.0);
    }

    public static double getInflationRate() {
        FileConfiguration config = plugin.getConfig();
        return config.getDouble("statistics.inflation-rate", 0.0);
    }

    public static void applyInflationControl(String currencyId) {
        // Placeholder for actual inflation control logic.
    }
}