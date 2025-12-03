package com.multiverse.npcai.managers;

import com.multiverse.npcai.NPCAICore;
import com.multiverse.npcai.models.*;
import com.multiverse.npcai.models.enums.ShopType;
import com.multiverse.npcai.utils.ConfigUtil;
import com.multiverse.npcai.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * NPC 상점 시스템, 거래/할인/재고
 */
public class ShopManager {

    private final NPCAICore plugin;
    private final DataManager dataManager;
    private final ReputationManager reputationManager;
    private final ConfigUtil config;

    public ShopManager(NPCAICore plugin, DataManager dataManager, ReputationManager reputationManager, ConfigUtil config) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.reputationManager = reputationManager;
        this.config = config;
    }

    // === 상점 관리 ===

    public Shop getShop(String shopId) {
        return dataManager.getShop(shopId);
    }

    public Shop getNPCShop(int npcId) {
        return dataManager.getShopByNPC(npcId);
    }

    public void createShop(int npcId, String name, ShopType type) {
        Shop shop = new Shop(UUID.randomUUID().toString(), npcId, name, type);
        dataManager.saveShop(shop);
    }

    // === 상품 관리 ===

    public void addItem(String shopId, ShopItem item) {
        Shop shop = getShop(shopId);
        if (shop == null) return;
        shop.getItems().add(item);
        dataManager.saveShop(shop);
    }

    public void removeItem(String shopId, String itemId) {
        Shop shop = getShop(shopId);
        if (shop == null) return;
        shop.getItems().removeIf(i -> i.getItemId().equals(itemId));
        dataManager.saveShop(shop);
    }

    public void updateStock(String shopId, String itemId, int amount) {
        Shop shop = getShop(shopId);
        if (shop == null) return;
        // 재고 관리
        Optional<ShopItem> itemOpt = shop.getItems().stream().filter(i -> i.getItemId().equals(itemId)).findFirst();
        itemOpt.ifPresent(item -> item.setCurrentStock(amount));
        dataManager.saveShop(shop);
    }

    // === 거래 ===

    public void buyItem(Player player, Shop shop, String itemId, int amount) {
        ShopItem item = shop.getItemById(itemId);
        if (item == null) {
            player.sendMessage(config.getString("messages.shop.insufficient-stock"));
            return;
        }
        if (amount < 1) amount = 1;
        double basePrice = item.getBuyPrice() * amount;
        double discount = getDiscount(player, shop.getNpcId());
        double finalPrice = basePrice * discount;
        // 재고 확인
        if (item.getMaxStock() > 0 && item.getCurrentStock() < amount) {
            player.sendMessage(config.getString("messages.shop.insufficient-stock"));
            return;
        }
        // 돈 확인
        if (!plugin.getEconomy().has(player, finalPrice)) {
            player.sendMessage(config.getString("messages.shop.insufficient-money").replace("{price}", String.valueOf(finalPrice)));
            return;
        }
        // 돈 차감
        plugin.getEconomy().withdrawPlayer(player, finalPrice);
        player.getInventory().addItem(item.getItem().clone());
        if (item.getMaxStock() > 0) item.setCurrentStock(item.getCurrentStock() - amount);
        dataManager.saveShop(shop);
        player.sendMessage(config.getString("messages.shop.bought")
                .replace("{item}", item.getItem().getType().name())
                .replace("{amount}", String.valueOf(amount))
                .replace("{price}", String.valueOf(finalPrice)));
    }

    public void sellItem(Player player, Shop shop, ItemStack item, int amount) {
        ShopItem shopItem = shop.getItemByMaterial(item.getType().name());
        if (shopItem == null) {
            player.sendMessage(config.getString("messages.shop.insufficient-stock"));
            return;
        }
        double price = shopItem.getSellPrice() * amount;
        plugin.getEconomy().depositPlayer(player, price);
        item.setAmount(item.getAmount() - amount);
        player.sendMessage(config.getString("messages.shop.sold")
                .replace("{item}", shopItem.getItem().getType().name())
                .replace("{amount}", String.valueOf(amount))
                .replace("{price}", String.valueOf(price)));
        // 재고 증가 (필요시)
    }

    // === 가격 계산 ===

    public double calculatePrice(Player player, int npcId, double basePrice) {
        double disc = getDiscount(player, npcId);
        return basePrice * disc;
    }

    public double getDiscount(Player player, int npcId) {
        return reputationManager.getPriceMultiplier(player, npcId);
    }

    // === 리셋 ===

    public void resetShop(String shopId) {
        Shop shop = getShop(shopId);
        if (shop == null) return;
        for (ShopItem item : shop.getItems()) {
            if (item.getMaxStock() > 0) {
                item.setCurrentStock(item.getMaxStock());
            }
        }
        shop.setLastReset(System.currentTimeMillis());
        dataManager.saveShop(shop);
    }

    public void resetAllShops() {
        List<Shop> shops = dataManager.getAllShops();
        for (Shop s : shops) resetShop(s.getShopId());
    }

    // === ShopItem 생성 유틸 ===
    public ShopItem makeShopItem(ItemStack item, double buyPrice, double sellPrice) {
        return new ShopItem(UUID.randomUUID().toString(), item, buyPrice, sellPrice, -1, -1, 0);
    }
}