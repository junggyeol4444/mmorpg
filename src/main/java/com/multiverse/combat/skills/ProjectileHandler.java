package com.multiverse.combat. skills;

import org.bukkit. entity.Player;
import org.bukkit. entity.Arrow;
import org.bukkit. entity.Entity;
import org.bukkit. util.Vector;
import org.bukkit.Location;
import com.multiverse.combat.CombatCore;
import com.multiverse. combat.models.Skill;

/**
 * 투사체 처리 클래스
 * 투사체 발사 및 충돌을 관리합니다.
 */
public class ProjectileHandler {
    
    private final CombatCore plugin;
    
    /**
     * ProjectileHandler 생성자
     * @param plugin CombatCore 플러그인 인스턴스
     */
    public ProjectileHandler(CombatCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 투사체 발사
     * @param player 플레이어
     * @param skill 스킬
     */
    public void launchProjectile(Player player, Skill skill) {
        if (skill.getSkillEffect() == null || ! skill.getSkillEffect().isProjectile()) {
            return;
        }
        
        Location startLocation = player.getEyeLocation();
        Vector direction = player.getLocation().getDirection();
        
        // Arrow 사용 (Bukkit 기본 투사체)
        Arrow arrow = player.getWorld().spawnArrow(startLocation, direction, 
            (float) skill.getSkillEffect(). getProjectileSpeed(), 0);
        
        // 메타데이터로 스킬 정보 저장
        arrow. setMetadata("skill_id", new org.bukkit.metadata. FixedMetadataValue(plugin, skill.getSkillId()));
        arrow.setMetadata("skill_level", new org.bukkit.metadata.FixedMetadataValue(plugin, 
            plugin.getSkillManager().getSkillLevel(player, skill.getSkillId())));
        arrow.setMetadata("caster_uuid", new org.bukkit.metadata. FixedMetadataValue(plugin, player.getUniqueId()));
        
        // 투사체 라이프타임
        org.bukkit. Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (arrow.isValid()) {
                arrow.remove();
            }
        }, 600L);  // 30초
    }
    
    /**
     * 범위 공격
     * @param player 플레이어
     * @param skill 스킬
     * @param center 중심 위치
     */
    public void dealAreaDamage(Player player, Skill skill, Location center) {
        if (skill.getSkillEffect() == null) {
            return;
        }
        
        double radius = skill.getSkillEffect(). getRadius();
        double damage = skill.getSkillEffect(). getBaseDamage();
        int skillLevel = plugin.getSkillManager().getSkillLevel(player, skill.getSkillId());
        
        // 범위 내 모든 엔티티 조회
        for (Entity entity : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof org.bukkit.entity.LivingEntity) {
                org.bukkit.entity.LivingEntity target = (org.bukkit.entity.LivingEntity) entity;
                
                // 자신 제외
                if (target. equals(player)) continue;
                
                // 데미지 계산
                double scaledDamage = damage * (1.0 + (skillLevel - 1) * 0. 1);
                double finalDamage = plugin.getDamageCalculator().getFinalDamage(player, target,
                    scaledDamage, skill.getSkillEffect().getDamageType(), true);
                
                // 데미지 적용
                target.damage(finalDamage);
                plugin.getCombatDataManager().addDamageDealt(player, finalDamage);
            }
        }
        
        player.sendMessage(plugin.getCombatConfig().getString("messages.prefix", "[전투] ") +
            "§a범위 공격을 시전했습니다!");
    }
    
    /**
     * 직선 공격
     * @param player 플레이어
     * @param skill 스킬
     */
    public void dealLineDamage(Player player, Skill skill) {
        if (skill.getSkillEffect() == null) {
            return;
        }
        
        Vector direction = player.getLocation().getDirection();
        double range = skill.getSkillEffect().getRange();
        double damage = skill. getSkillEffect().getBaseDamage();
        int skillLevel = plugin.getSkillManager(). getSkillLevel(player, skill.getSkillId());
        
        Location start = player.getEyeLocation();
        Location end = start.clone().add(direction.multiply(range));
        
        // 직선 경로 상의 엔티티 조회
        for (Entity entity : player.getWorld().getNearbyEntities(end, range / 2, range / 2, range / 2)) {
            if (entity instanceof org.bukkit.entity.LivingEntity) {
                org.bukkit. entity.LivingEntity target = (org.bukkit.entity. LivingEntity) entity;
                
                // 자신 제외
                if (target. equals(player)) continue;
                
                // 데미지 계산
                double scaledDamage = damage * (1. 0 + (skillLevel - 1) * 0.1);
                double finalDamage = plugin.getDamageCalculator(). getFinalDamage(player, target,
                    scaledDamage, skill.getSkillEffect().getDamageType(), true);
                
                // 데미지 적용
                target.damage(finalDamage);
                plugin.getCombatDataManager().addDamageDealt(player, finalDamage);
            }
        }
    }
}