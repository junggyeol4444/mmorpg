package com. multiverse.item. utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class LoreUtil {
    
    /**
     * 아이템의 로어 가져오기
     */
    public static List<String> getLore(ItemStack item) {
        if (item == null || item. getItemMeta() == null) {
            return new ArrayList<>();
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta. hasLore()) {
            return new ArrayList<>(meta.getLore());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * 아이템의 로어 설정
     */
    public static void setLore(ItemStack item, List<String> lore) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
    
    /**
     * 로어에 라인 추가
     */
    public static void addLoreLine(ItemStack item, String line) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        List<String> lore = getLore(item);
        lore. add(line);
        setLore(item, lore);
    }
    
    /**
     * 로어에 라인 추가 (인덱스 지정)
     */
    public static void addLoreLine(ItemStack item, int index, String line) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        List<String> lore = getLore(item);
        if (index >= 0 && index <= lore.size()) {
            lore.add(index, line);
            setLore(item, lore);
        }
    }
    
    /**
     * 로어 라인 제거
     */
    public static void removeLoreLine(ItemStack item, int index) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        List<String> lore = getLore(item);
        if (index >= 0 && index < lore.size()) {
            lore.remove(index);
            setLore(item, lore);
        }
    }
    
    /**
     * 로어 라인 수정
     */
    public static void modifyLoreLine(ItemStack item, int index, String newLine) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        List<String> lore = getLore(item);
        if (index >= 0 && index < lore.size()) {
            lore.set(index, newLine);
            setLore(item, lore);
        }
    }
    
    /**
     * 로어에 빈 줄 추가
     */
    public static void addEmptyLine(ItemStack item) {
        addLoreLine(item, "");
    }
    
    /**
     * 모든 로어 제거
     */
    public static void clearLore(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        meta. setLore(new ArrayList<>());
        item.setItemMeta(meta);
    }
    
    /**
     * 로어 라인 개수
     */
    public static int getLoreLineCount(ItemStack item) {
        return getLore(item).size();
    }
    
    /**
     * 로어에 특정 텍스트 포함 여부
     */
    public static boolean containsLoreText(ItemStack item, String text) {
        List<String> lore = getLore(item);
        for (String line : lore) {
            if (line.contains(text)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 로어에서 특정 텍스트 찾기
     */
    public static int findLoreLineIndex(ItemStack item, String text) {
        List<String> lore = getLore(item);
        for (int i = 0; i < lore.size(); i++) {
            if (lore.get(i).contains(text)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 로어에 구분선 추가
     */
    public static void addSeperator(ItemStack item) {
        addLoreLine(item, "§7§m─────────────────§r");
    }
    
    /**
     * 로어 포맷팅 (색상 코드 적용)
     */
    public static List<String> formatLore(List<String> lore) {
        List<String> formatted = new ArrayList<>();
        for (String line : lore) {
            formatted.add(ColorUtil.translateColorCodes(line));
        }
        return formatted;
    }
    
    /**
     * 로어 길이 제한
     */
    public static List<String> limitLoreLength(List<String> lore, int maxLines) {
        if (lore.size() <= maxLines) {
            return lore;
        }
        
        List<String> limited = new ArrayList<>();
        for (int i = 0; i < maxLines - 1; i++) {
            limited.add(lore.get(i));
        }
        limited.add("§7...  외 " + (lore.size() - maxLines + 1) + "줄");
        return limited;
    }
}