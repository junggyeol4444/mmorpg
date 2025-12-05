package com.multiverse.item.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org. bukkit.entity.Player;
import org.bukkit.inventory. ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import com.multiverse.item. utils.MessageUtil;

public class ItemUnequipListener implements Listener {
    
    private ItemCore plugin;
    
    public ItemUnequipListener(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어가 아이템을 해제할 때
     */
    @EventHandler
    public void onItemUnequip(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        // 이전에 들고 있던 아이템
        ItemStack oldItem = player.getInventory(). getItem(event.getOldSlot());
        
        if (oldItem == null || oldItem.getAmount() == 0) {
            return;
        }
        
        // CustomItem으로 변환 시도
        CustomItem customItem = null;
        try {
            customItem = plugin.getItemManager().fromItemStack(oldItem);
        } catch (Exception e) {
            // 일반 아이템이므로 무시
            return;
        }
        
        if (customItem == null) {
            return;
        }
        
        // 아이템 능력 제거
        removeItemStats(player, customItem);
        
        // 세트 보너스 확인 및 제거
        if (customItem. getSetId() != null && ! customItem.getSetId().isEmpty()) {
            plugin.getSetManager().deactivateSetBonus(player, customItem.getSetId());
            plugin.getSetManager().updatePlayerSetBonuses(player);
        }
        
        // 메시지 출력
        MessageUtil.sendMessage(player, "&c" + customItem.getName() + "&f을(를) 해제했습니다!");
    }
    
    /**
     * 아이템 스탯 제거
     */
    private void removeItemStats(Player player, CustomItem item) {
        // 플레이어에게서 스탯 제거 (나중에 스탯 시스템과 연동)
        // 현재는 메시지만 출력
        
        if (item. getBaseStats() != null) {
            StringBuilder stats = new StringBuilder("&7[");
            for (String statName : item.getBaseStats().keySet()) {
                double value = item.getBaseStats().get(statName);
                stats.append(statName).append(": -").append(String.format("%. 0f", value)).append(" ");
            }
            stats.append("]");
            
            plugin.getLogger().info("플레이어 " + player.getName() + "가 해제한 아이템 스탯: " + stats);
        }
    }
}