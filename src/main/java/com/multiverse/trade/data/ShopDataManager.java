package com.multiverse.trade.data;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.models.PlayerShop;
import com.multiverse.trade.models.ShopItem;
import org.bukkit. Bukkit;
import org.bukkit. Location;
import org. bukkit.World;
import org. bukkit.configuration.ConfigurationSection;
import org.bukkit. configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit. inventory.ItemStack;

import java.io. File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ShopDataManager {

    private final TradeCore plugin;
    private final File shopsFolder;
    private final Map<UUID, PlayerShop> cachedShops = new ConcurrentHashMap<>();

    public ShopDataManager(TradeCore plugin) {
        this.plugin = plugin;
        this. shopsFolder = new File(plugin.getDataFolder(), "shops");
        if (!shopsFolder. exists()) {
            shopsFolder.mkdirs();
        }
    }

    public void loadAll() {
        cachedShops.clear();
        
        File[] files = shopsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return;
        }

        for (File file : files) {
            try {
                PlayerShop shop = loadShop(file);
                if (shop != null) {
                    cachedShops.put(shop.getShopId(), shop);
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "상점 로드 실패: " + file.getName(), e);
            }
        }

        plugin.getLogger().info("상점 " + cachedShops.size() + "개 로드됨");
    }

    private PlayerShop loadShop(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection section = config.getConfigurationSection("shop");
        if (section == null) {
            return null;
        }

        PlayerShop shop = new PlayerShop();
        shop.setShopId(UUID.fromString(section.getString("shop-id")));
        shop.setOwner(UUID.fromString(section.getString("owner")));
        shop.setShopName(section. getString("name", "상점"));

        if (section.contains("location")) {
            ConfigurationSection locSection = section.getConfigurationSection("location");
            if (locSection != null) {
                World world = Bukkit.getWorld(locSection.getString("world", "world"));
                if (world != null) {
                    Location loc = new Location(
                        world,
                        locSection. getDouble("x"),
                        locSection.getDouble("y"),
                        locSection.getDouble("z")
                    );
                    shop.setLocation(loc);
                }
            }
        }

        List<ShopItem> items = new ArrayList<>();
        if (section.contains("items")) {
            List<Map<?, ?>> itemsList = section.getMapList("items");
            for (Map<?, ?> itemMap : itemsList) {
                ShopItem shopItem = new ShopItem();
                shopItem.setSlot((Integer) itemMap.get("slot"));
                shopItem. setPrice(((Number) itemMap.get("price")).doubleValue());
                shopItem.setStock((Integer) itemMap.get("stock"));
                shopItem. setMaxStock((Integer) itemMap.getOrDefault("max-stock", shopItem.getStock()));
                shopItem.setTotalSold((Integer) itemMap.getOrDefault("total-sold", 0));
                shopItem.setTotalRevenue(((Number) itemMap.getOrDefault("total-revenue", 0.0)).doubleValue());

                @SuppressWarnings("unchecked")
                Map<String, Object> itemData = (Map<String, Object>) itemMap.get("item");
                if (itemData != null) {
                    ItemStack item = ItemStack.deserialize(itemData);
                    shopItem.setItem(item);
                }

                items.add(shopItem);
            }
        }
        shop.setItems(items);

        shop.setOpen(section.getBoolean("is-open", true));
        shop.setDescription(section.getString("description", ""));
        shop.setTotalSales(section.getDouble("statistics.total-sales", 0));
        shop.setTotalOrders(section.getInt("statistics.total-orders", 0));
        shop.setCreatedTime(section.getLong("created-time", System.currentTimeMillis()));

        return shop;
    }

    public void saveShop(PlayerShop shop) {
        if (shop == null) {
            return;
        }

        File file = new File(shopsFolder, shop.getShopId().toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        ConfigurationSection section = config.createSection("shop");
        section.set("shop-id", shop.getShopId().toString());
        section.set("owner", shop.getOwner().toString());
        section.set("name", shop.getShopName());

        if (shop. getLocation() != null) {
            ConfigurationSection locSection = section.createSection("location");
            locSection.set("world", shop.getLocation().getWorld().getName());
            locSection. set("x", shop.getLocation().getX());
            locSection.set("y", shop.getLocation().getY());
            locSection.set("z", shop. getLocation().getZ());
        }

        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (ShopItem shopItem : shop.getItems()) {
            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("slot", shopItem. getSlot());
            itemMap.put("price", shopItem.getPrice());
            itemMap.put("stock", shopItem.getStock());
            itemMap.put("max-stock", shopItem.getMaxStock());
            itemMap.put("total-sold", shopItem.getTotalSold());
            itemMap.put("total-revenue", shopItem. getTotalRevenue());
            
            if (shopItem.getItem() != null) {
                itemMap.put("item", shopItem.getItem().serialize());
            }
            
            itemsList.add(itemMap);
        }
        section.set("items", itemsList);

        section.set("is-open", shop.isOpen());
        section.set("description", shop.getDescription());
        section.set("statistics.total-sales", shop.getTotalSales());
        section.set("statistics.total-orders", shop.getTotalOrders());
        section.set("created-time", shop. getCreatedTime());

        try {
            config. save(file);
            cachedShops.put(shop.getShopId(), shop);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "상점 저장 실패: " + shop.getShopId(), e);
        }
    }

    public void deleteShop(UUID shopId) {
        cachedShops.remove(shopId);
        
        File file = new File(shopsFolder, shopId.toString() + ".yml");
        if (file.exists()) {
            file. delete();
        }
    }

    public PlayerShop getShop(UUID shopId) {
        return cachedShops.get(shopId);
    }

    public Map<UUID, PlayerShop> getAllShops() {
        return new HashMap<>(cachedShops);
    }

    public void saveAll() {
        for (PlayerShop shop : cachedShops.values()) {
            saveShop(shop);
        }
        plugin.getLogger().info("상점 " + cachedShops.size() + "개 저장됨");
    }
}