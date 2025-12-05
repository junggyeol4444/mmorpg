package com.multiverse.item.  listeners;

import org.bukkit. event.EventHandler;
import org.bukkit.event.  Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse.item. data.CustomItem;
import com.multiverse.  item.utils.MessageUtil;

public class ItemPickupListener implements Listener {
    
    private ItemCore plugin;
    
    public ItemPickupListener(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어가 아이템을 습득할 때
     */
    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();
        
        if (itemStack == null || itemStack.getAmount() == 0) {
            return;
        }
        
        // CustomItem 확인
        CustomItem customItem = null;
        try {
            customItem = plugin. getItemManager().fromItemStack(itemStack);
        } catch (Exception e) {
            // 일반 아이템이므로 무시
            return;
        }
        
        if (customItem == null) {
            return;
        }
        
        // 아이템 습득 가능 여부 확인
        if (!canPickup(player, customItem)) {
            event.setCancelled(true);
            return;
        }
        
        // 아이템 습득 처리
        onCustomItemPickup(player, customItem, itemStack);
    }
    
    /**
     * 아이템 습득 가능 여부 확인
     */
    private boolean canPickup(Player player, CustomItem item) {
        // 인벤토리 공간 확인
        if (! hasInventorySpace(player)) {
            MessageUtil.sendMessage(player, "&c인벤토리가 가득 찼습니다!");
            return false;
        }
        
        // 요구사항 확인
        if (player.getLevel() < item.getRequiredLevel()) {
            MessageUtil.sendMessage(player, "&c필요 레벨: " + item.getRequiredLevel());
            return false;
        }
        
        // 클래스 요구사항 확인 (선택사항)
        if (item.getRequiredClass() != null && !item.getRequiredClass().isEmpty()) {
            // 플레이어의 클래스 데이터 확인 (나중에 구현)
        }
        
        // 종족 요구사항 확인 (선택사항)
        if (item.getRequiredRace() != null && !item. getRequiredRace().isEmpty()) {
            // 플레이어의 종족 데이터 확인 (나중에 구현)
        }
        
        return true;
    }
    
    /**
     * 인벤토리 공간 확인
     */
    private boolean hasInventorySpace(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack item : contents) {
            if (item == null || item.getAmount() == 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 커스텀 아이템 습득 처리
     */
    private void onCustomItemPickup(Player player, CustomItem item, ItemStack itemStack) {
        // 아이템 정보 출력
        StringBuilder message = new StringBuilder();
        message. append("&a[").append(item.getRarity(). getColor()).append(item.getRarity().getKoreanName());
        message.append("&a] ").append(item.getName()). append(" &f을(를) 획득했습니다!");
        
        MessageUtil.sendMessage(player, message.toString());
        
        // 아이템 정보 표시 (채팅)
        if (item.getEnhanceLevel() > 0) {
            MessageUtil.sendMessage(player, "&7강화: +" + item.getEnhanceLevel());
        }
        
        if (item.getOptions() != null && ! item.getOptions().isEmpty()) {
            MessageUtil.sendMessage(player, "&7옵션: " + item.getOptions(). size() + "개");
        }
        
        // 거래 불가 아이템 확인
        if (item.isSoulbound()) {
            MessageUtil.sendMessage(player, "&c이 아이템은 거래할 수 없습니다.");
        }
        
        // 아이템 습득 통계 증가
        recordPickup(player, item);
    }
    
    /**
     * 아이템 습득 통계 기록
     */
    private void recordPickup(Player player, CustomItem item) {
        // 플레이어별 아이템 습득 통계 (나중에 구현)
        // DataManager에 저장될 예정
        plugin.getLogger().info(player.getName() + "가 " + item.getName() + "을(를) 습득했습니다.");
    }
}