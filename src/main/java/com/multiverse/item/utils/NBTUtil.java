package com.multiverse.item.  utils;

import org.bukkit.  inventory.ItemStack;
import org. bukkit. NamespacedKey;
import org.bukkit.persistence.  PersistentDataType;
import org.bukkit. persistence.PersistentDataContainer;
import org.bukkit.inventory.meta.  ItemMeta;

public class NBTUtil {
    
    /**
     * 문자열 데이터 설정
     */
    public static void setString(ItemStack item, NamespacedKey key, String value) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(key, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }
    
    /**
     * 문자열 데이터 가져오기
     */
    public static String getString(ItemStack item, NamespacedKey key, String defaultValue) {
        if (item == null || item.getItemMeta() == null) {
            return defaultValue;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        
        if (data.has(key, PersistentDataType.STRING)) {
            return data.get(key, PersistentDataType.STRING);
        }
        
        return defaultValue;
    }
    
    /**
     * 정수 데이터 설정
     */
    public static void setInt(ItemStack item, NamespacedKey key, int value) {
        if (item == null || item.  getItemMeta() == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(key, PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
    }
    
    /**
     * 정수 데이터 가져오기
     */
    public static int getInt(ItemStack item, NamespacedKey key, int defaultValue) {
        if (item == null || item. getItemMeta() == null) {
            return defaultValue;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        
        if (data.has(key, PersistentDataType. INTEGER)) {
            return data. get(key, PersistentDataType.INTEGER);
        }
        
        return defaultValue;
    }
    
    /**
     * 더블 데이터 설정
     */
    public static void setDouble(ItemStack item, NamespacedKey key, double value) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(key, PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
    }
    
    /**
     * 더블 데이터 가져오기
     */
    public static double getDouble(ItemStack item, NamespacedKey key, double defaultValue) {
        if (item == null || item.getItemMeta() == null) {
            return defaultValue;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        
        if (data.has(key, PersistentDataType.DOUBLE)) {
            return data.get(key, PersistentDataType.DOUBLE);
        }
        
        return defaultValue;
    }
    
    /**
     * 데이터 존재 확인
     */
    public static boolean hasData(ItemStack item, NamespacedKey key, PersistentDataType<? > type) {
        if (item == null || item.getItemMeta() == null) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(key, type);
    }
    
    /**
     * 데이터 삭제
     */
    public static void removeData(ItemStack item, NamespacedKey key) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.remove(key);
        item.setItemMeta(meta);
    }
    
    /**
     * 모든 NBT 데이터 삭제
     */
    public static void clearAllData(ItemStack item) {
        if (item == null || item. getItemMeta() == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        
        // 모든 키 제거
        for (NamespacedKey key : data.getKeys()) {
            data.remove(key);
        }
        
        item.setItemMeta(meta);
    }
    
    /**
     * 바이트 데이터 설정
     */
    public static void setByte(ItemStack item, NamespacedKey key, byte value) {
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(key, PersistentDataType.BYTE, value);
        item.setItemMeta(meta);
    }
    
    /**
     * 바이트 데이터 가져오기
     */
    public static byte getByte(ItemStack item, NamespacedKey key, byte defaultValue) {
        if (item == null || item.getItemMeta() == null) {
            return defaultValue;
        }
        
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta. getPersistentDataContainer();
        
        if (data.has(key, PersistentDataType.BYTE)) {
            return data.get(key, PersistentDataType.BYTE);
        }
        
        return defaultValue;
    }
}