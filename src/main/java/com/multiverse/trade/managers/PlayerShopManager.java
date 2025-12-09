package com.multiverse.trade.managers;

import com.multiverse. trade.TradeCore;
import com. multiverse.trade. events.ShopPurchaseEvent;
import com.multiverse.trade.models.PlayerShop;
import com.multiverse.trade.models.ShopItem;
import com.multiverse. trade.utils.ItemUtil;
import com.multiverse.trade.utils.MessageUtil;
import com.multiverse.trade.utils.NumberUtil;
import org.bukkit. Bukkit;
import org.bukkit. Location;
import org. bukkit.entity.Player;
import org.bukkit. inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util. stream.Collectors;

public class PlayerShopManager {

    private final TradeCore plugin;
    private final Map<UUID, PlayerShop> shops = new ConcurrentHashMap<>();
    private final Map<Location, UUID> locationToShop = new ConcurrentHashMap<>();

    public PlayerShopManager(TradeCore plugin) {
        this.plugin = plugin;
        loadShops();
    }

    private void loadShops() {
        Map<UUID, PlayerShop> loadedShops = plugin.getShopDataManager().getAllShops();
        shops.putAll(loadedShops);
        
        for (PlayerShop shop : shops.values()) {
            if (shop.getLocation() != null) {
                locationToShop.put(shop.getLocation(), shop.getShopId());
            }
        }
    }

    public PlayerShop createShop(Player player, Location location, String name) {
        UUID shopId = UUID.randomUUID();
        
        PlayerShop shop = new PlayerShop();
        shop.setShopId(shopId);
        shop.setOwner(player.getUniqueId());
        shop.setShopName(name);
        shop.setLocation(location);
        shop.setItems(new ArrayList<>());
        shop.setOpen(true);
        shop.setDescription("");
        shop.setTotalSales(0);
        shop.setTotalOrders(0);
        shop.setCreatedTime(System.currentTimeMillis());

        shops.put(shopId, shop);
        if (location != null) {
            locationToShop.put(location, shopId);
        }

        plugin.getShopDataManager().saveShop(shop);

        return shop;
    }

    public void deleteShop(UUID shopId) {
        PlayerShop shop = shops.remove(shopId);
        if (shop != null) {
            if (shop.getLocation() != null) {
                locationToShop.remove(shop.getLocation());
            }
            plugin.getShopDataManager().deleteShop(shopId);
        }
    }

    public PlayerShop getShop(UUID shopId) {
        return shops. get(shopId);
    }

    public PlayerShop getShopAtLocation(Location location) {
        UUID shopId = locationToShop.get(location);
        if (shopId != null) {
            return shops.get(shopId);
        }
        return null;
    }

    public List<PlayerShop> getPlayerShops(Player player) {
        return getPlayerShopsByUUID(player.getUniqueId());
    }

    public List<PlayerShop> getPlayerShopsByUUID(UUID playerId) {
        return shops. values().stream()
                .filter(shop -> shop.getOwner().equals(playerId))
                .collect(Collectors. toList());
    }

    public List<PlayerShop> getAllShops() {
        return new ArrayList<>(shops.values());
    }

    public void addItem(PlayerShop shop, int slot, ItemStack item, double price, int stock) {
        if (shop == null || item == null) {
            return;
        }

        int maxItems = plugin.getConfig().getInt("player-shops.limits.max-items-per-shop", 27);
        if (shop.getItems().size() >= maxItems) {
            return;
        }

        ShopItem shopItem = new ShopItem();
        shopItem.setSlot(slot);
        shopItem.setItem(item. clone());
        shopItem.setPrice(price);
        shopItem.setStock(stock);
        shopItem.setMaxStock(stock);
        shopItem.setTotalSold(0);
        shopItem.setTotalRevenue(0);

        shop.getItems().removeIf(i -> i.getSlot() == slot);
        shop.getItems().add(shopItem);

        plugin.getShopDataManager().saveShop(shop);
    }

    public void removeItem(PlayerShop shop, int slot) {
        if (shop == null) {
            return;
        }

        shop.getItems().removeIf(item -> item.getSlot() == slot);
        plugin.getShopDataManager().saveShop(shop);
    }

    public void updatePrice(PlayerShop shop, int slot, double price) {
        if (shop == null || price < 0) {
            return;
        }

        for (ShopItem item : shop.getItems()) {
            if (item.getSlot() == slot) {
                item. setPrice(price);
                break;
            }
        }

        plugin.getShopDataManager().saveShop(shop);
    }

    public void updateStock(PlayerShop shop, int slot, int stock) {
        if (shop == null || stock < 0) {
            return;
        }

        for (ShopItem item : shop.getItems()) {
            if (item.getSlot() == slot) {
                item.setStock(stock);
                break;
            }
        }

        plugin. getShopDataManager().saveShop(shop);
    }

    public boolean buyItem(Player buyer, PlayerShop shop, int slot, int amount) {
        if (shop == null || ! shop.isOpen()) {
            MessageUtil.send(buyer, "shop.shop-closed");
            return false;
        }

        if (shop.getOwner().equals(buyer.getUniqueId())) {
            buyer.sendMessage(MessageUtil.color("&c자신의 상점에서는 구매할 수 없습니다. "));
            return false;
        }

        ShopItem shopItem = null;
        for (ShopItem item :  shop.getItems()) {
            if (item.getSlot() == slot) {
                shopItem = item;
                break;
            }
        }

        if (shopItem == null) {
            MessageUtil.send(buyer, "shop.not-found");
            return false;
        }

        if (shopItem.getStock() < amount) {
            MessageUtil.send(buyer, "shop. out-of-stock");
            return false;
        }

        double totalPrice = shopItem.getPrice() * amount;

        if (! plugin.getEconomy().has(buyer, totalPrice)) {
            MessageUtil. send(buyer, "shop.not-enough-money");
            return false;
        }

        ShopPurchaseEvent event = new ShopPurchaseEvent(shop, buyer, shopItem, amount, totalPrice);
        Bukkit.getPluginManager().callEvent(event);

        if (event. isCancelled()) {
            return false;
        }

        plugin.getEconomy().withdrawPlayer(buyer, totalPrice);

        double feeRate = plugin.getConfig().getDouble("player-shops.fees.sale-fee", 3.0) / 100.0;
        double fee = totalPrice * feeRate;
        double sellerAmount = totalPrice - fee;

        Bukkit.getOfflinePlayer(shop.getOwner());
        plugin.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(shop.getOwner()), sellerAmount);

        if (fee > 0) {
            plugin.getTransactionFeeManager().distributeFee(fee);
        }

        ItemStack purchasedItem = shopItem.getItem().clone();
        purchasedItem.setAmount(amount);
        buyer.getInventory().addItem(purchasedItem);

        shopItem.setStock(shopItem.getStock() - amount);
        shopItem.setTotalSold(shopItem.getTotalSold() + amount);
        shopItem.setTotalRevenue(shopItem.getTotalRevenue() + totalPrice);

        shop.setTotalSales(shop.getTotalSales() + totalPrice);
        shop.setTotalOrders(shop.getTotalOrders() + 1);

        plugin.getShopDataManager().saveShop(shop);

        String itemName = ItemUtil.getItemName(shopItem.getItem());
        MessageUtil. send(buyer, "shop.purchased", 
            "item", itemName + " x" + amount,
            "price", NumberUtil.format(totalPrice));

        Player owner = Bukkit. getPlayer(shop. getOwner());
        if (owner != null && owner.isOnline()) {
            MessageUtil.send(owner, "shop.sold-notify",
                "buyer", buyer.getName(),
                "item", itemName + " x" + amount,
                "price", NumberUtil.format(sellerAmount));
        }

        plugin.getPriceTracker().recordTransaction(shopItem. getItem(), shopItem.getPrice(), amount);

        return true;
    }

    public List<PlayerShop> searchShops(String query) {
        String lowerQuery = query.toLowerCase();
        return shops.values().stream()
                .filter(shop -> shop.isOpen())
                .filter(shop -> {
                    if (shop.getShopName().toLowerCase().contains(lowerQuery)) {
                        return true;
                    }
                    if (shop.getDescription().toLowerCase().contains(lowerQuery)) {
                        return true;
                    }
                    String ownerName = Bukkit.getOfflinePlayer(shop. getOwner()).getName();
                    if (ownerName != null && ownerName.toLowerCase().contains(lowerQuery)) {
                        return true;
                    }
                    for (ShopItem item : shop.getItems()) {
                        String itemName = ItemUtil.getItemName(item.getItem());
                        if (itemName. toLowerCase().contains(lowerQuery)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors. toList());
    }

    public List<ShopItem> searchItems(String itemName) {
        String lowerName = itemName.toLowerCase();
        List<ShopItem> results = new ArrayList<>();

        for (PlayerShop shop : shops.values()) {
            if (! shop.isOpen()) {
                continue;
            }

            for (ShopItem item : shop.getItems()) {
                if (item.getStock() <= 0) {
                    continue;
                }

                String name = ItemUtil.getItemName(item.getItem());
                if (name.toLowerCase().contains(lowerName)) {
                    results.add(item);
                }
            }
        }

        results.sort(Comparator. comparingDouble(ShopItem:: getPrice));
        return results;
    }

    public void saveAllShops() {
        for (PlayerShop shop : shops.values()) {
            plugin. getShopDataManager().saveShop(shop);
        }
    }
}