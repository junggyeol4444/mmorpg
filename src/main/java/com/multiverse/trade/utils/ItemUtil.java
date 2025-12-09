package com.multiverse. trade.utils;

import org.bukkit.Material;
import org. bukkit.  enchantments.Enchantment;
import org.bukkit. inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Base64;
import java.io.*;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.  BukkitObjectOutputStream;

public class ItemUtil {

    public static String getItemName(ItemStack item) {
        if (item == null) {
            return "없음";
        }
        
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        
        String materialName = item.getType().name();
        return formatMaterialName(materialName);
    }

    public static String formatMaterialName(String materialName) {
        String[] parts = materialName.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();
        
        for (String part : parts) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(Character.toUpperCase(part. charAt(0)));
            result.append(part.substring(1));
        }
        
        return result.toString();
    }

    public static String getItemKey(ItemStack item) {
        if (item == null) {
            return "null";
        }
        
        StringBuilder key = new StringBuilder();
        key.append(item.getType().name());
        
        if (item.  hasItemMeta()) {
            ItemMeta meta = item.  getItemMeta();
            
            if (meta. hasDisplayName()) {
                key.append(": ").append(meta.getDisplayName().hashCode());
            }
            
            if (meta.hasLore()) {
                key.append(": ").append(meta.getLore().hashCode());
            }
            
            if (meta.hasEnchants()) {
                for (Enchantment enchant : meta.getEnchants().keySet()) {
                    key.append(":").append(enchant.  getKey().getKey());
                    key.append("-").append(meta.getEnchantLevel(enchant));
                }
            }
        }
        
        return key.toString();
    }

    public static ItemStack parseItemFromKey(String key) {
        if (key == null || key. equals("null")) {
            return null;
        }
        
        String[] parts = key. split(":");
        String materialName = parts[0];
        
        try {
            Material material = Material.valueOf(materialName);
            return new ItemStack(material);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        if (item1 == null || item2 == null) {
            return item1 == item2;
        }
        
        if (item1.getType() != item2.getType()) {
            return false;
        }
        
        if (item1.hasItemMeta() != item2.hasItemMeta()) {
            return false;
        }
        
        if (item1.hasItemMeta()) {
            ItemMeta meta1 = item1. getItemMeta();
            ItemMeta meta2 = item2.getItemMeta();
            
            if (meta1.hasDisplayName() != meta2.hasDisplayName()) {
                return false;
            }
            
            if (meta1.hasDisplayName() && !  meta1.getDisplayName().equals(meta2.getDisplayName())) {
                return false;
            }
            
            if (meta1.hasLore() != meta2.hasLore()) {
                return false;
            }
            
            if (meta1.hasLore() && ! meta1.getLore().equals(meta2.getLore())) {
                return false;
            }
        }
        
        return true;
    }

    public static String serializeItem(ItemStack item) {
        if (item == null) {
            return null;
        }
        
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }

    public static ItemStack deserializeItem(String data) {
        if (data == null || data.  isEmpty()) {
            return null;
        }
        
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isAir(ItemStack item) {
        return item == null || item.getType().isAir();
    }

    public static String getItemDescription(ItemStack item) {
        if (item == null) {
            return "없음";
        }
        
        String name = getItemName(item);
        int amount = item.getAmount();
        
        return name + " x" + amount;
    }
}