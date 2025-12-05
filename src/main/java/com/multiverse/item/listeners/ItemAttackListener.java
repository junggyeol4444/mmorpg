package com.multiverse.item. listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit. inventory.ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import com.multiverse.item. data.ItemOption;
import com.multiverse.item. data. OptionTrigger;

public class ItemAttackListener implements Listener {
    
    private ItemCore plugin;
    
    public ItemAttackListener(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어가 공격할 때
     */
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        // 공격자가 플레이어인지 확인
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player attacker = (Player) event.getDamager();
        Entity victim = event.getEntity();
        
        // 손에 들고 있는 아이템 확인
        ItemStack itemStack = attacker.getInventory().getItemInMainHand();
        
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
        
        // 공격 스탯 적용
        applyAttackStats(attacker, customItem, event);
        
        // 아이템 옵션 발동 (공격 시)
        triggerOptions(attacker, customItem, OptionTrigger.ON_ATTACK);
    }
    
    /**
     * 공격 스탯 적용
     */
    private void applyAttackStats(Player attacker, CustomItem item, EntityDamageByEntityEvent event) {
        double baseDamage = event.getDamage();
        double additionalDamage = 0;
        
        // 기본 스탯에서 공격력 추출
        if (item.getBaseStats() != null && item.getBaseStats().containsKey("damage")) {
            additionalDamage = item.getBaseStats().get("damage");
        }
        
        // 강화 레벨에 따른 피해 증가 (레벨당 5%)
        additionalDamage *= (1.0 + (item.getEnhanceLevel() * 0.05));
        
        // 최종 피해 계산
        double finalDamage = baseDamage + additionalDamage;
        event.setDamage(finalDamage);
    }
    
    /**
     * 공격 시 옵션 발동
     */
    private void triggerOptions(Player attacker, CustomItem item, OptionTrigger trigger) {
        if (item.getOptions() == null) {
            return;
        }
        
        for (ItemOption option : item.getOptions()) {
            // 해당 트리거의 옵션만 처리
            if (option.getTrigger() != trigger) {
                continue;
            }
            
            // 발동 확률 확인
            if (Math.random() * 100 > option.getValue()) {
                continue;
            }
            
            // 옵션 효과 적용
            applyOptionEffect(attacker, option);
        }
    }
    
    /**
     * 옵션 효과 적용
     */
    private void applyOptionEffect(Player player, ItemOption option) {
        switch (option.getType()) {
            case DAMAGE:
                // 추가 피해는 이미 기본 스탯에서 처리됨
                break;
            case CRITICAL_RATE:
                // 치명타율 증가 (파티클 이펙트 등으로 표현 가능)
                player.getWorld().playEffect(player.getLocation(), org.bukkit.Effect.MAGIC_CRIT, 1);
                break;
            case CRITICAL_DAMAGE:
                // 치명타 피해 증가
                break;
            case LIFESTEAL:
                // 생명력 흡수
                double healAmount = option.getValue();
                if (option.isPercentage()) {
                    // 퍼센티지 적용 (나중에 실제 피해량 기반으로 계산)
                }
                double newHealth = Math.min(player.getMaxHealth(), player.getHealth() + healAmount);
                player.setHealth(newHealth);
                break;
        }
    }
}