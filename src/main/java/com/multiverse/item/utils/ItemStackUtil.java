package com.multiverse.item.  utils;

import org.  bukkit.Material;
import org. bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class ItemStackUtil {
    
    /**
     * 아이템이 유효한지 확인 (null 체크)
     */
    public static boolean isValidItem(ItemStack item) {
        return item != null && item.getType() != Material.AIR && item.getAmount() > 0;
    }
    
    /**
     * 아이템의 디스플레이 네임 가져오기
     */
    public static String getDisplayName(ItemStack item) {
        if (! isValidItem(item)) {
            return "";
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        
        return item.getType().toString();
    }
    
    /**
     * 아이템의 로어 가져오기
     */
    public static List<String> getLore(ItemStack item) {
        if (!isValidItem(item)) {
            return new ArrayList<>();
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            return new ArrayList<>(meta.getLore());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * 아이템의 로어 설정
     */
    public static void setLore(ItemStack item, List<String> lore) {
        if (!isValidItem(item)) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }
    
    /**
     * 아이템의 로어에 라인 추가
     */
    public static void addLoreLine(ItemStack item, String line) {
        if (!isValidItem(item)) {
            return;
        }
        
        List<String> lore = getLore(item);
        lore. add(line);
        setLore(item, lore);
    }
    
    /**
     * 아이템 복사
     */
    public static ItemStack copy(ItemStack item) {
        if (!isValidItem(item)) {
            return new ItemStack(Material.AIR);
        }
        
        return new ItemStack(item);
    }
    
    /**
     * 지정된 개수만큼 아이템 복사
     */
    public static ItemStack copyAmount(ItemStack item, int amount) {
        if (!isValidItem(item)) {
            return new ItemStack(Material.AIR);
        }
        
        ItemStack copy = copy(item);
        copy.setAmount(amount);
        return copy;
    }
    
    /**
     * 두 아이템이 같은지 비교 (메타 제외)
     */
    public static boolean isSameType(ItemStack item1, ItemStack item2) {
        if (!isValidItem(item1) || !isValidItem(item2)) {
            return false;
        }
        
        return item1.getType() == item2.getType();
    }
    
    /**
     * 아이템이 인챈트되어 있는지 확인
     */
    public static boolean isEnchanted(ItemStack item) {
        if (!isValidItem(item)) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasEnchants();
    }
    
    /**
     * 아이템 스택 합치기
     */
    public static ItemStack combineStacks(ItemStack item1, ItemStack item2) {
        if (!isValidItem(item1)) {
            return item2;
        }
        if (!isValidItem(item2)) {
            return item1;
        }
        
        if (! isSameType(item1, item2)) {
            return item1;
        }
        
        ItemStack combined = copy(item1);
        combined.setAmount(Math.min(64, item1.getAmount() + item2.getAmount()));
        return combined;
    }
    
    /**
     * 아이템 비활성화 (클릭 불가)
     */
    public static void disableItem(ItemStack item) {
        if (!isValidItem(item)) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
    }
}