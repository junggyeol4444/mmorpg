package com.multiverse.party.utils;

import org.bukkit.inventory.ItemStack;

/**
 * 아이템 관련 유틸리티
 */
public class ItemUtil {

    /** 아이템 이름 반환 예시 */
    public static String getItemName(ItemStack item) {
        if (item == null) return "";
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
            return item.getItemMeta().getDisplayName();
        return item.getType().name();
    }

    // 필요 시 여기서 아이템 조작 메서드 확장
}