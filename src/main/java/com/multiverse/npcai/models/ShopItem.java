package com.multiverse.npcai.models;

import org.bukkit.Material;

import java.util.*;

/**
 * 상점 아이템 데이터 모델
 */
public class ShopItem {
    private String itemId;
    private String displayName;
    private Material type;
    private int amount;
    private double price;
    private boolean isBuyable;
    private boolean isSellable;

    public ShopItem(String itemId, String displayName, Material type, int amount, double price, boolean isBuyable, boolean isSellable) {
        this.itemId = itemId;
        this.displayName = displayName;
        this.type = type;
        this.amount = amount;
        this.price = price;
        this.isBuyable = isBuyable;
        this.isSellable = isSellable;
    }

    public String getItemId() { return itemId; }
    public String getDisplayName() { return displayName; }
    public Material getType() { return type; }
    public int getAmount() { return amount; }
    public double getPrice() { return price; }
    public boolean isBuyable() { return isBuyable; }
    public boolean isSellable() { return isSellable; }

    // === Map 직렬화/역직렬화 ===
    public static ShopItem fromMap(Map<?, ?> map) {
        String itemId = (String) map.get("itemId");
        String displayName = (String) map.get("displayName");
        Material type = Material.getMaterial((String) map.get("type"));
        int amount = (int) map.getOrDefault("amount", 1);
        double price = (double) map.getOrDefault("price", 0.0);
        boolean buyable = (boolean) map.getOrDefault("isBuyable", true);
        boolean sellable = (boolean) map.getOrDefault("isSellable", false);
        return new ShopItem(itemId, displayName, type, amount, price, buyable, sellable);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("displayName", displayName);
        map.put("type", type.name());
        map.put("amount", amount);
        map.put("price", price);
        map.put("isBuyable", isBuyable);
        map.put("isSellable", isSellable);
        return map;
    }
}