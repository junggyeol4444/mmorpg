package com.multiverse.trade.models;

import java.util.HashSet;
import java.util.Set;
import java.util. UUID;

public class PlayerTradeData {

    private UUID uuid;
    private String name;
    
    private int totalTrades;
    private int totalBought;
    private int totalSold;
    private double totalSpent;
    private double totalEarned;
    
    private int shopCount;
    
    private Set<UUID> blacklist;
    
    private long lastTradeTime;

    public PlayerTradeData() {
        this.totalTrades = 0;
        this. totalBought = 0;
        this.totalSold = 0;
        this.totalSpent = 0;
        this.totalEarned = 0;
        this.shopCount = 0;
        this.blacklist = new HashSet<>();
        this.lastTradeTime = 0;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this. uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalTrades() {
        return totalTrades;
    }

    public void setTotalTrades(int totalTrades) {
        this.totalTrades = totalTrades;
    }

    public int getTotalBought() {
        return totalBought;
    }

    public void setTotalBought(int totalBought) {
        this. totalBought = totalBought;
    }

    public int getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(int totalSold) {
        this.totalSold = totalSold;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public double getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(double totalEarned) {
        this.totalEarned = totalEarned;
    }

    public int getShopCount() {
        return shopCount;
    }

    public void setShopCount(int shopCount) {
        this.shopCount = shopCount;
    }

    public Set<UUID> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(Set<UUID> blacklist) {
        this.blacklist = blacklist;
    }

    public long getLastTradeTime() {
        return lastTradeTime;
    }

    public void setLastTradeTime(long lastTradeTime) {
        this.lastTradeTime = lastTradeTime;
    }

    public void addToBlacklist(UUID playerId) {
        blacklist.add(playerId);
    }

    public void removeFromBlacklist(UUID playerId) {
        blacklist. remove(playerId);
    }

    public boolean isBlacklisted(UUID playerId) {
        return blacklist.contains(playerId);
    }

    public void incrementTrades() {
        totalTrades++;
    }

    public void incrementBought(int amount) {
        totalBought += amount;
    }

    public void incrementSold(int amount) {
        totalSold += amount;
    }

    public void addSpent(double amount) {
        totalSpent += amount;
    }

    public void addEarned(double amount) {
        totalEarned += amount;
    }

    public double getNetProfit() {
        return totalEarned - totalSpent;
    }

    public double getAverageTradeValue() {
        if (totalTrades == 0) {
            return 0;
        }
        return (totalSpent + totalEarned) / (totalTrades * 2.0);
    }

    public int getTotalTransactions() {
        return totalBought + totalSold;
    }

    public long getTimeSinceLastTrade() {
        if (lastTradeTime == 0) {
            return -1;
        }
        return System. currentTimeMillis() - lastTradeTime;
    }

    public boolean hasTraded() {
        return totalTrades > 0;
    }

    public boolean hasRecentTrade(long withinMs) {
        if (lastTradeTime == 0) {
            return false;
        }
        return System.currentTimeMillis() - lastTradeTime <= withinMs;
    }
}