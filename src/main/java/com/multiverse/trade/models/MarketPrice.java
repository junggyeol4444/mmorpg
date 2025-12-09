package com.multiverse.trade.models;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java. util.List;

public class MarketPrice {

    private ItemStack item;
    private double currentPrice;
    private double averagePrice;
    private double lowestSell;
    private double highestBuy;
    private int dailyVolume;
    private List<PriceHistory> history;

    public MarketPrice() {
        this. currentPrice = 0;
        this.averagePrice = 0;
        this. lowestSell = 0;
        this.highestBuy = 0;
        this.dailyVolume = 0;
        this.history = new ArrayList<>();
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public double getLowestSell() {
        return lowestSell;
    }

    public void setLowestSell(double lowestSell) {
        this.lowestSell = lowestSell;
    }

    public double getHighestBuy() {
        return highestBuy;
    }

    public void setHighestBuy(double highestBuy) {
        this.highestBuy = highestBuy;
    }

    public int getDailyVolume() {
        return dailyVolume;
    }

    public void setDailyVolume(int dailyVolume) {
        this. dailyVolume = dailyVolume;
    }

    public List<PriceHistory> getHistory() {
        return history;
    }

    public void setHistory(List<PriceHistory> history) {
        this.history = history;
    }

    public double getSpread() {
        if (lowestSell <= 0 || highestBuy <= 0) {
            return 0;
        }
        return lowestSell - highestBuy;
    }

    public double getSpreadPercentage() {
        if (highestBuy <= 0) {
            return 0;
        }
        return (getSpread() / highestBuy) * 100.0;
    }

    public double getPriceChange24h() {
        if (history.isEmpty()) {
            return 0;
        }

        long oneDayAgo = System.currentTimeMillis() - (24L * 60L * 60L * 1000L);
        
        double oldPrice = currentPrice;
        for (PriceHistory h : history) {
            if (h. getTimestamp() <= oneDayAgo) {
                oldPrice = h.getPrice();
                break;
            }
        }

        if (oldPrice == 0) {
            return 0;
        }

        return ((currentPrice - oldPrice) / oldPrice) * 100.0;
    }

    public PriceTrend getTrend() {
        double change = getPriceChange24h();
        if (change > 5) {
            return PriceTrend. RISING;
        } else if (change < -5) {
            return PriceTrend.FALLING;
        } else {
            return PriceTrend. STABLE;
        }
    }

    public boolean hasMarketData() {
        return currentPrice > 0 || ! history.isEmpty();
    }

    public double getSuggestedSellPrice() {
        if (lowestSell > 0) {
            return lowestSell - 1;
        }
        if (averagePrice > 0) {
            return averagePrice;
        }
        return currentPrice;
    }

    public double getSuggestedBuyPrice() {
        if (highestBuy > 0) {
            return highestBuy + 1;
        }
        if (averagePrice > 0) {
            return averagePrice;
        }
        return currentPrice;
    }
}