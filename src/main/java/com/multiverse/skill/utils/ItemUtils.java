package com.multiverse.skill.utils;

import org.bukkit.Material;
import org. bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

/**
 * 아이템 유틸리티
 */
public class ItemUtils {

    /**
     * 아이템 생성
     */
    public static ItemStack createItem(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 아이템에 설명 추가
     */
    public static ItemStack addLore(ItemStack item, List<String> lore) {
        if (item == null || lore == null) {
            return item;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 아이템에 한 줄 설명 추가
     */
    public static ItemStack addLoreLine(ItemStack item, String line) {
        if (item == null || line == null) {
            return item;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            List<String> lore = meta.getLore();
            
            if (lore == null) {
                lore = new ArrayList<>();
            }

            lore.add(line);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 아이템 개수 설정
     */
    public static ItemStack setAmount(ItemStack item, int amount) {
        if (item == null) {
            return item;
        }

        item.setAmount(amount);
        return item;
    }

    /**
     * 아이템 내구도 설정
     */
    public static ItemStack setDurability(ItemStack item, short durability) {
        if (item == null) {
            return item;
        }

        item.setDurability(durability);
        return item;
    }

    /**
     * 아이템 커스텀 모델 데이터 설정
     */
    public static ItemStack setCustomModelData(ItemStack item, int modelData) {
        if (item == null) {
            return item;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta. setCustomModelData(modelData);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * 아이템 비교
     */
    public static boolean isSameItem(ItemStack item1, ItemStack item2) {
        if (item1 == null && item2 == null) {
            return true;
        }

        if (item1 == null || item2 == null) {
            return false;
        }

        return item1.getType(). equals(item2.getType());
    }

    /**
     * 아이템이 비어있는지 확인
     */
    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType(). equals(Material.AIR);
    }

    /**
     * 아이템 복제
     */
    public static ItemStack cloneItem(ItemStack item) {
        if (item == null) {
            return null;
        }

        return item.clone();
    }

    /**
     * 아이템 표시 이름 조회
     */
    public static String getDisplayName(ItemStack item) {
        if (item == null) {
            return "";
        }

        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }

        return item.getType(). name();
    }

    /**
     * 아이템 설명 조회
     */
    public static List<String> getLore(ItemStack item) {
        if (item == null) {
            return new ArrayList<>();
        }

        ItemMeta meta = item.getItemMeta();

        if (meta != null && meta.hasLore()) {
            return meta.getLore();
        }

        return new ArrayList<>();
    }
}