package com.multiverse.item. gui;

import org.bukkit. Bukkit;
import org.bukkit.Material;
import org.bukkit. inventory. Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data. Gem;
import java.util. ArrayList;
import java.util. List;

public class GemSelectionGUI {
    
    private ItemCore plugin;
    private Player player;
    private Inventory inventory;
    private List<Gem> availableGems;
    
    /**
     * 기본 생성자
     */
    public GemSelectionGUI(ItemCore plugin, Player player, List<Gem> availableGems) {
        this.plugin = plugin;
        this.player = player;
        this.availableGems = availableGems;
        this.inventory = createGUI();
    }
    
    /**
     * GUI 인벤토리 생성
     */
    private Inventory createGUI() {
        Inventory inv = Bukkit.createInventory(null, 54, "§6보석 선택");
        
        // 보석 목록 표시
        displayGems(inv);
        
        // 닫기 버튼
        ItemStack closeButton = createButton(Material.BARRIER, "§c닫기", "GUI를 닫습니다.");
        inv.setItem(49, closeButton);
        
        return inv;
    }
    
    /**
     * 보석 목록 표시
     */
    private void displayGems(Inventory inv) {
        if (availableGems == null || availableGems.isEmpty()) {
            return;
        }
        
        int slot = 0;
        for (Gem gem : availableGems) {
            if (slot >= 45) break;
            
            ItemStack gemDisplay = createGemDisplay(gem);
            inv.setItem(slot, gemDisplay);
            slot++;
        }
    }
    
    /**
     * 보석 표시 생성
     */
    private ItemStack createGemDisplay(Gem gem) {
        ItemStack gemItem = new ItemStack(Material. AMETHYST_CLUSTER);
        ItemMeta meta = gemItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b" + gem.getName());
            List<String> lore = new ArrayList<>();
            lore.add("§7타입: " + gem.getType());
            lore.add("§7희귀도: " + gem. getRarity());
            
            // 보석 효과 표시
            if (gem.getEffect() != null) {
                lore.add("§7효과: " + gem.getEffect());
            }
            
            lore.add("");
            lore.add("§e우클릭으로 선택하세요.");
            
            meta.setLore(lore);
            gemItem.setItemMeta(meta);
        }
        
        return gemItem;
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
     * 사용 가능한 보석 목록 반환
     */
    public List<Gem> getAvailableGems() {
        return availableGems;
    }
}