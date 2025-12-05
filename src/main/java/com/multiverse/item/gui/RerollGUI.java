package com.multiverse.item. gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory. Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;
import com. multiverse.item.data.CustomItem;
import java.util. ArrayList;
import java.util. List;

public class RerollGUI {
    
    private ItemCore plugin;
    private Player player;
    private CustomItem item;
    private Inventory inventory;
    private int rerollCost;
    private int rerollCount;
    
    /**
     * 기본 생성자
     */
    public RerollGUI(ItemCore plugin, Player player, CustomItem item, int rerollCost, int rerollCount) {
        this.plugin = plugin;
        this.player = player;
        this.item = item;
        this.rerollCost = rerollCost;
        this.rerollCount = rerollCount;
        this. inventory = createGUI();
    }
    
    /**
     * GUI 인벤토리 생성
     */
    private Inventory createGUI() {
        Inventory inv = Bukkit.createInventory(null, 27, "§6옵션 리롤 시스템");
        
        // 리롤 대상 아이템 표시
        ItemStack targetItem = createItemDisplay(item);
        inv. setItem(11, targetItem);
        
        // 리롤 정보 표시
        ItemStack rerollInfo = createRerollInfoDisplay();
        inv.setItem(13, rerollInfo);
        
        // 현재 옵션 표시
        ItemStack currentOptions = createCurrentOptionsDisplay();
        inv.setItem(15, currentOptions);
        
        // 리롤 버튼
        ItemStack rerollButton = createButton(Material. RECOVERY_COMPASS, "§a리롤 실행", 
                                             "아이템의 옵션을 다시 뽑습니다.");
        inv.setItem(22, rerollButton);
        
        // 닫기 버튼
        ItemStack closeButton = createButton(Material.BARRIER, "§c닫기", "GUI를 닫습니다.");
        inv.setItem(26, closeButton);
        
        return inv;
    }
    
    /**
     * 리롤 대상 아이템 표시
     */
    private ItemStack createItemDisplay(CustomItem item) {
        ItemStack display = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = display.getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName("§b리롤 대상: " + item.getName());
            List<String> lore = new ArrayList<>();
            lore.add("§7희귀도: " + item.getRarity().getKoreanName());
            lore.add("§7리롤 횟수: " + rerollCount);
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }
    
    /**
     * 리롤 정보 표시
     */
    private ItemStack createRerollInfoDisplay() {
        ItemStack infoItem = new ItemStack(Material. BOOK);
        ItemMeta meta = infoItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b리롤 정보");
            List<String> lore = new ArrayList<>();
            lore.add("§7기본 비용: §c" + rerollCost + " Gold");
            lore.add("§7총 비용: §c" + (rerollCost * rerollCount) + " Gold");
            lore.add("§7회차당 비용: §c" + rerollCost + " Gold");
            meta.setLore(lore);
            infoItem.setItemMeta(meta);
        }
        
        return infoItem;
    }
    
    /**
     * 현재 옵션 표시
     */
    private ItemStack createCurrentOptionsDisplay() {
        ItemStack optionItem = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = optionItem.getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName("§b현재 옵션");
            List<String> lore = new ArrayList<>();
            
            if (item.getOptions() != null && ! item.getOptions().isEmpty()) {
                lore.add("§7옵션 개수: §a" + item.getOptions().size());
                int count = 0;
                for (int i = 0; i < item.getOptions().size() && count < 3; i++) {
                    lore.add("  §7- " + item.getOptions().get(i). getName());
                    count++;
                }
                if (item.getOptions().size() > 3) {
                    lore.add("  §7... 외 " + (item.getOptions().size() - 3) + "개");
                }
            } else {
                lore.add("§7옵션: §c없음");
            }
            
            meta.setLore(lore);
            optionItem.setItemMeta(meta);
        }
        
        return optionItem;
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
            button. setItemMeta(meta);
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