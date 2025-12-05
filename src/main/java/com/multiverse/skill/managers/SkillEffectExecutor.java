package com.multiverse.skill.managers;

import com.multiverse.skill. SkillCore;
import com.multiverse. skill.data.enums.EffectType;
import com.multiverse.skill.data.enums.ProjectileType;
import com.multiverse.skill.data.models.*;
import com.multiverse.skill.utils.DamageCalculator;
import com.multiverse.skill.utils. ParticleUtils;
import com. multiverse.skill.utils. TargetSelector;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.*;

public class SkillEffectExecutor {

    private final SkillCore plugin;
    private final SkillManager skillManager;
    private final Map<UUID, Map<String, Long>> dotTimers = new HashMap<>();

    public SkillEffectExecutor(SkillCore plugin, SkillManager skillManager) {
        this.plugin = plugin;
        this.skillManager = skillManager;
    }

    /**
     * 단일 엔티티를 대상으로 효과 실행
     */
    public void execute(Player caster, Skill skill, LivingEntity target) {
        if (target == null || !target.isValid()) {
            return;
        }

        for (SkillEffect effect : skill.getEffects()) {
            executeEffect(caster, target, effect);
        }

        // 스킬 통계 업데이트
        plugin.getLearningManager().updateSkillStats(caster, skill. getSkillId(), 0);
    }

    /**
     * 위치를 기준으로 효과 실행
     */
    public void execute(Player caster, Skill skill, Location targetLocation) {
        if (targetLocation == null) {
            return;
        }

        // 범위 내 모든 엔티티 선택
        List<LivingEntity> targets = TargetSelector.getEntitiesInRange(
            targetLocation,
            skill.getDefaultEffect().getRange(),
            skill.getDefaultEffect().getMaxTargets()
        );

        for (LivingEntity target : targets) {
            if (target. equals(caster)) continue; // 자신 제외

            for (SkillEffect effect : skill.getEffects()) {
                executeEffect(caster, target, effect);
            }
        }

        plugin.getLearningManager().updateSkillStats(caster, skill. getSkillId(), 0);
    }

    /**
     * 개별 효과 실행
     */
    private void executeEffect(Player caster, LivingEntity target, SkillEffect effect) {
        EffectType effectType = effect.getType();

        switch (effectType) {
            case DAMAGE -> executeDamage(caster, target, effect);
            case HEAL -> executeHeal(caster, target, effect);
            case PROJECTILE -> executeProjectile(caster, effect);
            case SUMMON -> executeSummon(caster, effect);
            case DEBUFF -> executeDebuff(target, effect);
            case BUFF -> executeBuff(caster, effect);
            case TELEPORT -> executeTeleport(caster, target);
            case DOT -> applyDoT(target, effect, caster);
            case STUN -> executeStun(target, effect);
            case KNOCKBACK -> executeKnockback(target, caster, effect);
        }
    }

    /**
     * 데미지 효과 실행
     */
    private void executeDamage(Player caster, LivingEntity target, SkillEffect effect) {
        if (!(target instanceof LivingEntity)) {
            return;
        }

        double damage = DamageCalculator.calculateDamage(
            caster,
            effect.getBaseValue(),
            effect.getScaling(),
            effect.getDamageType()
        );

        target.damage(damage, caster);

        // 파티클 효과
        ParticleUtils.spawnDamageParticles(target. getLocation(), effect.getDamageType());

        // 추가 효과
        if (effect.getAdditionalEffects() != null) {
            for (SkillEffect additionalEffect : effect.getAdditionalEffects()) {
                executeEffect(caster, target, additionalEffect);
            }
        }
    }

    /**
     * 힐 효과 실행
     */
    private void executeHeal(Player caster, LivingEntity target, SkillEffect effect) {
        if (!(target instanceof LivingEntity)) {
            return;
        }

        double healAmount = DamageCalculator. calculateHeal(
            caster,
            effect.getBaseValue(),
            effect.getScaling()
        );

        double newHealth = Math.min(target.getHealth() + healAmount, target.getMaxHealth());
        target.setHealth(newHealth);

        // 파티클 효과
        ParticleUtils. spawnHealParticles(target.getLocation());

        // 메시지
        if (target instanceof Player) {
            ((Player) target).sendMessage("§a+" + (int) healAmount + " HP");
        }
    }

    /**
     * 투사체 효과 실행
     */
    private void executeProjectile(Player caster, SkillEffect effect) {
        ProjectileConfig projectileConfig = effect.getProjectileConfig();
        if (projectileConfig == null) {
            return;
        }

        Location spawnLocation = caster.getEyeLocation();
        Vector direction = spawnLocation.getDirection(). multiply(projectileConfig.getSpeed() / 10.0);

        // 투사체 생성
        Projectile projectile = switch (projectileConfig.getType()) {
            case ARROW -> caster.launchProjectile(Arrow.class);
            case FIREBALL -> caster.launchProjectile(Fireball.class);
            case SNOWBALL -> caster.launchProjectile(Snowball.class);
            default -> caster.launchProjectile(Arrow.class);
        };

        projectile.setVelocity(direction);
        projectile.setMetadata("skill", new org.bukkit.metadata. FixedMetadataValue(plugin, effect.getEffectId()));
    }

    /**
     * 소환 효과 실행
     */
    private void executeSummon(Player caster, SkillEffect effect) {
        SummonConfig summonConfig = effect.getSummonConfig();
        if (summonConfig == null) {
            return;
        }

        Location spawnLocation = caster.getLocation(). add(0, 1, 0);

        // MythicMob이 있으면 사용, 없으면 일반 엔티티로 대체
        EntityType entityType = EntityType. ZOMBIE; // 예시

        try {
            entityType = EntityType.valueOf(summonConfig.getEntityType().toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Unknown entity type: " + summonConfig.getEntityType());
        }

        LivingEntity summoned = (LivingEntity) spawnLocation.getWorld().spawnEntity(spawnLocation, entityType);
        
        // 소환수 설정
        summoned. setCustomName("§c" + caster.getName() + "의 소환수");
        summoned.setCustomNameVisible(true);
        summoned. setMetadata("summon_owner", new org.bukkit.metadata. FixedMetadataValue(plugin, caster.getUniqueId()));
        summoned.setMetadata("summon_ai", new org.bukkit.metadata.FixedMetadataValue(plugin, summonConfig.getAi(). name()));

        // 지속 시간 설정
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, summoned::remove, summonConfig.getDuration() * 20L);
    }

    /**
     * 버프 효과 실행
     */
    private void executeBuff(Player player, SkillEffect effect) {
        if (effect.getBuffEffects() == null) {
            return;
        }

        for (Map.Entry<String, Object> buffEntry : effect.getBuffEffects().entrySet()) {
            String buffType = buffEntry.getKey();
            Object value = buffEntry.getValue();

            if (buffType.equals("strength")) {
                int amplifier = ((Number) value).intValue();
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.STRENGTH,
                    300,
                    amplifier - 1,
                    false,
                    false
                ));
            }
        }
    }

    /**
     * 디버프 효과 실행
     */
    private void executeDebuff(LivingEntity target, SkillEffect effect) {
        if (effect.getDebuffEffects() == null) {
            return;
        }

        for (Map.Entry<String, Object> debuffEntry : effect.getDebuffEffects().entrySet()) {
            String debuffType = debuffEntry.getKey();
            Object value = debuffEntry.getValue();

            if (debuffType.equals("poison")) {
                int duration = ((Number) value).intValue();
                target.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.POISON,
                    duration * 20,
                    0,
                    false,
                    false
                ));
            }
        }
    }

    /**
     * 텔레포트 효과
     */
    private void executeTeleport(Player caster, LivingEntity target) {
        Vector direction = caster.getLocation().getDirection().multiply(5);
        Location teleportLocation = target.getLocation().add(direction);
        teleportLocation.setY(teleportLocation.getWorld().getHighestBlockYAt(teleportLocation) + 1);
        
        caster.teleport(teleportLocation);
    }

    /**
     * DoT (Damage over Time) 적용
     */
    public void applyDoT(LivingEntity target, SkillEffect effect, Player caster) {
        if (! dotTimers.containsKey(target.getUniqueId())) {
            dotTimers.put(target.getUniqueId(), new HashMap<>());
        }

        Map<String, Long> targetDoTs = dotTimers.get(target.getUniqueId());
        targetDoTs.put(effect.getEffectId(), System.currentTimeMillis());

        int duration = effect.getDuration();
        int tickInterval = effect.getTickInterval();
        int totalTicks = (duration * 1000) / tickInterval;

        for (int i = 0; i < totalTicks; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (target.isValid()) {
                    tickDoT(target, effect, caster);
                }
            }, (long) i * tickInterval / 50);
        }
    }

    /**
     * DoT 틱 처리
     */
    public void tickDoT(LivingEntity target, SkillEffect effect, Player caster) {
        double damage = DamageCalculator.calculateDamage(
            caster,
            effect.getBaseValue() * 0.2,
            effect.getScaling(),
            effect.getDamageType()
        );

        target.damage(damage, caster);
        ParticleUtils.spawnDamageParticles(target.getLocation(), effect.getDamageType());
    }

    /**
     * 스턴 효과
     */
    private void executeStun(LivingEntity target, SkillEffect effect) {
        if (target instanceof Player) {
            Player player = (Player) target;
            player.addPotionEffect(new org.bukkit.potion. PotionEffect(
                org.bukkit.potion.PotionEffectType.SLOWNESS,
                effect.getDuration() * 20,
                100,
                false,
                false
            ));
        }
    }

    /**
     * 넉백 효과
     */
    private void executeKnockback(LivingEntity target, Player caster, SkillEffect effect) {
        Vector knockbackVector = caster.getLocation().toVector()
            .subtract(target.getLocation().toVector())
            .normalize()
            .multiply(2);
        knockbackVector. setY(0. 5);

        target.setVelocity(knockbackVector);
    }
}