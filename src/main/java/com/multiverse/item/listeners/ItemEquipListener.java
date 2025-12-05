package com.multiverse.item. listeners;

import org.bukkit. event.EventHandler;
import org. bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit. NamespacedKey;
import org. bukkit.persistence.PersistentDataType;
import com.multiverse.item.ItemCore;
import com.multiverse.item. data.CustomItem;
import com.multiverse.item. utils.MessageUtil;

public class ItemEquipListener implements Listener {
    
    private ItemCore plugin;
    
    public ItemEquipListener(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어가 아이템을 장착할 때
     */
    @EventHandler
    public void onItemEquip(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        // 새로 선택한 아이템
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        
        if (newItem == null || newItem.getAmount() == 0) {
            return;
        }
        
        // CustomItem으로 변환 시도
        CustomItem customItem = null;
        try {
            customItem = plugin.getItemManager().fromItemStack(newItem);
        } catch (Exception e) {
            // 일반 아이템이므로 무시
            return;
        }
        
        if (customItem == null) {
            return;
        }
        
        // 요구사항 확인
        if (! checkRequirements(player, customItem)) {
            // 요구사항 미달시 아이템 장착 불가
            event.setCancelled(true);
            player.getInventory().setHeldItemSlot(event.getOldSlot());
            MessageUtil.sendMessage(player, "&c이 아이템을 장착할 수 없습니다!");
            return;
        }
        
        // 아이템 능력 적용
        applyItemStats(player, customItem);
        
        // 세트 보너스 확인 및 적용
        if (customItem.getSetId() != null && ! customItem.getSetId().isEmpty()) {
            plugin.getSetManager().updatePlayerSetBonuses(player);
        }
        
        // 메시지 출력
        MessageUtil.sendMessage(player, "&a" + customItem.getName() + "&f을(를) 장착했습니다!");
    }
    
    /**
     * 아이템 요구사항 확인
     */
    private boolean checkRequirements(Player player, CustomItem item) {
        // 레벨 확인
        if (player.getLevel() < item.getRequiredLevel()) {
            MessageUtil.sendMessage(player, "&c필요 레벨: " + item.getRequiredLevel());
            return false;
        }
        
        // 클래스 확인 (선택사항)
        if (item.getRequiredClass() != null && !item.getRequiredClass().isEmpty()) {
            // 플레이어의 클래스 데이터가 있다면 확인
            // 현재는 구현 예정
        }
        
        // 종족 확인 (선택사항)
        if (item. getRequiredRace() != null && !item. getRequiredRace().isEmpty()) {
            // 플레이어의 종족 데이터가 있다면 확인
            // 현재는 구현 예정
        }
        
        return true;
    }
    
    /**
     * 아이템 스탯 적용
     */
    private void applyItemStats(Player player, CustomItem item) {
        // 플레이어에게 스탯 적용 (나중에 스탯 시스템과 연동)
        // 현재는 메시지만 출력
        
        if (item.getBaseStats() != null) {
            StringBuilder stats = new StringBuilder("&7[");
            for (String statName : item.getBaseStats(). keySet()) {
                double value = item.getBaseStats(). get(statName);
                stats.append(statName).append(": ").append(String.format("%.0f", value)).append(" ");
            }
            stats. append("]");
            
            plugin.getLogger().info("플레이어 " + player.getName() + "가 장착한 아이템 스탯: " + stats);
        }
    }
}