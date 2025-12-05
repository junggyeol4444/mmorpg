package com. multiverse.item. listeners;

import org.bukkit.event. EventHandler;
import org.bukkit.event. Listener;
import org.bukkit.event. player.PlayerDropItemEvent;
import org.bukkit.entity. Player;
import org.bukkit.inventory.ItemStack;
import com. multiverse.item.ItemCore;
import com.multiverse.item.  data.CustomItem;
import com.multiverse. item.utils.MessageUtil;

public class ItemDropListener implements Listener {
    
    private ItemCore plugin;
    
    public ItemDropListener(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어가 아이템을 드롭할 때
     */
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event. getItemDrop().getItemStack();
        
        if (itemStack == null || itemStack.getAmount() == 0) {
            return;
        }
        
        // CustomItem 확인
        CustomItem customItem = null;
        try {
            customItem = plugin.getItemManager().fromItemStack(itemStack);
        } catch (Exception e) {
            // 일반 아이템이므로 무시
            return;
        }
        
        if (customItem == null) {
            return;
        }
        
        // 드롭 가능 여부 확인
        if (!canDrop(player, customItem)) {
            event.setCancelled(true);
            return;
        }
        
        // 드롭 처리
        onCustomItemDrop(player, customItem);
    }
    
    /**
     * 드롭 가능 여부 확인
     */
    private boolean canDrop(Player player, CustomItem item) {
        // 바인딩된 아이템은 드롭 불가
        if (item.isSoulbound()) {
            MessageUtil.sendMessage(player, "&c이 아이템은 드롭할 수 없습니다!");
            return false;
        }
        
        // 관리자가 아니면 일부 아이템 드롭 제한 (선택사항)
        if (! player.hasPermission("multiverse.item.admin")) {
            // 특수 아이템 드롭 제한 로직 (나중에 구현)
        }
        
        return true;
    }
    
    /**
     * 커스텀 아이템 드롭 처리
     */
    private void onCustomItemDrop(Player player, CustomItem item) {
        // 아이템 정보 출력
        StringBuilder message = new StringBuilder();
        message.append("&c[").append(item.getRarity().getColor()).append(item. getRarity().getKoreanName());
        message.append("&c] ").append(item. getName()).append(" &f을(를) 드롭했습니다!");
        
        MessageUtil.sendMessage(player, message.toString());
        
        // 드롭 위치 표시
        plugin.getLogger().info(player. getName() + "가 " + item.getName() + 
                               "을(를) " + player.getLocation().getBlockX() + ", " + 
                               player. getLocation().getBlockY() + ", " + 
                               player. getLocation().getBlockZ() + "에 드롭했습니다.");
        
        // 아이템 거래 횟수 증가
        item.setTradeCount(item.getTradeCount() + 1);
        
        // 아이템 드롭 통계 기록
        recordDrop(player, item);
    }
    
    /**
     * 아이템 드롭 통계 기록
     */
    private void recordDrop(Player player, CustomItem item) {
        // 플레이어별 아이템 드롭 통계 (나중에 구현)
        // DataManager에 저장될 예정
        plugin.getLogger(). info(player.getName() + "가 " + item.getName() + "을(를) 드롭했습니다.");
    }
}