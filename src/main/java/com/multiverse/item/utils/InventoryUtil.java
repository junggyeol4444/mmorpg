package com.multiverse.item.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory. ItemStack;
import org.bukkit.Material;

public class InventoryUtil {
    
    /**
     * 플레이어 인벤토리에 빈 슬롯 있는지 확인
     */
    public static boolean hasEmptySlot(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 플레이어 인벤토리에 빈 슬롯 개수
     */
    public static int getEmptySlotCount(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material. AIR) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 플레이어 인벤토리에 아이템 추가 가능한지 확인
     */
    public static boolean canAddItem(Player player, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        
        int itemsToAdd = item.getAmount();
        
        for (ItemStack invItem : player.getInventory(). getContents()) {
            if (invItem == null || invItem.getType() == Material.AIR) {
                return true;
            }
            
            if (invItem.isSimilar(item)) {
                int space = 64 - invItem.getAmount();
                if (space > 0) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 플레이어 인벤토리에 아이템 추가
     */
    public static boolean addItem(Player player, ItemStack item) {
        if (! canAddItem(player, item)) {
            return false;
        }
        
        player.getInventory().addItem(item);
        return true;
    }
    
    /**
     * 플레이어 인벤토리에서 아이템 제거
     */
    public static boolean removeItem(Player player, ItemStack item, int amount) {
        int removed = 0;
        
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack invItem = player.getInventory().getItem(i);
            
            if (invItem != null && invItem.isSimilar(item)) {
                int removeAmount = Math.min(invItem.getAmount(), amount - removed);
                invItem.setAmount(invItem. getAmount() - removeAmount);
                removed += removeAmount;
                
                if (removed >= amount) {
                    break;
                }
            }
        }
        
        return removed == amount;
    }
    
    /**
     * 플레이어가 특정 아이템을 가지고 있는지 확인
     */
    public static boolean hasItem(Player player, Material material) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 플레이어가 특정 아이템의 개수
     */
    public static int getItemCount(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory(). getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }
    
    /**
     * 인벤토리 비우기
     */
    public static void clearInventory(Player player) {
        player.getInventory().clear();
    }
    
    /**
     * 인벤토리가 비어있는지 확인
     */
    public static boolean isInventoryEmpty(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 손에 들고 있는 아이템 가져오기
     */
    public static ItemStack getHandItem(Player player) {
        return player.getInventory().getItemInMainHand();
    }
    
    /**
     * 손에 들고 있는 아이템 설정
     */
    public static void setHandItem(Player player, ItemStack item) {
        player. getInventory().setItemInMainHand(item);
    }
    
    /**
     * 갑옷 아이템 가져오기 (헬멧)
     */
    public static ItemStack getHelmet(Player player) {
        return player.getInventory().getHelmet();
    }
    
    /**
     * 갑옷 아이템 가져오기 (체스트플레이트)
     */
    public static ItemStack getChestplate(Player player) {
        return player.getInventory().getChestplate();
    }
    
    /**
     * 갑옷 아이템 가져오기 (레깅스)
     */
    public static ItemStack getLeggings(Player player) {
        return player.getInventory().getLeggings();
    }
    
    /**
     * 갑옷 아이템 가져오기 (부츠)
     */
    public static ItemStack getBoots(Player player) {
        return player.getInventory().getBoots();
    }
    
    /**
     * 인벤토리 크기
     */
    public static int getInventorySize(Inventory inventory) {
        return inventory.getSize();
    }
    
    /**
     * 인벤토리에서 특정 재료의 개수 확인
     */
    public static int countMaterial(Inventory inventory, Material material) {
        int count = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material) {
                count += item. getAmount();
            }
        }
        return count;
    }
}