package com.multiverse.trade.utils;

import com.multiverse.trade.TradeCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java. io.IOException;
import java.io. InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java. util.logging.Level;

public class ConfigUtil {

    private static TradeCore plugin;

    public static void init(TradeCore tradePlugin) {
        plugin = tradePlugin;
    }

    public static String getString(String path, String defaultValue) {
        return plugin.getConfig().getString(path, defaultValue);
    }

    public static int getInt(String path, int defaultValue) {
        return plugin. getConfig().getInt(path, defaultValue);
    }

    public static double getDouble(String path, double defaultValue) {
        return plugin.getConfig().getDouble(path, defaultValue);
    }

    public static boolean getBoolean(String path, boolean defaultValue) {
        return plugin.getConfig().getBoolean(path, defaultValue);
    }

    public static List<String> getStringList(String path) {
        return plugin.getConfig().getStringList(path);
    }

    public static List<Integer> getIntegerList(String path) {
        return plugin. getConfig().getIntegerList(path);
    }

    public static boolean contains(String path) {
        return plugin.getConfig().contains(path);
    }

    public static void set(String path, Object value) {
        plugin.getConfig().set(path, value);
    }

    public static void save() {
        plugin.saveConfig();
    }

    public static void reload() {
        plugin.reloadConfig();
        MessageUtil.reload();
    }

    public static FileConfiguration loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        
        if (!file. exists()) {
            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
            } else {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, "파일 생성 실패:  " + fileName, e);
                }
            }
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        InputStream defaultStream = plugin.getResource(fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            config.setDefaults(defaultConfig);
        }
        
        return config;
    }

    public static void saveConfig(FileConfiguration config, String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "파일 저장 실패: " + fileName, e);
        }
    }

    public static double getTradeTaxRate() {
        return getDouble("direct-trade. tax. rate", 1.0);
    }

    public static boolean isTradeTaxEnabled() {
        return getBoolean("direct-trade.tax. enabled", true);
    }

    public static int getTradeMaxDistance() {
        return getInt("direct-trade.restrictions.max-distance", 50);
    }

    public static int getTradeRequestTimeout() {
        return getInt("direct-trade.request-timeout", 60);
    }

    public static int getTradeConfirmDelay() {
        return getInt("direct-trade.confirm-delay", 3);
    }

    public static int getMaxShopsPerPlayer() {
        return getInt("player-shops.limits.max-shops-per-player", 3);
    }

    public static int getMaxItemsPerShop() {
        return getInt("player-shops.limits. max-items-per-shop", 27);
    }

    public static double getShopCreationCost() {
        return getDouble("player-shops.fees.creation-cost", 10000.0);
    }

    public static double getShopSaleFee() {
        return getDouble("player-shops.fees. sale-fee", 3.0);
    }

    public static double getAuctionListingFee() {
        return getDouble("auction. fees.listing-fee", 100.0);
    }

    public static double getAuctionSellerFee() {
        return getDouble("auction.fees.seller-fee", 5.0);
    }

    public static int getAuctionDefaultDuration() {
        return getInt("auction.durations.default", 24);
    }

    public static int getMarketOrderExpiry() {
        return getInt("market. order-expiry", 7);
    }

    public static int getMailExpiryDays() {
        return getInt("mail.expiry. days", 30);
    }

    public static double getMailPostage() {
        return getDouble("mail. cost. postage", 10.0);
    }
}