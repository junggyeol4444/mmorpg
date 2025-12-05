package com.multiverse. item.listeners;

import org. bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org. bukkit.event.entity.EntityDamageEvent;
import org.bukkit. event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit. inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import com.multiverse.item.ItemCore;
import com.multiverse.item. data.CustomItem;
import com.multiverse.item.data. ItemOption;
import com.multiverse.item.data.OptionTrigger;

public class ItemDamageListener implements Listener {
    
    private ItemCore plugin;
    
    public ItemDamageListener(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 플레이어가 피해를 입을 때
     */
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player victim = (Player) event.getEntity();
        double damage = event.getDamage();
        
        // 방어 아이템 확인 (갑옷)
        applyDefenseStats(victim, event);
        
        // 피해 옵션 발동
        triggerDamageOptions(victim, OptionTrigger.ON_DAMAGED);
    }
    
    /**
     * 방어 스탯 적용
     */
    private void applyDefenseStats(Player victim, EntityDamageEvent event) {
        PlayerInventory inventory = victim.getInventory();
        double totalDefense = 0;
        double damageReduction = 1.0; // 기본값: 피해 감소 없음
        
        // 착용 중인 방어 아이템 확인
        ItemStack[] armorContents = inventory.getArmorContents();
        
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null || armorPiece.getAmount() == 0) {
                continue;
            }
            
            CustomItem customItem = null;
            try {
                customItem = plugin.getItemManager(). fromItemStack(armorPiece);
            } catch (Exception e) {
                continue;
            }
            
            if (customItem == null) {
                continue;
            }
            
            // 방어력 스탯 추출
            if (customItem.getBaseStats() != null && customItem.getBaseStats().containsKey("defense")) {
                totalDefense += customItem.getBaseStats().get("defense");
            }
            
            // 강화 레벨에 따른 방어력 증가 (레벨당 3%)
            totalDefense *= (1.0 + (customItem.getEnhanceLevel() * 0.03));
        }
        
        // 방어력을 피해 감소로 변환 (방어력 10당 1% 감소)
        damageReduction = Math. max(0. 1, 1.0 - (totalDefense / 1000.0));
        
        // 최종 피해 계산
        double finalDamage = event.getDamage() * damageReduction;
        event.setDamage(finalDamage);
        
        // 내구도 감소
        reduceDurability(victim, armorContents);
    }
    
    /**
     * 방어 옵션 발동
     */
    private void triggerDamageOptions(Player victim, OptionTrigger trigger) {
        PlayerInventory inventory = victim.getInventory();
        ItemStack[] armorContents = inventory.getArmorContents();
        
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null || armorPiece.getAmount() == 0) {
                continue;
            }
            
            CustomItem customItem = null;
            try {
                customItem = plugin.getItemManager().fromItemStack(armorPiece);
            } catch (Exception e) {
                continue;
            }
            
            if (customItem == null || customItem.getOptions() == null) {
                continue;
            }
            
            for (ItemOption option : customItem. getOptions()) {
                if (option.getTrigger() != trigger) {
                    continue;
                }
                
                if (Math.random() * 100 > option.getValue()) {
                    continue;
                }
                
                applyOptionEffect(victim, option);
            }
        }
    }
    
    /**
     * 내구도 감소
     */
    private void reduceDurability(Player player, ItemStack[] armorContents) {
        for (ItemStack armorPiece : armorContents) {
            if (armorPiece == null) {
                continue;
            }
            
            CustomItem customItem = null;
            try {
                customItem = plugin.getItemManager().fromItemStack(armorPiece);
            } catch (Exception e) {
                continue;
            }
            
            if (customItem == null || customItem.isUnbreakable()) {
                continue;
            }
            
            // 내구도 1 감소
            customItem.setDurability(customItem.getDurability() - 1);
            
            // 내구도가 0이 되면 아이템 제거
            if (customItem.getDurability() <= 0) {
                player.getInventory().remove(armorPiece);
            }
        }
    }
    
    /**
     * 옵션 효과 적용
     */
    private void applyOptionEffect(Player player, ItemOption option) {
        switch (option.getType()) {
            case DEFENSE:
                // 이미 피해 감소에서 처리됨
                break;
            case RESISTANCE:
                // 모든 저항 증가
                break;
            case REFLECT:
                // 피해 반사 (나중에 구현)
                break;
        }
    }
}