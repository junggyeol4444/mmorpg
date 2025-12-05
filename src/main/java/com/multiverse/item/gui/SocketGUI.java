package com.multiverse.item.  gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory. Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import com.multiverse.item.ItemCore;
import com.multiverse.item.  data.CustomItem;
import java.util.ArrayList;
import java.util.List;

public class SocketGUI {
    
    private ItemCore plugin;
    private Player player;
    private CustomItem item;
    private Inventory inventory;
    
    /**
     * 기본 생성자
     */
    public SocketGUI(ItemCore plugin, Player player, CustomItem item) {
        this.plugin = plugin;
        this.player = player;
        this.item = item;
        this.inventory = createGUI();
    }
    
    /**
     * GUI 인벤토리 생성
     */
    private Inventory createGUI() {
        Inventory inv = Bukkit.createInventory(null, 27, "§6소켓 시스템");
        
        // 아이템 표시
        ItemStack targetItem = createItemDisplay(item);
        inv.setItem(11, targetItem);
        
        // 소켓 슬롯 표시 (최대 3개)
        displaySockets(inv);
        
        // 보석 선택 버튼
        ItemStack selectGemButton = createButton(Material.DIAMOND, "§a보석 선택", "보석을 선택하여 소켓에 끼웁니다.");
        inv. setItem(22, selectGemButton);
        
        // 닫기 버튼
        ItemStack closeButton = createButton(Material.BARRIER, "§c닫기", "GUI를 닫습니다.");
        inv.setItem(26, closeButton);
        
        return inv;
    }
    
    /**
     * 소켓 슬롯 표시
     */
    private void displaySockets(Inventory inv) {
        int[] socketSlots = {13, 14, 15};
        
        for (int i = 0; i < 3; i++) {
            ItemStack socketDisplay = createSocketDisplay(i);
            inv.setItem(socketSlots[i], socketDisplay);
        }
    }
    
    /**
     * 소켓 표시 생성
     */
    private ItemStack createSocketDisplay(int socketIndex) {
        ItemStack socketItem = new ItemStack(Material.  GLASS);
        ItemMeta meta = socketItem.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§b소켓 #" + (socketIndex + 1));
            List<String> lore = new ArrayList<>();
            
            if (item.getSockets() != null && socketIndex < item.getSockets().size()) {
                lore.add("§a설치됨: " + item.getSockets().get(socketIndex).getName());
            } else {
                lore.add("§c비어있음");
                lore.add("§7우클릭으로 보석을 설치하세요.");
            }
            
            meta.setLore(lore);
            socketItem.setItemMeta(meta);
        }
        
        return socketItem;
    }
    
    /**
     * 아이템 표시 생성
     */
    private ItemStack createItemDisplay(CustomItem item) {
        ItemStack display = new ItemStack(Material. DIAMOND_SWORD);
        ItemMeta meta = display.getItemMeta();
        
        if (meta != null) {
            meta. setDisplayName("§b" + item.getName());
            List<String> lore = new ArrayList<>();
            lore.add("§7희귀도: " + item.getRarity(). getKoreanName());
            lore.add("§7소켓: " + (item.getSockets() != null ?  item.getSockets().size() : 0) + "/3");
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