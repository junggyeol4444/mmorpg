package com.multiverse.item.gui;

import org.bukkit. Bukkit;
import org. bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit. inventory.ItemStack;
import org.bukkit.inventory.meta. ItemMeta;
import org. bukkit.entity.Player;
import com.multiverse.item. ItemCore;
import com.multiverse.item.data.ItemSet;
import java.util.ArrayList;
import java.util.List;

public class SetInfoGUI {
    
    private ItemCore plugin;
    private Player player;
    private ItemSet itemSet;
    private Inventory inventory;
    private int equippedCount;
    
    /**
     * 기본 생성자
     */
    public SetInfoGUI(ItemCore plugin, Player player, ItemSet itemSet, int equippedCount) {
        this.plugin = plugin;
        this.player = player;
        this.itemSet = itemSet;
        this.equippedCount = equippedCount;
        this.inventory = createGUI();
    }
    
    /**
     * GUI 인벤토리 생성
     */
    private Inventory createGUI() {
        Inventory inv = Bukkit.createInventory(null, 54, "§6세트 정보: " + itemSet.getName());
        
        // 세트 기본 정보
        ItemStack setBasicInfo = createSetBasicInfoDisplay();
        inv.setItem(11, setBasicInfo);
        
        // 세트 완성도
        ItemStack setCompletion = createSetCompletionDisplay();
        inv.setItem(13, setCompletion);
        
        // 세트 보너스 표시
        displaySetBonuses(inv);
        
        // 세트 아이템 목록
        displaySetItems(inv);
        
        // 닫기 버튼
        ItemStack closeButton = createButton(Material.BARRIER, "§c닫기", "GUI를 닫습니다.");
        inv.setItem(49, closeButton);
        
        return inv;
    }
    
    /**
     * 세트 기본 정보 표시
     */
    private ItemStack createSetBasicInfoDisplay() {
        ItemStack setItem = new ItemStack(Material. NETHER_STAR);
        ItemMeta meta = setItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b" + itemSet.getName());
            List<String> lore = new ArrayList<>();
            lore.add("§7세트 ID: " + itemSet.getId());
            lore.add("§7설명: " + (itemSet.getDescription() != null ? itemSet.getDescription() : "없음"));
            meta.setLore(lore);
            setItem.setItemMeta(meta);
        }
        
        return setItem;
    }
    
    /**
     * 세트 완성도 표시
     */
    private ItemStack createSetCompletionDisplay() {
        ItemStack completionItem = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = completionItem.getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName("§b세트 완성도");
            List<String> lore = new ArrayList<>();
            
            int totalItems = itemSet.getItems() != null ? itemSet.getItems().size() : 0;
            double completion = totalItems > 0 ? (equippedCount / (double) totalItems) * 100 : 0;
            
            lore.add("§7착용: " + equippedCount + "/" + totalItems);
            lore.add("§7완성도: " + String.format("%.1f%%", completion));
            
            if (equippedCount == totalItems && totalItems > 0) {
                lore.add("§a세트 완성!  모든 보너스가 활성화됩니다.");
            }
            
            meta.setLore(lore);
            completionItem.setItemMeta(meta);
        }
        
        return completionItem;
    }
    
    /**
     * 세트 보너스 표시
     */
    private void displaySetBonuses(Inventory inv) {
        if (itemSet.getBonuses() == null || itemSet.getBonuses().isEmpty()) {
            return;
        }
        
        int slot = 20;
        for (int i = 0; i < Math.min(5, itemSet.getBonuses(). size()); i++) {
            ItemStack bonusDisplay = createBonusDisplay(itemSet.getBonuses().get(i), i + 1);
            inv.setItem(slot, bonusDisplay);
            slot += 2;
        }
    }
    
    /**
     * 세트 보너스 표시 생성
     */
    private ItemStack createBonusDisplay(Object bonus, int bonusLevel) {
        ItemStack bonusItem = new ItemStack(Material. ENCHANTED_BOOK);
        ItemMeta meta = bonusItem.getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName("§b" + bonusLevel + "세트 보너스");
            List<String> lore = new ArrayList<>();
            lore.add("§7" + bonus.toString());
            
            if (equippedCount >= bonusLevel) {
                lore.add("§a활성화됨");
            } else {
                lore.add("§c비활성화됨 (" + bonusLevel + "개 필요)");
            }
            
            meta.setLore(lore);
            bonusItem.setItemMeta(meta);
        }
        
        return bonusItem;
    }
    
    /**
     * 세트 아이템 목록 표시
     */
    private void displaySetItems(Inventory inv) {
        if (itemSet.getItems() == null || itemSet.getItems().isEmpty()) {
            return;
        }
        
        int slot = 35;
        for (int i = 0; i < Math.min(5, itemSet.getItems().size()); i++) {
            ItemStack itemDisplay = createSetItemDisplay(itemSet.getItems().get(i));
            inv.setItem(slot, itemDisplay);
            slot++;
        }
    }
    
    /**
     * 세트 아이템 표시 생성
     */
    private ItemStack createSetItemDisplay(Object item) {
        ItemStack itemDisplay = new ItemStack(Material. DIAMOND_SWORD);
        ItemMeta meta = itemDisplay.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b세트 아이템");
            List<String> lore = new ArrayList<>();
            lore.add("§7" + item.toString());
            meta.setLore(lore);
            itemDisplay.setItemMeta(meta);
        }
        
        return itemDisplay;
    }
    
    /**
     * 버튼 생성
     */
    private ItemStack createButton(Material material, String name, String...  lore) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button. getItemMeta();
        
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
     * 세트 정보 반환
     */
    public ItemSet getItemSet() {
        return itemSet;
    }
}