package com.multiverse.trade.models;

import org. bukkit. Chunk;
import org. bukkit.Location;

import java.util. ArrayList;
import java. util.List;
import java.util. UUID;

public class PlayerShop {

    private UUID shopId;
    private UUID owner;
    private String shopName;
    
    private Location location;
    private Chunk chunk;
    
    private List<ShopItem> items;
    
    private double totalSales;
    private int totalOrders;
    
    private boolean isOpen;
    private String description;
    
    private long createdTime;

    public PlayerShop() {
        this.items = new ArrayList<>();
        this.totalSales = 0;
        this. totalOrders = 0;
        this.isOpen = true;
        this. description = "";
        this.createdTime = System. currentTimeMillis();
    }

    public UUID getShopId() {
        return shopId;
    }

    public void setShopId(UUID shopId) {
        this.shopId = shopId;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this. owner = owner;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        if (location != null) {
            this.chunk = location. getChunk();
        }
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this. chunk = chunk;
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public void setItems(List<ShopItem> items) {
        this.items = items;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this. totalSales = totalSales;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public ShopItem getItem(int slot) {
        for (ShopItem item : items) {
            if (item. getSlot() == slot) {
                return item;
            }
        }
        return null;
    }

    public void addItem(ShopItem item) {
        items.removeIf(i -> i.getSlot() == item.getSlot());
        items.add(item);
    }

    public void removeItem(int slot) {
        items.removeIf(i -> i. getSlot() == slot);
    }

    public int getItemCount() {
        return items. size();
    }

    public int getTotalStock() {
        return items.stream().mapToInt(ShopItem::getStock).sum();
    }

    public void addSale(double amount) {
        this.totalSales += amount;
        this.totalOrders++;
    }

    public double getAverageOrderValue() {
        if (totalOrders == 0) {
            return 0;
        }
        return totalSales / totalOrders;
    }

    public boolean hasStock() {
        return items.stream().anyMatch(i -> i.getStock() > 0);
    }

    public List<ShopItem> getItemsWithStock() {
        List<ShopItem> result = new ArrayList<>();
        for (ShopItem item : items) {
            if (item.getStock() > 0) {
                result.add(item);
            }
        }
        return result;
    }
}