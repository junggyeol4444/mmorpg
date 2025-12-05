package com.multiverse.item. gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory. Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;
import com. multiverse.item.data.CustomItem;
import java.util.ArrayList;
import java.util.List;

public class ItemInfoGUI {
    
    private ItemCore plugin;
    private Player player;
    private CustomItem item;
    private Inventory inventory;
    
    /**
     * 기본 생성자
     */
    public ItemInfoGUI(ItemCore plugin, Player player, CustomItem item) {
        this.plugin = plugin;
        this.player = player;
        this.item = item;
        this.inventory = createGUI();
    }
    
    /**
     * GUI 인벤토리 생성
     */
    private Inventory createGUI() {
        Inventory inv = Bukkit. createInventory(null, 54, "§6아이템 정보");
        
        // 아이템 정보 표시
        ItemStack basicInfo = createBasicInfoDisplay();
        inv.setItem(11, basicInfo);
        
        // 기본 스탯 표시
        ItemStack baseStats = createBaseStatsDisplay();
        inv.setItem(13, baseStats);
        
        // 강화 정보 표시
        ItemStack enhanceInfo = createEnhanceInfoDisplay();
        inv.setItem(15, enhanceInfo);
        
        // 옵션 정보 표시 (최대 3개)
        displayOptions(inv);
        
        // 세트 정보 표시
        if (item.getSetId() != null && ! item.getSetId().isEmpty()) {
            ItemStack setInfo = createSetInfoDisplay();
            inv.setItem(35, setInfo);
        }
        
        // 소켓 정보 표시
        ItemStack socketInfo = createSocketInfoDisplay();
        inv.setItem(37, socketInfo);
        
        // 내구도 정보 표시
        ItemStack durabilityInfo = createDurabilityInfoDisplay();
        inv.setItem(39, durabilityInfo);
        
        // 거래 정보 표시
        ItemStack tradeInfo = createTradeInfoDisplay();
        inv.setItem(41, tradeInfo);
        
        // 닫기 버튼
        ItemStack closeButton = createButton(Material.BARRIER, "§c닫기", "GUI를 닫습니다.");
        inv.setItem(49, closeButton);
        
        return inv;
    }
    
    /**
     * 기본 정보 표시
     */
    private ItemStack createBasicInfoDisplay() {
        ItemStack display = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = display.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b" + item.getName());
            List<String> lore = new ArrayList<>();
            lore.add("§7타입: " + item.getType());
            lore.add("§7희귀도: " + item. getRarity().getKoreanName());
            lore.add("§7필요 레벨: " + item.getRequiredLevel());
            if (item.isSoulbound()) {
                lore. add("§c거래 불가 (영혼결속)");
            }
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }
    
    /**
     * 기본 스탯 표시
     */
    private ItemStack createBaseStatsDisplay() {
        ItemStack statsItem = new ItemStack(Material. GOLDEN_APPLE);
        ItemMeta meta = statsItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b기본 스탯");
            List<String> lore = new ArrayList<>();
            
            if (item.getBaseStats() != null && ! item.getBaseStats().isEmpty()) {
                for (String stat : item.getBaseStats(). keySet()) {
                    double value = item.getBaseStats(). get(stat);
                    lore.add("§7" + stat + ": §a" + String.format("%.0f", value));
                }
            } else {
                lore.add("§7스탯: §c없음");
            }
            
            meta.setLore(lore);
            statsItem.setItemMeta(meta);
        }
        
        return statsItem;
    }
    
    /**
     * 강화 정보 표시
     */
    private ItemStack createEnhanceInfoDisplay() {
        ItemStack enhanceItem = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = enhanceItem.getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName("§b강화 정보");
            List<String> lore = new ArrayList<>();
            lore.add("§7강화 레벨: +§a" + item.getEnhanceLevel());
            lore. add("§7최대 강화: +15");
            meta.setLore(lore);
            enhanceItem.setItemMeta(meta);
        }
        
        return enhanceItem;
    }
    
    /**
     * 옵션 표시
     */
    private void displayOptions(Inventory inv) {
        if (item.getOptions() == null || item.getOptions().isEmpty()) {
            return;
        }
        
        int slot = 20;
        for (int i = 0; i < Math.min(3, item.getOptions().size()); i++) {
            ItemStack optionDisplay = createOptionDisplay(item.getOptions().get(i));
            inv.setItem(slot, optionDisplay);
            slot += 2;
        }
    }
    
    /**
     * 단일 옵션 표시
     */
    private ItemStack createOptionDisplay(Object option) {
        ItemStack optionItem = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = optionItem. getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b옵션");
            List<String> lore = new ArrayList<>();
            lore.add("§7" + option.toString());
            meta.setLore(lore);
            optionItem.setItemMeta(meta);
        }
        
        return optionItem;
    }
    
    /**
     * 세트 정보 표시
     */
    private ItemStack createSetInfoDisplay() {
        ItemStack setItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = setItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b세트 정보");
            List<String> lore = new ArrayList<>();
            lore.add("§7세트: " + item.getSetId());
            meta.setLore(lore);
            setItem. setItemMeta(meta);
        }
        
        return setItem;
    }
    
    /**
     * 소켓 정보 표시
     */
    private ItemStack createSocketInfoDisplay() {
        ItemStack socketItem = new ItemStack(Material.AMETHYST_CLUSTER);
        ItemMeta meta = socketItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b소켓 정보");
            List<String> lore = new ArrayList<>();
            int socketCount = item.getSockets() != null ? item.getSockets().size() : 0;
            lore.add("§7소켓: " + socketCount + "/3");
            meta.setLore(lore);
            socketItem.setItemMeta(meta);
        }
        
        return socketItem;
    }
    
    /**
     * 내구도 정보 표시
     */
    private ItemStack createDurabilityInfoDisplay() {
        ItemStack durabilityItem = new ItemStack(Material.DIAMOND);
        ItemMeta meta = durabilityItem.getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName("§b내구도");
            List<String> lore = new ArrayList<>();
            if (item.isUnbreakable()) {
                lore.add("§a영구적");
            } else {
                lore.add("§7내구도: §a" + item.getDurability());
            }
            meta.setLore(lore);
            durabilityItem. setItemMeta(meta);
        }
        
        return durabilityItem;
    }
    
    /**
     * 거래 정보 표시
     */
    private ItemStack createTradeInfoDisplay() {
        ItemStack tradeItem = new ItemStack(Material.EMERALD);
        ItemMeta meta = tradeItem.getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName("§b거래 정보");
            List<String> lore = new ArrayList<>();
            lore.add("§7거래 횟수: " + item.getTradeCount());
            if (item.isSoulbound()) {
                lore.add("§c거래 불가");
            }
            meta.setLore(lore);
            tradeItem.setItemMeta(meta);
        }
        
        return tradeItem;
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