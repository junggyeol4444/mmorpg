package com.multiverse.skill.listeners;

import com.multiverse. skill.SkillCore;
import com.multiverse.skill. managers.SkillManager;
import com.multiverse.skill. data.models. ProjectileConfig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit. event.entity.ProjectileHitEvent;

/**
 * 투사체 리스너
 */
public class ProjectileListener implements Listener {

    private final SkillCore plugin;
    private final SkillManager skillManager;

    public ProjectileListener(SkillCore plugin, SkillManager skillManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
    }

    /**
     * 투사체 명중 이벤트
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        
        if (projectile == null) {
            return;
        }

        // 발사자 확인
        if (!(projectile. getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) projectile.getShooter();

        // 투사체 메타데이터 확인
        ProjectileConfig config = getProjectileConfig(projectile);
        if (config == null) {
            return;
        }

        // 명중 대상 확인
        if (event.getHitEntity() != null) {
            LivingEntity hitEntity = event.getHitEntity();
            handleProjectileHit(shooter, hitEntity, projectile, config);
        } else if (event.getHitBlock() != null) {
            handleProjectileBlockHit(shooter, projectile, config);
        }

        // 투사체 제거
        projectile.remove();
    }

    /**
     * 투사체 엔티티 명중 처리
     */
    private void handleProjectileHit(Player shooter, LivingEntity hitEntity, 
                                     Projectile projectile, ProjectileConfig config) {
        // 자신에게 명중했는지 확인
        if (hitEntity. equals(shooter)) {
            return;
        }

        // 데미지 적용
        double damage = config. getDamage();
        hitEntity.damage(damage, shooter);

        // 투사체 효과 실행
        executeProjectileEffects(shooter, hitEntity, config);

        plugin.getLogger().info(shooter.getName() + "의 투사체가 " + hitEntity.getType() + "에 명중!");
    }

    /**
     * 투사체 블록 명중 처리
     */
    private void handleProjectileBlockHit(Player shooter, Projectile projectile, ProjectileConfig config) {
        // 블록 파괴 가능 여부 확인
        if (config.isCanBreakBlocks()) {
            // 블록 파괴 로직 (구현 필요)
        }

        plugin.getLogger().info(shooter. getName() + "의 투사체가 블록에 명중!");
    }

    /**
     * 투사체 효과 실행
     */
    private void executeProjectileEffects(Player shooter, LivingEntity target, ProjectileConfig config) {
        // 폭발 효과
        if (config.isExplosion()) {
            target.getWorld().createExplosion(target.getLocation(), config.getExplosionRadius());
        }

        // 관통 효과
        if (config.isPierce()) {
            // 관통 로직 (구현 필요)
        }

        // 추가 효과 (불, 중독 등)
        if (config. getEffectType() != null) {
            applyAdditionalEffects(target, config);
        }
    }

    /**
     * 추가 효과 적용
     */
    private void applyAdditionalEffects(LivingEntity target, ProjectileConfig config) {
        // 효과 타입별 처리
        switch (config.getEffectType()) {
            case "FIRE" -> {
                target.setFireTicks(100);
            }
            case "POISON" -> {
                // 중독 효과 (구현 필요)
            }
            case "SLOW" -> {
                // 느림 효과 (구현 필요)
            }
        }
    }

    /**
     * 투사체 설정 조회
     */
    private ProjectileConfig getProjectileConfig(Projectile projectile) {
        // 메타데이터에서 투사체 설정 추출
        // 실제 구현에서는 NBT 데이터나 PersistentDataContainer 사용
        Object data = projectile.getMetadata("skillProjectile");
        
        if (data != null && data instanceof ProjectileConfig) {
            return (ProjectileConfig) data;
        }

        return null;
    }
}