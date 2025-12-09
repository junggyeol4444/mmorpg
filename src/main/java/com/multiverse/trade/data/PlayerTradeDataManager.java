package com.multiverse. trade.data;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.models.PlayerTradeData;
import com. multiverse.trade. models.Trade;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file. YamlConfiguration;

import java.io. File;
import java.io.IOException;
import java.util.*;
import java. util.concurrent.ConcurrentHashMap;
import java.util. logging.Level;

public class PlayerTradeDataManager {

    private final TradeCore plugin;
    private final File playersFolder;
    private final Map<UUID, PlayerTradeData> cachedData = new ConcurrentHashMap<>();

    public PlayerTradeDataManager(TradeCore plugin) {
        this. plugin = plugin;
        this.playersFolder = new File(plugin.getDataFolder(), "players");
        
        if (! playersFolder.exists()) {
            playersFolder.mkdirs();
        }
    }

    public void loadAll() {
        cachedData.clear();
        
        File[] files = playersFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return;
        }

        for (File file : files) {
            try {
                String fileName = file.getName().replace(".yml", "");
                UUID playerId = UUID. fromString(fileName);
                PlayerTradeData data = loadPlayerData(file);
                if (data != null) {
                    cachedData.put(playerId, data);
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "플레이어 데이터 로드 실패:  " + file.getName(), e);
            }
        }

        plugin.getLogger().info("플레이어 거래 데이터 " + cachedData. size() + "개 로드됨");
    }

    private PlayerTradeData loadPlayerData(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        if (! config.contains("player")) {
            return null;
        }

        PlayerTradeData data = new PlayerTradeData();
        data.setUuid(UUID.fromString(config.getString("player. uuid")));
        data.setName(config.getString("player.name"));
        data.setTotalTrades(config.getInt("trade-stats.total-trades", 0));
        data.setTotalBought(config.getInt("trade-stats. total-bought", 0));
        data.setTotalSold(config.getInt("trade-stats.total-sold", 0));
        data.setTotalSpent(config.getDouble("trade-stats. total-spent", 0));
        data.setTotalEarned(config. getDouble("trade-stats.total-earned", 0));
        data.setShopCount(config. getInt("shop-count", 0));
        
        Set<UUID> blacklist = new HashSet<>();
        List<String> blacklistStrings = config.getStringList("blacklist");
        for (String s : blacklistStrings) {
            try {
                blacklist.add(UUID. fromString(s));
            } catch (Exception ignored) {}
        }
        data.setBlacklist(blacklist);
        
        data.setLastTradeTime(config. getLong("last-trade-time", 0));

        return data;
    }

    public void savePlayerData(PlayerTradeData data) {
        if (data == null) {
            return;
        }

        cachedData.put(data.getUuid(), data);

        File file = new File(playersFolder, data.getUuid().toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        config.set("player. uuid", data.getUuid().toString());
        config.set("player.name", data.getName());
        config.set("trade-stats.total-trades", data. getTotalTrades());
        config.set("trade-stats. total-bought", data.getTotalBought());
        config.set("trade-stats.total-sold", data.getTotalSold());
        config.set("trade-stats.total-spent", data.getTotalSpent());
        config.set("trade-stats.total-earned", data.getTotalEarned());
        config.set("shop-count", data.getShopCount());
        
        List<String> blacklistStrings = new ArrayList<>();
        for (UUID uuid : data.getBlacklist()) {
            blacklistStrings.add(uuid. toString());
        }
        config.set("blacklist", blacklistStrings);
        
        config. set("last-trade-time", data. getLastTradeTime());

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "플레이어 데이터 저장 실패:  " + data.getUuid(), e);
        }
    }

    public PlayerTradeData getPlayerData(UUID playerId) {
        PlayerTradeData data = cachedData.get(playerId);
        
        if (data == null) {
            File file = new File(playersFolder, playerId.toString() + ".yml");
            if (file.exists()) {
                data = loadPlayerData(file);
                if (data != null) {
                    cachedData. put(playerId, data);
                }
            }
        }
        
        return data;
    }

    public PlayerTradeData getOrCreatePlayerData(UUID playerId, String playerName) {
        PlayerTradeData data = getPlayerData(playerId);
        
        if (data == null) {
            data = new PlayerTradeData();
            data.setUuid(playerId);
            data.setName(playerName);
            data.setTotalTrades(0);
            data.setTotalBought(0);
            data.setTotalSold(0);
            data.setTotalSpent(0);
            data.setTotalEarned(0);
            data.setShopCount(0);
            data.setBlacklist(new HashSet<>());
            data.setLastTradeTime(0);
            
            cachedData.put(playerId, data);
            savePlayerData(data);
        }
        
        return data;
    }

    public void recordTrade(Trade trade) {
        PlayerTradeData data1 = getOrCreatePlayerData(trade. getPlayer1(), null);
        PlayerTradeData data2 = getOrCreatePlayerData(trade.getPlayer2(), null);

        data1.setTotalTrades(data1.getTotalTrades() + 1);
        data2.setTotalTrades(data2.getTotalTrades() + 1);

        double money1 = trade. getPlayer1Money();
        double money2 = trade. getPlayer2Money();

        if (money1 > 0) {
            data1.setTotalSpent(data1.getTotalSpent() + money1);
            data2.setTotalEarned(data2.getTotalEarned() + money1);
        }
        if (money2 > 0) {
            data2.setTotalSpent(data2.getTotalSpent() + money2);
            data1.setTotalEarned(data1.getTotalEarned() + money2);
        }

        int items1Count = trade.getPlayer1Items().size();
        int items2Count = trade.getPlayer2Items().size();

        data1.setTotalSold(data1.getTotalSold() + items1Count);
        data1.setTotalBought(data1.getTotalBought() + items2Count);
        data2.setTotalSold(data2.getTotalSold() + items2Count);
        data2.setTotalBought(data2.getTotalBought() + items1Count);

        data1.setLastTradeTime(System.currentTimeMillis());
        data2.setLastTradeTime(System.currentTimeMillis());

        savePlayerData(data1);
        savePlayerData(data2);
    }

    public void updateShopCount(UUID playerId, int count) {
        PlayerTradeData data = getPlayerData(playerId);
        if (data != null) {
            data. setShopCount(count);
            savePlayerData(data);
        }
    }

    public void saveAll() {
        for (PlayerTradeData data : cachedData.values()) {
            savePlayerData(data);
        }
        plugin.getLogger().info("플레이어 거래 데이터 " + cachedData.size() + "개 저장됨");
    }
}