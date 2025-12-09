package com.multiverse.trade.data;

import com. multiverse.trade. TradeCore;
import com. multiverse.trade. models.MarketOrder;
import com. multiverse.trade. models.OrderStatus;
import com.multiverse.trade.models.OrderType;
import com.multiverse.trade.models.PriceHistory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit. configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit. inventory.ItemStack;

import java.io. File;
import java.io.IOException;
import java. util.*;
import java.util.concurrent. ConcurrentHashMap;
import java.util. logging.Level;

public class MarketDataManager {

    private final TradeCore plugin;
    private final File marketFolder;
    private final File ordersFile;
    private final File pricesFile;
    private final Map<UUID, MarketOrder> cachedOrders = new ConcurrentHashMap<>();
    private final Map<String, List<PriceHistory>> cachedPriceHistories = new ConcurrentHashMap<>();

    public MarketDataManager(TradeCore plugin) {
        this. plugin = plugin;
        this.marketFolder = new File(plugin.getDataFolder(), "market");
        this.ordersFile = new File(marketFolder, "orders.yml");
        this.pricesFile = new File(marketFolder, "prices.yml");
        
        if (!marketFolder.exists()) {
            marketFolder.mkdirs();
        }
    }

    public void loadAll() {
        loadOrders();
        loadPriceHistories();
    }

    private void loadOrders() {
        cachedOrders.clear();
        
        if (!ordersFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(ordersFile);
        List<Map<?, ?>> ordersList = config.getMapList("orders");

        for (Map<?, ?> orderMap : ordersList) {
            try {
                MarketOrder order = deserializeOrder(orderMap);
                if (order != null) {
                    cachedOrders.put(order.getOrderId(), order);
                }
            } catch (Exception e) {
                plugin. getLogger().log(Level.WARNING, "주문 로드 실패", e);
            }
        }

        plugin.getLogger().info("거래소 주문 " + cachedOrders.size() + "개 로드됨");
    }

    private MarketOrder deserializeOrder(Map<?, ?> map) {
        MarketOrder order = new MarketOrder();
        
        order.setOrderId(UUID.fromString((String) map.get("order-id")));
        order.setPlayer(UUID.fromString((String) map.get("player")));
        order.setType(OrderType.valueOf((String) map.get("type")));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> itemData = (Map<String, Object>) map.get("item");
        if (itemData != null) {
            order.setItem(ItemStack.deserialize(itemData));
        }

        order.setAmount(((Number) map.get("amount")).intValue());
        order.setRemainingAmount(((Number) map.get("remaining-amount")).intValue());
        order.setPricePerUnit(((Number) map.get("price-per-unit")).doubleValue());
        order.setCreateTime(((Number) map.get("create-time")).longValue());
        order.setExpiryTime(((Number) map.get("expiry-time")).longValue());
        order.setStatus(OrderStatus.valueOf((String) map.get("status")));

        return order;
    }

    public void saveOrder(MarketOrder order) {
        if (order == null) {
            return;
        }

        cachedOrders.put(order.getOrderId(), order);
        saveAllOrders();
    }

    private void saveAllOrders() {
        List<Map<String, Object>> ordersList = new ArrayList<>();

        for (MarketOrder order : cachedOrders.values()) {
            ordersList.add(serializeOrder(order));
        }

        FileConfiguration config = new YamlConfiguration();
        config.set("orders", ordersList);

        try {
            config.save(ordersFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "주문 저장 실패", e);
        }
    }

    private Map<String, Object> serializeOrder(MarketOrder order) {
        Map<String, Object> map = new LinkedHashMap<>();
        
        map.put("order-id", order.getOrderId().toString());
        map.put("player", order. getPlayer().toString());
        map.put("type", order. getType().name());
        
        if (order.getItem() != null) {
            map.put("item", order. getItem().serialize());
        }

        map.put("amount", order.getAmount());
        map.put("remaining-amount", order.getRemainingAmount());
        map.put("price-per-unit", order.getPricePerUnit());
        map.put("create-time", order. getCreateTime());
        map.put("expiry-time", order.getExpiryTime());
        map.put("status", order.getStatus().name());

        return map;
    }

    public Map<UUID, MarketOrder> getAllOrders() {
        return new HashMap<>(cachedOrders);
    }

    public void deleteOrder(UUID orderId) {
        cachedOrders. remove(orderId);
        saveAllOrders();
    }

    private void loadPriceHistories() {
        cachedPriceHistories.clear();
        
        if (!pricesFile.exists()) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(pricesFile);
        ConfigurationSection section = config.getConfigurationSection("prices");
        
        if (section == null) {
            return;
        }

        for (String itemKey : section.getKeys(false)) {
            List<PriceHistory> histories = new ArrayList<>();
            List<Map<?, ?>> historyList = section.getMapList(itemKey);
            
            for (Map<?, ? > historyMap :  historyList) {
                PriceHistory history = new PriceHistory();
                history.setTimestamp(((Number) historyMap.get("timestamp")).longValue());
                history.setPrice(((Number) historyMap.get("price")).doubleValue());
                history.setVolume(((Number) historyMap.get("volume")).intValue());
                histories.add(history);
            }
            
            cachedPriceHistories. put(itemKey, Collections.synchronizedList(histories));
        }

        plugin.getLogger().info("가격 기록 " + cachedPriceHistories.size() + "개 아이템 로드됨");
    }

    public void savePriceHistory(String itemKey, List<PriceHistory> histories) {
        cachedPriceHistories.put(itemKey, histories);
        saveAllPriceHistories();
    }

    private void saveAllPriceHistories() {
        FileConfiguration config = new YamlConfiguration();
        ConfigurationSection section = config.createSection("prices");

        for (Map. Entry<String, List<PriceHistory>> entry : cachedPriceHistories.entrySet()) {
            List<Map<String, Object>> historyList = new ArrayList<>();
            
            for (PriceHistory history : entry.getValue()) {
                Map<String, Object> historyMap = new LinkedHashMap<>();
                historyMap.put("timestamp", history.getTimestamp());
                historyMap.put("price", history.getPrice());
                historyMap.put("volume", history.getVolume());
                historyList.add(historyMap);
            }
            
            section.set(entry. getKey(), historyList);
        }

        try {
            config.save(pricesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level. SEVERE, "가격 기록 저장 실패", e);
        }
    }

    public Map<String, List<PriceHistory>> loadPriceHistories() {
        if (cachedPriceHistories.isEmpty()) {
            loadPriceHistories();
        }
        return new HashMap<>(cachedPriceHistories);
    }

    public void saveAll() {
        saveAllOrders();
        saveAllPriceHistories();
    }
}