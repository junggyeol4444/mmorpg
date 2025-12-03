package com.multiverse.npcai.models;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

/**
 * NPC 상점 데이터 모델
 */
public class Shop {
    private String shopId;
    private int npcId;
    private String name;
    private List<ShopItem> items = new ArrayList<>();

    public Shop(String shopId, int npcId, String name) {
        this.shopId = shopId;
        this.npcId = npcId;
        this.name = name;
    }

    public String getShopId() { return shopId; }
    public int getNpcId() { return npcId; }
    public String getName() { return name; }
    public List<ShopItem> getItems() { return items; }
    public void setItems(List<ShopItem> items) { this.items = items; }

    // === YAML 직렬화/역직렬화 ===

    public static Shop fromYAML(YamlConfiguration yml) {
        String shopId = yml.getString("shopId");
        int npcId = yml.getInt("npcId");
        String name = yml.getString("name");
        Shop shop = new Shop(shopId, npcId, name);
        List<Map<?, ?>> itemsRaw = yml.getMapList("items");
        List<ShopItem> items = new ArrayList<>();
        for (Map<?, ?> m : itemsRaw) {
            items.add(ShopItem.fromMap(m));
        }
        shop.setItems(items);
        return shop;
    }

    public YamlConfiguration toYAML() {
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("shopId", shopId);
        yml.set("npcId", npcId);
        yml.set("name", name);
        if (!items.isEmpty()) {
            List<Map<String, Object>> itemsList = new ArrayList<>();
            for (ShopItem item : items) itemsList.add(item.toMap());
            yml.set("items", itemsList);
        }
        return yml;
    }
}