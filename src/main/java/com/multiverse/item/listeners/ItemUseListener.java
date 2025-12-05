package com.multiverse.item. listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event. Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit. event.block.Action;
import org.bukkit. entity.Player;
import org.bukkit.inventory.ItemStack;
import com.multiverse. item.ItemCore;
import com.multiverse.item.data. CustomItem;
import com.multiverse.item.data.ItemType;
import com.multiverse. item.utils.MessageUtil;

public class ItemUseListener implements Listener {
    
    private ItemCore plugin;
    
    public ItemUseListener(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어가 아이템을 사용할 때
     */
    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        // 블록 상호작용은 무시
        if (event.getClickedBlock() != null) {
            return;
        }
        
        // 오른쪽 클릭만 처리
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action. RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        
        if (itemStack == null || itemStack.getAmount() == 0) {
            return;
        }
        
        // CustomItem으로 변환 시도
        CustomItem customItem = null;
        try {
            customItem = plugin.getItemManager().fromItemStack(itemStack);
        } catch (Exception e) {
            return;
        }
        
        if (customItem == null) {
            return;
        }
        
        // 소비 아이템만 처리
        if (customItem.getType() != ItemType.POTION && customItem.getType() != ItemType.SCROLL) {
            return;
        }
        
        // 아이템 사용 처리
        handleItemUse(player, customItem, itemStack);
        
        event.setCancelled(true);
    }
    
    /**
     * 아이템 사용 처리
     */
    private void handleItemUse(Player player, CustomItem item, ItemStack itemStack) {
        switch (item.getType()) {
            case POTION:
                usePotionItem(player, item);
                break;
            case SCROLL:
                useScrollItem(player, item);
                break;
        }
        
        // 아이템 개수 감소
        itemStack.setAmount(itemStack. getAmount() - 1);
    }
    
    /**
     * 포션 아이템 사용
     */
    private void usePotionItem(Player player, CustomItem item) {
        MessageUtil.sendMessage(player, "&a" + item.getName() + "&f을(를) 사용했습니다!");
        
        // 포션 효과 적용
        if (item.getBaseStats() != null) {
            for (String statName : item.getBaseStats().keySet()) {
                double value = item.getBaseStats().get(statName);
                
                // 스탯에 따른 효과 적용
                switch (statName) {
                    case "health":
                        double newHealth = Math.min(player.getMaxHealth(), player.getHealth() + value);
                        player.setHealth(newHealth);
                        break;
                    case "mana":
                        // 마나 시스템 추가 시 구현
                        break;
                }
            }
        }
    }
    
    /**
     * 스크롤 아이템 사용
     */
    private void useScrollItem(Player player, CustomItem item) {
        MessageUtil. sendMessage(player, "&a" + item.getName() + "&f을(를) 사용했습니다!");
        
        // 스크롤 효과는 특수 효과에 따라 다르게 처리
        if (item.getOptions() != null && ! item.getOptions().isEmpty()) {
            for (int i = 0; i < item.getOptions().size(); i++) {
                plugin.getLogger().info("스크롤 효과 " + (i + 1) + ": " + item.getOptions(). get(i). getName());
            }
        }
    }
}