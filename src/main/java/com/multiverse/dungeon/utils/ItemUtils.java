package com.multiverse.dungeon.utils;

import org.bukkit.Material;
import org.bukkit. enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org. bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

/**
 * 아이템 유틸리티
 */
public class ItemUtils {

    /**
     * 아이템 생성
     *
     * @param material 재료
     * @param amount 개수
     * @return 생성된 아이템
     */
    public static ItemStack createItem(Material material, int amount) {
        return new ItemStack(material, amount);
    }

    /**
     * 커스텀 이름이 있는 아이템 생성
     *
     * @param material 재료
     * @param amount 개수
     * @param name 아이템 이름
     * @return 생성된 아이템
     */
    public static ItemStack createItem(Material material, int amount, String name) {
        ItemStack item = new ItemStack(material, amount);
        setDisplayName(item, name);
        return item;
    }

    /**
     * 설명이 있는 아이템 생성
     *
     * @param material 재료
     * @param amount 개수
     * @param name 아이템 이름
     * @param lore 설명
     * @return 생성된 아이템
     */
    public static ItemStack createItem(Material material, int amount, String name, List<String> lore) {
        ItemStack item = createItem(material, amount, name);
        setLore(item, lore);
        return item;
    }

    /**
     * 아이템의 표시명 설정
     *
     * @param item 아이템
     * @param name 이름
     */
    public static void setDisplayName(ItemStack item, String name) {
        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(MessageUtils.colorize(name));
            item.setItemMeta(meta);
        }
    }

    /**
     * 아이템의 표시명 가져오기
     *
     * @param item 아이템
     * @return 표시명
     */
    public static String getDisplayName(ItemStack item) {
        if (item == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }

        return item.  getType().name();
    }

    /**
     * 아이템의 설명 설정
     *
     * @param item 아이템
     * @param lore 설명
     */
    public static void setLore(ItemStack item, List<String> lore) {
        if (item == null || lore == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> colorizedLore = new ArrayList<>();
            for (String line : lore) {
                colorizedLore. add(MessageUtils.colorize(line));
            }
            meta.setLore(colorizedLore);
            item.setItemMeta(meta);
        }
    }

    /**
     * 아이템의 설명 가져오기
     *
     * @param item 아이템
     * @return 설명
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

    /**
     * 아이템에 설명 한 줄 추가
     *
     * @param item 아이템
     * @param line 설명 줄
     */
    public static void addLoreLine(ItemStack item, String line) {
        if (item == null) {
            return;
        }

        List<String> lore = getLore(item);
        lore. add(MessageUtils.colorize(line));
        setLore(item, lore);
    }

    /**
     * 아이템에 빈 설명 줄 추가
     *
     * @param item 아이템
     */
    public static void addBlankLoreLine(ItemStack item) {
        addLoreLine(item, "");
    }

    /**
     * 아이템에 인챈트 추가
     *
     * @param item 아이템
     * @param enchantment 인챈트
     * @param level 레벨
     */
    public static void addEnchantment(ItemStack item, Enchantment enchantment, int level) {
        if (item == null) {
            return;
        }

        item.addEnchantment(enchantment, level);
    }

    /**
     * 아이템에서 인챈트 제거
     *
     * @param item 아이템
     * @param enchantment 인챈트
     */
    public static void removeEnchantment(ItemStack item, Enchantment enchantment) {
        if (item == null) {
            return;
        }

        item.removeEnchantment(enchantment);
    }

    /**
     * 아이템이 인챈트를 가지고 있는지 확인
     *
     * @param item 아이템
     * @param enchantment 인챈트
     * @return 가지고 있으면 true
     */
    public static boolean hasEnchantment(ItemStack item, Enchantment enchantment) {
        if (item == null) {
            return false;
        }

        return item.containsEnchantment(enchantment);
    }

    /**
     * 아이템의 내구성 설정
     *
     * @param item 아이템
     * @param durability 내구성
     */
    public static void setDurability(ItemStack item, short durability) {
        if (item == null) {
            return;
        }

        item. setDurability(durability);
    }

    /**
     * 아이템 복제
     *
     * @param item 원본 아이템
     * @return 복제된 아이템
     */
    public static ItemStack clone(ItemStack item) {
        if (item == null) {
            return null;
        }

        return item.clone();
    }

    /**
     * 아이템이 비어있는지 확인
     *
     * @param item 아이템
     * @return 비어있으면 true
     */
    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR || item. getAmount() == 0;
    }

    /**
     * 두 아이템이 같은지 확인 (개수 무시)
     *
     * @param item1 아이템1
     * @param item2 아이템2
     * @return 같으면 true
     */
    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) {
            return false;
        }

        return item1.isSimilar(item2);
    }

    /**
     * 아이템의 재료 이름
     *
     * @param item 아이템
     * @return 재료 이름
     */
    public static String getMaterialName(ItemStack item) {
        if (item == null) {
            return "Unknown";
        }

        return item.getType().name();
    }

    /**
     * 재료를 기반으로 아이템 생성
     *
     * @param materialName 재료 이름
     * @param amount 개수
     * @return 생성된 아이템
     */
    public static ItemStack createItemByName(String materialName, int amount) {
        try {
            Material material = Material.valueOf(materialName.toUpperCase());
            return new ItemStack(material, amount);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 아이템 개수 설정
     *
     * @param item 아이템
     * @param amount 개수
     */
    public static void setAmount(ItemStack item, int amount) {
        if (item == null) {
            return;
        }

        item.setAmount(Math.max(1, Math.min(64, amount)));
    }

    /**
     * 아이템 개수 증가
     *
     * @param item 아이템
     * @param amount 증가량
     */
    public static void addAmount(ItemStack item, int amount) {
        if (item == null) {
            return;
        }

        setAmount(item, item.getAmount() + amount);
    }

    /**
     * 아이템 개수 감소
     *
     * @param item 아이템
     * @param amount 감소량
     */
    public static void subtractAmount(ItemStack item, int amount) {
        if (item == null) {
            return;
        }

        setAmount(item, item.getAmount() - amount);
    }

    /**
     * 희귀도 기반 색상 코드
     *
     * @param rarity 희귀도 (common, uncommon, rare, epic, legendary)
     * @return 색상 코드
     */
    public static String getRarityColor(String rarity) {
        return switch (rarity. toLowerCase()) {
            case "common" -> "§f";
            case "uncommon" -> "§a";
            case "rare" -> "§b";
            case "epic" -> "§d";
            case "legendary" -> "§6";
            default -> "§7";
        };
    }

    /**
     * 희귀도 기반 포맷
     *
     * @param name 아이템 이름
     * @param rarity 희귀도
     * @return 포맷된 이름
     */
    public static String formatRarity(String name, String rarity) {
        return getRarityColor(rarity) + name;
    }

    /**
     * 아이템 비교 (모든 속성 포함)
     *
     * @param item1 아이템1
     * @param item2 아이템2
     * @return 같으면 true
     */
    public static boolean equals(ItemStack item1, ItemStack item2) {
        if (item1 == null && item2 == null) {
            return true;
        }

        if (item1 == null || item2 == null) {
            return false;
        }

        return item1.equals(item2);
    }
}