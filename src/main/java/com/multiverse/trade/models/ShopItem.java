package com.multiverse.trade.models;

import org.bukkit.inventory.ItemStack;

public class ShopItem {

    private int slot;
    private ItemStack item;
    private double price;
    private int stock;
    private int maxStock;
    
    private int totalSold;
    private double totalRevenue;

    public ShopItem() {
        this.slot = 0;
        this.item = null;
        this. price = 0;
        this.stock = 0;
        this.maxStock = 0;
        this.totalSold = 0;
        this.totalRevenue = 0;
    }

    public ShopItem(int slot, ItemStack item, double price, int stock) {
        this. slot = slot;
        this.item = item;
        this.price = price;
        this.stock = stock;
        this.maxStock = stock;
        this.totalSold = 0;
        this.totalRevenue = 0;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this. slot = slot;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this. item = item;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(int maxStock) {
        this.maxStock = maxStock;
    }

    public int getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(int totalSold) {
        this.totalSold = totalSold;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public boolean hasStock() {
        return stock > 0;
    }

    public boolean hasStock(int amount) {
        return stock >= amount;
    }

    public void decreaseStock(int amount) {
        this.stock = Math.max(0, this.stock - amount);
    }

    public void increaseStock(int amount) {
        this.stock = Math. min(maxStock, this.stock + amount);
    }

    public void recordSale(int amount, double revenue) {
        this.totalSold += amount;
        this.totalRevenue += revenue;
        decreaseStock(amount);
    }

    public double getAveragePrice() {
        if (totalSold == 0) {
            return price;
        }
        return totalRevenue / totalSold;
    }

    public int getStockPercentage() {
        if (maxStock == 0) {
            return 0;
        }
        return (int) ((double) stock / maxStock * 100);
    }

    public boolean isOutOfStock() {
        return stock <= 0;
    }

    public boolean isLowStock() {
        return maxStock > 0 && getStockPercentage() < 20;
    }

    public ItemStack createDisplayItem() {
        if (item == null) {
            return null;
        }
        ItemStack display = item.clone();
        display.setAmount(Math.min(stock, 64));
        return display;
    }

    public ItemStack createPurchaseItem(int amount) {
        if (item == null || amount <= 0) {
            return null;
        }
        ItemStack purchase = item.clone();
        purchase.setAmount(amount);
        return purchase;
    }

    @Override
    public ShopItem clone() {
        ShopItem clone = new ShopItem();
        clone.setSlot(this.slot);
        clone.setItem(this.item != null ? this.item.clone() : null);
        clone.setPrice(this. price);
        clone.setStock(this.stock);
        clone.setMaxStock(this.maxStock);
        clone.setTotalSold(this.totalSold);
        clone.setTotalRevenue(this.totalRevenue);
        return clone;
    }
}