package com. multiverse.item.gui;

import org.bukkit.event. EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event. inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.multiverse.item.ItemCore;
import com.multiverse. item.utils.MessageUtil;

public class GUIListener implements Listener {
    
    private ItemCore plugin;
    
    public GUIListener(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 인벤토리 클릭 이벤트
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        String inventoryName = event.getInventory().getName();
        
        if (clickedItem == null || clickedItem.getItemMeta() == null) {
            return;
        }
        
        String itemName = clickedItem.getItemMeta().getDisplayName();
        
        // GUI 유형별 처리
        if (inventoryName.contains("강화")) {
            handleEnhanceGUIClick(player, event, itemName);
        } else if (inventoryName.contains("소켓")) {
            handleSocketGUIClick(player, event, itemName);
        } else if (inventoryName.contains("분해")) {
            handleDisassembleGUIClick(player, event, itemName);
        } else if (inventoryName.contains("식별")) {
            handleIdentifyGUIClick(player, event, itemName);
        } else if (inventoryName.contains("리롤")) {
            handleRerollGUIClick(player, event, itemName);
        } else if (inventoryName.contains("보석")) {
            handleGemSelectionGUIClick(player, event, itemName);
        } else if (inventoryName.contains("세트")) {
            handleSetInfoGUIClick(player, event, itemName);
        }
        
        // 닫기 버튼 처리
        if (itemName.contains("닫기")) {
            event.setCancelled(true);
            player.closeInventory();
        }
    }
    
    /**
     * 강화 GUI 클릭 처리
     */
    private void handleEnhanceGUIClick(Player player, InventoryClickEvent event, String itemName) {
        event.setCancelled(true);
        
        if (itemName.contains("강화 실행")) {
            MessageUtil.sendMessage(player, "&a강화를 시작합니다!");
            // 강화 로직 실행
            plugin.getEnhanceManager().enhance(player);
        }
    }
    
    /**
     * 소켓 GUI 클릭 처리
     */
    private void handleSocketGUIClick(Player player, InventoryClickEvent event, String itemName) {
        event. setCancelled(true);
        
        if (itemName.contains("보석 선택")) {
            MessageUtil. sendMessage(player, "&a보석을 선택하세요.");
            // 보석 선택 GUI 오픈
        }
    }
    
    /**
     * 분해 GUI 클릭 처리
     */
    private void handleDisassembleGUIClick(Player player, InventoryClickEvent event, String itemName) {
        event.setCancelled(true);
        
        if (itemName.contains("분해 실행")) {
            MessageUtil. sendMessage(player, "&a분해를 시작합니다!");
            // 분해 로직 실행
            plugin. getDisassembleSystem().disassemble(player);
        }
    }
    
    /**
     * 식별 GUI 클릭 처리
     */
    private void handleIdentifyGUIClick(Player player, InventoryClickEvent event, String itemName) {
        event.setCancelled(true);
        
        if (itemName.contains("식별 실행")) {
            MessageUtil.sendMessage(player, "&a식별을 시작합니다!");
            // 식별 로직 실행
            plugin. getIdentifySystem().identify(player);
        }
    }
    
    /**
     * 리롤 GUI 클릭 처리
     */
    private void handleRerollGUIClick(Player player, InventoryClickEvent event, String itemName) {
        event.setCancelled(true);
        
        if (itemName.contains("리롤 실행")) {
            MessageUtil.sendMessage(player, "&a리롤을 시작합니다!");
            // 리롤 로직 실행
            plugin.getItemOptionManager().rerollOptions(player);
        }
    }
    
    /**
     * 보석 선택 GUI 클릭 처리
     */
    private void handleGemSelectionGUIClick(Player player, InventoryClickEvent event, String itemName) {
        event.setCancelled(true);
        
        if (! itemName.contains("닫기")) {
            MessageUtil.sendMessage(player, "&a보석이 선택되었습니다: " + itemName);
            // 보석 설치 로직 실행
            plugin.getGemManager().insertGem(player, itemName);
        }
    }
    
    /**
     * 세트 정보 GUI 클릭 처리
     */
    private void handleSetInfoGUIClick(Player player, InventoryClickEvent event, String itemName) {
        event.setCancelled(true);
        
        if (!itemName.contains("닫기")) {
            MessageUtil.sendMessage(player, "&a세트 정보를 확인하세요.");
        }
    }
    
    /**
     * 인벤토리 닫기 이벤트
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        
        // GUI 닫을 때 필요한 처리
        plugin.getLogger().info(player.getName() + "이(가) GUI를 닫았습니다.");
    }
}