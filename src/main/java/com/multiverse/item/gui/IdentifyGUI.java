package com.multiverse.item. gui;

import org.bukkit. Bukkit;
import org.bukkit.Material;
import org.bukkit. inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit. inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import java.util.ArrayList;
import java.util.List;

public class IdentifyGUI {
    
    private ItemCore plugin;
    private Player player;
    private CustomItem item;
    private Inventory inventory;
    private int identifyCost;
    
    /**
     * 기본 생성자
     */
    public IdentifyGUI(ItemCore plugin, Player player, CustomItem item, int identifyCost) {
        this.plugin = plugin;
        this.player = player;
        this.item = item;
        this.identifyCost = identifyCost;
        this.inventory = createGUI();
    }
    
    /**
     * GUI 인벤토리 생성
     */
    private Inventory createGUI() {
        Inventory inv = Bukkit.createInventory(null, 27, "§6식별 시스템");
        
        // 식별 대상 아이템 표시
        ItemStack targetItem = createItemDisplay(item);
        inv.setItem(11, targetItem);
        
        // 식별 정보 표시
        ItemStack identifyInfo = createIdentifyInfoDisplay();
        inv.setItem(13, identifyInfo);
        
        // 숨겨진 옵션 표시
        ItemStack hiddenOptions = createHiddenOptionsDisplay();
        inv.setItem(15, hiddenOptions);
        
        // 식별 버튼
        ItemStack identifyButton = createButton(Material.AMETHYST_CLUSTER, "§a식별 실행", 
                                               "아이템을 식별하여 숨겨진 옵션을 공개합니다.");
        inv.setItem(22, identifyButton);
        
        // 닫기 버튼
        ItemStack closeButton = createButton(Material.BARRIER, "§c닫기", "GUI를 닫습니다.");
        inv.setItem(26, closeButton);
        
        return inv;
    }
    
    /**
     * 식별 대상 아이템 표시
     */
    private ItemStack createItemDisplay(CustomItem item) {
        ItemStack display = new ItemStack(Material. DIAMOND_SWORD);
        ItemMeta meta = display.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b식별 대상: " + item.getName());
            List<String> lore = new ArrayList<>();
            lore.add("§7희귀도: " + item.getRarity(). getKoreanName());
            lore.add("§7식별 상태: " + (item.isIdentified() ? "§a식별됨" : "§c미식별"));
            meta.setLore(lore);
            display.setItemMeta(meta);
        }
        
        return display;
    }
    
    /**
     * 식별 정보 표시
     */
    private ItemStack createIdentifyInfoDisplay() {
        ItemStack infoItem = new ItemStack(Material. BOOK);
        ItemMeta meta = infoItem.getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName("§b식별 정보");
            List<String> lore = new ArrayList<>();
            lore.add("§7비용: §c" + identifyCost + " Gold");
            
            if (! item.isIdentified()) {
                lore.add("§7상태: §c미식별");
                lore.add("§7우클릭으로 식별을 시작하세요.");
            } else {
                lore.add("§7상태: §a이미 식별됨");
            }
            
            meta. setLore(lore);
            infoItem.setItemMeta(meta);
        }
        
        return infoItem;
    }
    
    /**
     * 숨겨진 옵션 표시
     */
    private ItemStack createHiddenOptionsDisplay() {
        ItemStack optionItem = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = optionItem. getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b숨겨진 옵션");
            List<String> lore = new ArrayList<>();
            
            if (item.getOptions() != null && ! item.getOptions().isEmpty()) {
                lore.add("§7옵션 개수: §a" + item.getOptions().size());
                lore.add("§7식별 시 모든 옵션이 공개됩니다.");
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