package com.multiverse.item.  listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.  Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.  bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import com.multiverse.item.ItemCore;
import com.multiverse.item. data.CustomItem;
import com.multiverse.  item.utils.MessageUtil;

public class PlayerDeathListener implements Listener {
    
    private ItemCore plugin;
    
    public PlayerDeathListener(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어가 사망할 때
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        
        // 소울바운드 아이템 보호
        protectSoulboundItems(victim);
        
        // 아이템 드롭 처리
        handleItemDrop(victim, event);
        
        // 사망 통계 기록
        recordDeath(victim);
    }
    
    /**
     * 소울바운드 아이템 보호
     */
    private void protectSoulboundItems(Player victim) {
        PlayerInventory inventory = victim.getInventory();
        java.util.List<ItemStack> dropsToRemove = new java.util.ArrayList<>();
        
        // 인벤토리 확인
        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getAmount() == 0) {
                continue;
            }
            
            CustomItem customItem = null;
            try {
                customItem = plugin.getItemManager(). fromItemStack(item);
            } catch (Exception e) {
                continue;
            }
            
            // 소울바운드 아이템은 드롭되지 않음
            if (customItem != null && customItem. isSoulbound()) {
                dropsToRemove.add(item);
            }
        }
        
        // 갑옷 확인
        ItemStack[] armorContents = inventory.getArmorContents();
        for (ItemStack armor : armorContents) {
            if (armor == null || armor.getAmount() == 0) {
                continue;
            }
            
            CustomItem customItem = null;
            try {
                customItem = plugin.getItemManager().fromItemStack(armor);
            } catch (Exception e) {
                continue;
            }
            
            if (customItem != null && customItem.isSoulbound()) {
                dropsToRemove.add(armor);
            }
        }
    }
    
    /**
     * 아이템 드롭 처리
     */
    private void handleItemDrop(Player victim, PlayerDeathEvent event) {
        PlayerInventory inventory = victim.getInventory();
        
        // 드롭된 아이템 처리
        for (ItemStack item : event.getDrops()) {
            if (item == null || item. getAmount() == 0) {
                continue;
            }
            
            CustomItem customItem = null;
            try {
                customItem = plugin.getItemManager().fromItemStack(item);
            } catch (Exception e) {
                continue;
            }
            
            if (customItem == null) {
                continue;
            }
            
            // 강화 아이템 드롭 알림
            if (customItem. getEnhanceLevel() > 0) {
                victim.getWorld().dropItem(victim.getLocation(), item);
                MessageUtil.broadcast("&c[사망] " + victim.getName() + "가 강화된 아이템을 드롭했습니다!");
            }
            
            // 거래 횟수 증가
            customItem.setTradeCount(customItem.getTradeCount() + 1);
        }
    }
    
    /**
     * 사망 통계 기록
     */
    private void recordDeath(Player victim) {
        // 플레이어별 사망 통계 (나중에 구현)
        plugin.getLogger().info(victim. getName() + "이(가) 사망했습니다.");
    }
}