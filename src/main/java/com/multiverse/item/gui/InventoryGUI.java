package com.multiverse.item. gui;

import org.bukkit. Bukkit;
import org.bukkit.Material;
import org.bukkit. inventory.Inventory;
import org. bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import java.util.ArrayList;
import java.util.List;

public class InventoryGUI {
    
    private ItemCore plugin;
    private Player player;
    private Inventory inventory;
    private List<CustomItem> customItems;
    
    /**
     * 기본 생성자
     */
    public InventoryGUI(ItemCore plugin, Player player, List<CustomItem> customItems) {
        this.plugin = plugin;
        this.player = player;
        this.customItems = customItems;
        this.inventory = createGUI();
    }
    
    /**
     * GUI 인벤토리 생성
     */
    private Inventory createGUI() {
        Inventory inv = Bukkit.createInventory(null, 54, "§6커스텀 아이템 인벤토리");
        
        // 통계 정보 표시
        ItemStack statistics = createStatisticsDisplay();
        inv.setItem(10, statistics);
        
        // 커스텀 아이템 목록 표시
        displayCustomItems(inv);
        
        // 닫기 버튼
        ItemStack closeButton = createButton(Material.BARRIER, "§c닫기", "GUI를 닫습니다.");
        inv.setItem(49, closeButton);
        
        return inv;
    }
    
    /**
     * 통계 정보 표시
     */
    private ItemStack createStatisticsDisplay() {
        ItemStack statsItem = new ItemStack(Material.BOOK);
        ItemMeta meta = statsItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b통계");
            List<String> lore = new ArrayList<>();
            lore.add("§7총 커스텀 아이템: §a" + (customItems != null ? customItems. size() : 0));
            
            if (customItems != null) {
                int enhancedCount = 0;
                int sockettedCount = 0;
                int setItemCount = 0;
                
                for (CustomItem item : customItems) {
                    if (item.getEnhanceLevel() > 0) enhancedCount++;
                    if (item.getSockets() != null && ! item.getSockets().isEmpty()) sockettedCount++;
                    if (item.getSetId() != null && !item.getSetId().isEmpty()) setItemCount++;
                }
                
                lore.add("§7강화된 아이템: §a" + enhancedCount);
                lore.add("§7소켓된 아이템: §a" + sockettedCount);
                lore.add("§7세트 아이템: §a" + setItemCount);
            }
            
            meta. setLore(lore);
            statsItem.setItemMeta(meta);
        }
        
        return statsItem;
    }
    
    /**
     * 커스텀 아이템 목록 표시
     */
    private void displayCustomItems(Inventory inv) {
        if (customItems == null || customItems. isEmpty()) {
            return;
        }
        
        int slot = 11;
        for (CustomItem item : customItems) {
            if (slot >= 49) break;
            
            ItemStack itemDisplay = createCustomItemDisplay(item);
            inv.setItem(slot, itemDisplay);
            slot++;
        }
    }
    
    /**
     * 커스텀 아이템 표시 생성
     */
    private ItemStack createCustomItemDisplay(CustomItem item) {
        ItemStack display = new ItemStack(Material. DIAMOND_SWORD);
        ItemMeta meta = display.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b" + item.getName());
            List<String> lore = new ArrayList<>();
            
            lore.add("§7타입: " + item.getType());
            lore.add("§7희귀도: " + item.getRarity(). getKoreanName());
            
            if (item.getEnhanceLevel() > 0) {
                lore.add("§7강화: §a+" + item.getEnhanceLevel());
            }
            
            if (item.getOptions() != null && !item.getOptions().isEmpty()) {
                lore.add("§7옵션: " + item.getOptions(). size() + "개");
            }
            
            if (item.getSockets() != null && !item.getSockets().isEmpty()) {
                lore.add("§7소켓: " + item.getSockets().size() + "/3");
            }
            
            if (item.getSetId() != null && !item.getSetId().isEmpty()) {
                lore.add("§7세트: " + item.getSetId());
            }
            
            if (item.isSoulbound()) {
                lore. add("§c영혼결속");
            }
            
            lore.add("");
            lore.add("§e우클릭으로 자세히 보세요.");
            
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }
    
    /**
     * 버튼 생성
     */
    private ItemStack createButton(Material material, String name, String... lore) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button. getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> loreList = new ArrayList<>();
            for (String line : lore) {
                loreList. add(line);
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
     * 커스텀 아이템 목록 반환
     */
    public List<CustomItem> getCustomItems() {
        return customItems;
    }
}