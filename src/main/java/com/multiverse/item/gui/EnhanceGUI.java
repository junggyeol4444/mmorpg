package com.multiverse.item.   gui;

import org.bukkit. Bukkit;
import org.bukkit.Material;
import org.bukkit. inventory.Inventory;
import org. bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;
import com.multiverse. item.  data.CustomItem;
import java.util.ArrayList;
import java.util.List;

public class EnhanceGUI {
    
    private ItemCore plugin;
    private Player player;
    private CustomItem item;
    private Inventory inventory;
    
    /**
     * 기본 생성자
     */
    public EnhanceGUI(ItemCore plugin, Player player, CustomItem item) {
        this.plugin = plugin;
        this.player = player;
        this.item = item;
        this.inventory = createGUI();
    }
    
    /**
     * GUI 인벤토리 생성
     */
    private Inventory createGUI() {
        Inventory inv = Bukkit.createInventory(null, 27, "§6강화 시스템");
        
        // 강화 대상 아이템 표시
        ItemStack targetItem = createItemDisplay(item, "강화 대상 아이템");
        inv.setItem(11, targetItem);
        
        // 강화 정보 표시
        ItemStack infoItem = createInfoDisplay();
        inv.setItem(13, infoItem);
        
        // 강화 재료 표시
        ItemStack materialItem = createMaterialDisplay();
        inv.setItem(15, materialItem);
        
        // 강화 버튼
        ItemStack enhanceButton = createButton(Material.GOLD_BLOCK, "§a강화 실행", "비용을 지불하고 강화를 시작합니다.");
        inv.setItem(22, enhanceButton);
        
        // 닫기 버튼
        ItemStack closeButton = createButton(Material.BARRIER, "§c닫기", "GUI를 닫습니다.");
        inv.setItem(26, closeButton);
        
        return inv;
    }
    
    /**
     * 아이템 표시 생성
     */
    private ItemStack createItemDisplay(CustomItem item, String title) {
        ItemStack display = new ItemStack(Material. DIAMOND_SWORD);
        ItemMeta meta = display.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b" + title);
            List<String> lore = new ArrayList<>();
            lore.add("§7이름: " + item.getName());
            lore.add("§7강화: +" + item.getEnhanceLevel());
            lore.add("§7희귀도: " + item. getRarity(). getKoreanName());
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }
    
    /**
     * 강화 정보 표시
     */
    private ItemStack createInfoDisplay() {
        ItemStack infoItem = new ItemStack(Material. BOOK);
        ItemMeta meta = infoItem.getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName("§b강화 정보");
            List<String> lore = new ArrayList<>();
            
            int currentLevel = item.getEnhanceLevel();
            int nextLevel = currentLevel + 1;
            double successRate = 100.0 - (nextLevel * 5.0);
            successRate = Math.max(10.0, successRate);
            
            lore.add("§7현재 강화: +" + currentLevel);
            lore.add("§7다음 강화: +" + nextLevel);
            lore.add("§7성공률: §a" + String.format("%.1f%%", successRate));
            lore.add("§7비용: §c" + (1000 + (nextLevel * 500)) + " Gold");
            
            meta.setLore(lore);
            infoItem.setItemMeta(meta);
        }
        
        return infoItem;
    }
    
    /**
     * 재료 표시
     */
    private ItemStack createMaterialDisplay() {
        ItemStack materialItem = new ItemStack(Material. EMERALD);
        ItemMeta meta = materialItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b강화 재료");
            List<String> lore = new ArrayList<>();
            
            int nextLevel = item.getEnhanceLevel() + 1;
            int materialCost = 10 + (nextLevel * 5);
            
            lore. add("§7필요 재료: " + materialCost);
            lore. add("§7남은 재료: " + player.getInventory().first(Material.EMERALD) + "/§a" + materialCost);
            
            meta.setLore(lore);
            materialItem.setItemMeta(meta);
        }
        
        return materialItem;
    }
    
    /**
     * 버튼 생성
     */
    private ItemStack createButton(Material material, String name, String...  lore) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> loreList = new ArrayList<>();
            for (String line : lore) {
                loreList.add(line);
            }
            meta.setLore(loreList);
            button.setItemMeta(meta);
        }
        
        return button;
    }
    
    /**
     * GUI 열기
     */
    public void open() {
        player.openInventory(inventory);
    }
    
    /**
     * 인벤토리 반환
     */
    public Inventory getInventory() {
        return inventory;
    }
    
    /**
     * 플레이어 반환
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * 아이템 반환
     */
    public CustomItem getItem() {
        return item;
    }
}