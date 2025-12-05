package com.multiverse.item.   gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory. Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;
import com. multiverse.item.  data.CustomItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisassembleGUI {
    
    private ItemCore plugin;
    private Player player;
    private CustomItem item;
    private Inventory inventory;
    private Map<String, Integer> rewards;
    private int goldReward;
    
    /**
     * 기본 생성자
     */
    public DisassembleGUI(ItemCore plugin, Player player, CustomItem item, 
                         Map<String, Integer> rewards, int goldReward) {
        this.plugin = plugin;
        this.player = player;
        this.item = item;
        this.rewards = rewards;
        this.goldReward = goldReward;
        this.inventory = createGUI();
    }
    
    /**
     * GUI 인벤토리 생성
     */
    private Inventory createGUI() {
        Inventory inv = Bukkit.createInventory(null, 27, "§6분해 시스템");
        
        // 분해 대상 아이템 표시
        ItemStack targetItem = createItemDisplay(item);
        inv.setItem(11, targetItem);
        
        // 보상 정보 표시
        ItemStack rewardInfo = createRewardDisplay();
        inv.setItem(13, rewardInfo);
        
        // 재료 보상 표시
        displayMaterialRewards(inv);
        
        // 분해 버튼
        ItemStack disassembleButton = createButton(Material.IRON_AXE, "§a분해 실행", "아이템을 분해하여 재료를 획득합니다.");
        inv.setItem(22, disassembleButton);
        
        // 닫기 버튼
        ItemStack closeButton = createButton(Material.BARRIER, "§c닫기", "GUI를 닫습니다.");
        inv.setItem(26, closeButton);
        
        return inv;
    }
    
    /**
     * 분해 대상 아이템 표시
     */
    private ItemStack createItemDisplay(CustomItem item) {
        ItemStack display = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = display. getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b분해 대상: " + item.getName());
            List<String> lore = new ArrayList<>();
            lore.add("§7희귀도: " + item.getRarity().getKoreanName());
            lore.add("§7강화: +" + item.getEnhanceLevel());
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }
    
    /**
     * 보상 정보 표시
     */
    private ItemStack createRewardDisplay() {
        ItemStack rewardItem = new ItemStack(Material. GOLD_INGOT);
        ItemMeta meta = rewardItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b보상 정보");
            List<String> lore = new ArrayList<>();
            lore.add("§7골드 보상: §6" + goldReward);
            lore.add("§7재료 보상: §a" + (rewards != null ? rewards.size() : 0) + "종류");
            meta.setLore(lore);
            rewardItem.setItemMeta(meta);
        }
        
        return rewardItem;
    }
    
    /**
     * 재료 보상 표시
     */
    private void displayMaterialRewards(Inventory inv) {
        if (rewards == null || rewards.isEmpty()) {
            return;
        }
        
        int slot = 13;
        for (String material : rewards.keySet()) {
            if (slot > 17) break;
            
            ItemStack rewardDisplay = createMaterialRewardDisplay(material, rewards.get(material));
            inv.setItem(slot, rewardDisplay);
            slot++;
        }
    }
    
    /**
     * 재료 보상 표시 생성
     */
    private ItemStack createMaterialRewardDisplay(String materialName, int amount) {
        ItemStack display = new ItemStack(Material.EMERALD);
        ItemMeta meta = display.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b" + materialName);
            List<String> lore = new ArrayList<>();
            lore.add("§7수량: §a" + amount);
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
        player. openInventory(inventory);
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