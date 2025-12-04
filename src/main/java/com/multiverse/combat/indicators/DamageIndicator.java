package com.multiverse.combat.  indicators;

import org.bukkit.entity.ArmorStand;
import org. bukkit.  entity.LivingEntity;
import org.   bukkit.Location;
import org.bukkit.   Bukkit;
import com.multiverse.combat.CombatCore;

/**
 * 데미지 표시 클래스
 * 플레이어가 입힌 데미지를 공중에 표시합니다.  
 */
public class DamageIndicator {
    
    private final CombatCore plugin;
    
    /**
     * DamageIndicator 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public DamageIndicator(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 데미지 표시
     * @param target 대상
     * @param damage 데미지
     * @param isCritical 크리티컬 여부
     */
    public void showDamage(LivingEntity target, double damage, boolean isCritical) {
        Location loc = target.getLocation().  add(0, 2, 0);
        
        ArmorStand stand = target.getWorld().  spawn(loc, ArmorStand.class);
        stand.setCustomName(formatDamageText(damage, isCritical));
        stand.setCustomNameVisible(true);
        stand.setVisible(false);
        stand.  setGravity(false);
        stand.setSmall(true);
        
        // 상승 효과
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (!  stand.isDead()) {
                stand.teleport(stand.getLocation().add(0, 0.  05, 0));
            }
        }, 0L, 1L);
        
        // 제거
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, stand::remove, 40L);
    }
    
    /**
     * 데미지 텍스트 포맷팅
     * @param damage 데미지
     * @param isCritical 크리티컬 여부
     * @return 포맷된 텍스트
     */
    private String formatDamageText(double damage, boolean isCritical) {
        if (isCritical) {
            return "§c§l" + String.format("%.  0f", damage);
        } else {
            return "§f" + String.format("%. 0f", damage);
        }
    }
}