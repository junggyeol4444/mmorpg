package com. multiverse.trade. managers;

import com.multiverse.trade.TradeCore;
import com.multiverse. trade.models.MarketPrice;
import com.multiverse. trade.models.PriceHistory;
import com.multiverse. trade.models.PriceTrend;
import com.multiverse.trade.utils.ItemUtil;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util. stream.Collectors;

public class PriceTracker {

    private final TradeCore plugin;
    private final Map<String, List<PriceHistory>> priceHistories = new ConcurrentHashMap<>();
    private final Map<String, MarketPrice> cachedPrices = new ConcurrentHashMap<>();

    public PriceTracker(TradeCore plugin) {
        this.plugin = plugin;
        loadPriceData();
    }

    private void loadPriceData() {
        Map<String, List<PriceHistory>> loaded = plugin.getMarketDataManager().loadPriceHistories();
        if (loaded != null) {
            priceHistories.putAll(loaded);
        }
    }

    public void recordTransaction(ItemStack item, double price, int amount) {
        if (item == null || price <= 0) {
            return;
        }

        String itemKey = ItemUtil.getItemKey(item);

        PriceHistory history = new PriceHistory();
        history.setTimestamp(System.currentTimeMillis());
        history.setPrice(price);
        history.setVolume(amount);

        priceHistories.computeIfAbsent(itemKey, k -> Collections.synchronizedList(new ArrayList<>())).add(history);

        cleanOldHistory(itemKey);
        updateCachedPrice(itemKey, item);

        plugin.getMarketDataManager().savePriceHistory(itemKey, priceHistories.get(itemKey));
    }

    private void cleanOldHistory(String itemKey) {
        int historyDays = plugin.getConfig().getInt("price-tracking.history-days", 30);
        long cutoff = System.currentTimeMillis() - (historyDays * 24L * 60L * 60L * 1000L);

        List<PriceHistory> history = priceHistories.get(itemKey);
        if (history != null) {
            history.removeIf(h -> h.getTimestamp() < cutoff);
        }
    }

    private void updateCachedPrice(String itemKey, ItemStack item) {
        List<PriceHistory> history = priceHistories.get(itemKey);
        if (history == null || history.isEmpty()) {
            return;
        }

        MarketPrice marketPrice = new MarketPrice();
        marketPrice.setItem(item. clone());

        PriceHistory latest = history.get(history.size() - 1);
        marketPrice.setCurrentPrice(latest.getPrice());

        double sum = 0;
        int count = 0;
        int dailyVolume = 0;
        long oneDayAgo = System.currentTimeMillis() - (24L * 60L * 60L * 1000L);

        for (PriceHistory h : history) {
            sum += h.getPrice() * h.getVolume();
            count += h.getVolume();

            if (h. getTimestamp() >= oneDayAgo) {
                dailyVolume += h.getVolume();
            }
        }

        marketPrice.setAveragePrice(count > 0 ?  sum / count : latest.getPrice());
        marketPrice.setDailyVolume(dailyVolume);
        marketPrice.setLowestSell(getLowestSellPrice(itemKey));
        marketPrice.setHighestBuy(getHighestBuyPrice(itemKey));
        marketPrice.setHistory(new ArrayList<>(history));

        cachedPrices.put(itemKey, marketPrice);
    }

    private double getLowestSellPrice(String itemKey) {
        return plugin.getMarketManager().getActiveOrders(ItemUtil.parseItemFromKey(itemKey)).stream()
                .filter(o -> o.getType() == com.multiverse.trade.models.OrderType.SELL)
                .mapToDouble(o -> o.getPricePerUnit())
                .min()
                .orElse(0);
    }

    private double getHighestBuyPrice(String itemKey) {
        return plugin.getMarketManager().getActiveOrders(ItemUtil.parseItemFromKey(itemKey)).stream()
                .filter(o -> o.getType() == com.multiverse. trade.models.OrderType.BUY)
                .mapToDouble(o -> o.getPricePerUnit())
                .max()
                .orElse(0);
    }

    public double getCurrentPrice(ItemStack item) {
        String itemKey = ItemUtil.getItemKey(item);
        MarketPrice cached = cachedPrices.get(itemKey);
        if (cached != null) {
            return cached.getCurrentPrice();
        }

        List<PriceHistory> history = priceHistories.get(itemKey);
        if (history != null && !history.isEmpty()) {
            return history.get(history.size() - 1).getPrice();
        }

        return 0;
    }

    public double getAveragePrice(ItemStack item, int days) {
        String itemKey = ItemUtil.getItemKey(item);
        List<PriceHistory> history = priceHistories.get(itemKey);

        if (history == null || history.isEmpty()) {
            return 0;
        }

        long cutoff = System. currentTimeMillis() - (days * 24L * 60L * 60L * 1000L);

        double sum = 0;
        int count = 0;

        for (PriceHistory h : history) {
            if (h.getTimestamp() >= cutoff) {
                sum += h.getPrice() * h.getVolume();
                count += h.getVolume();
            }
        }

        return count > 0 ?  sum / count : 0;
    }

    public double getHighestPrice(ItemStack item, int days) {
        String itemKey = ItemUtil.getItemKey(item);
        List<PriceHistory> history = priceHistories. get(itemKey);

        if (history == null || history. isEmpty()) {
            return 0;
        }

        long cutoff = System. currentTimeMillis() - (days * 24L * 60L * 60L * 1000L);

        return history.stream()
                .filter(h -> h.getTimestamp() >= cutoff)
                .mapToDouble(PriceHistory::getPrice)
                .max()
                .orElse(0);
    }

    public double getLowestPrice(ItemStack item, int days) {
        String itemKey = ItemUtil.getItemKey(item);
        List<PriceHistory> history = priceHistories.get(itemKey);

        if (history == null || history.isEmpty()) {
            return 0;
        }

        long cutoff = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L);

        return history.stream()
                .filter(h -> h. getTimestamp() >= cutoff)
                .mapToDouble(PriceHistory:: getPrice)
                .min()
                .orElse(0);
    }

    public int getDailyVolume(ItemStack item) {
        String itemKey = ItemUtil. getItemKey(item);
        List<PriceHistory> history = priceHistories.get(itemKey);

        if (history == null || history.isEmpty()) {
            return 0;
        }

        long oneDayAgo = System.currentTimeMillis() - (24L * 60L * 60L * 1000L);

        return history. stream()
                .filter(h -> h.getTimestamp() >= oneDayAgo)
                .mapToInt(PriceHistory::getVolume)
                .sum();
    }

    public int getWeeklyVolume(ItemStack item) {
        String itemKey = ItemUtil. getItemKey(item);
        List<PriceHistory> history = priceHistories.get(itemKey);

        if (history == null || history.isEmpty()) {
            return 0;
        }

        long oneWeekAgo = System.currentTimeMillis() - (7L * 24L * 60L * 60L * 1000L);

        return history.stream()
                .filter(h -> h.getTimestamp() >= oneWeekAgo)
                .mapToInt(PriceHistory::getVolume)
                .sum();
    }

    public double getPriceChange(ItemStack item, int hours) {
        String itemKey = ItemUtil.getItemKey(item);
        List<PriceHistory> history = priceHistories.get(itemKey);

        if (history == null || history.size() < 2) {
            return 0;
        }

        long cutoff = System.currentTimeMillis() - (hours * 60L * 60L * 1000L);

        double currentPrice = history.get(history. size() - 1).getPrice();

        double oldPrice = currentPrice;
        for (PriceHistory h :  history) {
            if (h.getTimestamp() <= cutoff) {
                oldPrice = h.getPrice();
                break;
            }
        }

        if (oldPrice == 0) {
            return 0;
        }

        return ((currentPrice - oldPrice) / oldPrice) * 100.0;
    }

    public PriceTrend getTrend(ItemStack item) {
        double change24h = getPriceChange(item, 24);

        if (change24h > 5) {
            return PriceTrend. RISING;
        } else if (change24h < -5) {
            return PriceTrend. FALLING;
        } else {
            return PriceTrend.STABLE;
        }
    }

    public MarketPrice getMarketPrice(ItemStack item) {
        String itemKey = ItemUtil.getItemKey(item);
        MarketPrice cached = cachedPrices.get(itemKey);

        if (cached != null) {
            return cached;
        }

        MarketPrice marketPrice = new MarketPrice();
        marketPrice. setItem(item. clone());
        marketPrice.setCurrentPrice(getCurrentPrice(item));
        marketPrice.setAveragePrice(getAveragePrice(item, 7));
        marketPrice.setLowestSell(getLowestSellPrice(itemKey));
        marketPrice.setHighestBuy(getHighestBuyPrice(itemKey));
        marketPrice. setDailyVolume(getDailyVolume(item));

        List<PriceHistory> history = priceHistories.get(itemKey);
        marketPrice.setHistory(history != null ? new ArrayList<>(history) : new ArrayList<>());

        return marketPrice;
    }

    public List<MarketPrice> getTrendingItems(int limit) {
        List<MarketPrice> allPrices = new ArrayList<>();

        for (Map. Entry<String, MarketPrice> entry :  cachedPrices.entrySet()) {
            allPrices.add(entry.getValue());
        }

        return allPrices. stream()
                .sorted(Comparator.comparingInt(MarketPrice:: getDailyVolume).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public void updateAllPrices() {
        for (String itemKey : priceHistories.keySet()) {
            ItemStack item = ItemUtil.parseItemFromKey(itemKey);
            if (item != null) {
                updateCachedPrice(itemKey, item);
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<String, List<PriceHistory>> entry : priceHistories.entrySet()) {
            plugin.getMarketDataManager().savePriceHistory(entry.getKey(), entry.getValue());
        }
    }
}