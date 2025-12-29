package com.multiverse.pet.listener;

import com. multiverse.pet. PetCore;
import com.multiverse. pet.api.event.PetDeathEvent;
import com.multiverse. pet.entity.PetEntity;
import com.multiverse. pet.model.Pet;
import com. multiverse.pet. model.PetBehavior;
import com.multiverse.pet.util.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Entity;
import org. bukkit.entity. LivingEntity;
import org.bukkit.entity.Monster;
import org. bukkit.entity. Player;
import org. bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. entity.EntityDamageByEntityEvent;
import org.bukkit. event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org. bukkit.event. entity.ProjectileHitEvent;

import java.util.UUID;

/**
 * 펫 전투 관련 리스너
 * 펫 데미지 처리, 전투 보조, 킬 카운트 등
 */
public class PetCombatListener implements Listener {

    private final PetCore plugin;

    public PetCombatListener(PetCore plugin) {
        this.plugin = plugin;
    }

    // ===== 펫이 데미지를 받을 때 =====

    /**
     * 펫이 데미지를 받을 때 처리
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPetDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (! plugin.getPetEntityManager().isPetEntity(entity)) {
            return;
        }

        PetEntity petEntity = plugin.getPetEntityManager().getPetEntityByEntity(entity);
        if (petEntity == null) return;

        Pet pet = petEntity. getPet();
        Player owner = petEntity.getOwner();

        // 환경 데미지 처리
        switch (event.getCause()) {
            case FALL:
                // 낙하 데미지 무시 또는 감소
                if (plugin.getConfigManager().getPetSettings().isIgnoreFallDamage()) {
                    event.setCancelled(true);
                    return;
                }
                event.setDamage(event.getDamage() * 0.5);
                break;

            case DROWNING:
                // 익사 데미지 처리
                if (pet.hasAbility("water_breathing")) {
                    event.setCancelled(true);
                    return;
                }
                break;

            case FIRE:
            case FIRE_TICK: 
            case LAVA:
                // 화염 데미지 처리
                if (pet.hasAbility("fire_immunity")) {
                    event.setCancelled(true);
                    return;
                }
                break;

            case VOID:
                // 보이드 데미지 - 즉시 해제
                plugin.getPetManager().unsummonPet(owner, pet. getPetId());
                MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("pet.void-despawn")
                        .replace("{name}", pet.getPetName()));
                event.setCancelled(true);
                return;

            case SUFFOCATION:
                // 질식 데미지 - 텔레포트
                petEntity.teleportToOwner();
                event.setCancelled(true);
                return;

            default:
                break;
        }

        // 데미지 적용
        double finalDamage = event.getFinalDamage();
        
        // 최소 데미지 보장
        if (finalDamage < 0.5) {
            event.setCancelled(true);
            return;
        }

        // 펫 체력 감소
        petEntity.takeDamage(finalDamage, null);

        // 버킷 엔티티 데미지는 취소 (펫 시스템에서 관리)
        event.setDamage(0);
    }

    /**
     * 펫이 엔티티에게 데미지를 받을 때
     */
    @EventHandler(priority = EventPriority. HIGH, ignoreCancelled = true)
    public void onPetDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (!plugin.getPetEntityManager().isPetEntity(entity)) {
            return;
        }

        PetEntity petEntity = plugin.getPetEntityManager().getPetEntityByEntity(entity);
        if (petEntity == null) return;

        Pet pet = petEntity. getPet();
        Player owner = petEntity.getOwner();

        // 실제 공격자 확인 (투사체인 경우)
        LivingEntity actualDamager = getActualDamager(damager);

        // 주인에게 맞으면 무시
        if (actualDamager != null && actualDamager.equals(owner)) {
            event.setCancelled(true);
            return;
        }

        // 같은 주인의 펫에게 맞으면 무시
        if (actualDamager != null && plugin.getPetEntityManager().isPetEntity(actualDamager)) {
            PetEntity attackerPet = plugin. getPetEntityManager().getPetEntityByEntity(actualDamager);
            if (attackerPet != null && attackerPet.getOwnerPlayerId().equals(petEntity.getOwnerPlayerId())) {
                event.setCancelled(true);
                return;
            }
        }

        // 데미지 계산
        double baseDamage = event.getDamage();
        double defense = pet.getTotalStat("defense");
        double reduction = defense / (defense + 100);
        double finalDamage = baseDamage * (1 - reduction);

        // 데미지 적용
        petEntity.takeDamage(finalDamage, actualDamager);

        // 버킷 데미지 취소
        event.setDamage(0);

        // 반격 설정 (공격적 모드)
        if (pet.getBehavior() == PetBehavior. AGGRESSIVE || pet.getBehavior() == PetBehavior.DEFENSIVE) {
            if (actualDamager != null && ! actualDamager. equals(owner)) {
                petEntity.setAttackTarget(actualDamager);
            }
        }

        // 주인에게 알림 (중요한 데미지일 때)
        if (finalDamage > pet.getMaxHealth() * 0.2) {
            MessageUtil.sendMessage(owner, plugin.getConfigManager().getMessage("pet.took-damage")
                    . replace("{name}", pet.getPetName())
                    .replace("{damage}", String. format("%.1f", finalDamage)));
        }
    }

    // ===== 펫이 데미지를 줄 때 =====

    /**
     * 펫이 엔티티에게 데미지를 줄 때
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPetAttack(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity target = event. getEntity();

        // 투사체 처리
        LivingEntity actualDamager = getActualDamager(damager);
        if (actualDamager == null) return;

        if (! plugin.getPetEntityManager().isPetEntity(actualDamager)) {
            return;
        }

        PetEntity petEntity = plugin.getPetEntityManager().getPetEntityByEntity(actualDamager);
        if (petEntity == null) return;

        Pet pet = petEntity.getPet();
        Player owner = petEntity.getOwner();

        // 주인 공격 방지
        if (target.equals(owner)) {
            event. setCancelled(true);
            return;
        }

        // 같은 주인의 펫 공격 방지
        if (plugin.getPetEntityManager().isPetEntity(target)) {
            PetEntity targetPet = plugin. getPetEntityManager().getPetEntityByEntity(target);
            if (targetPet != null && targetPet.getOwnerPlayerId().equals(petEntity.getOwnerPlayerId())) {
                event. setCancelled(true);
                return;
            }
        }

        // 다른 플레이어 공격 방지 (PvP 설정에 따라)
        if (target instanceof Player && !plugin.getConfigManager().getPetSettings().isAllowPvP()) {
            event.setCancelled(true);
            return;
        }

        // 데미지 계산
        double baseDamage = pet.getTotalStat("attack");
        
        // 치명타 확인
        double critChance = pet.getTotalStat("critical_chance");
        boolean isCrit = Math.random() * 100 < critChance;
        
        if (isCrit) {
            double critDamage = pet.getTotalStat("critical_damage");
            baseDamage *= (1 + critDamage / 100);
        }

        // 타입 보너스 적용
        if (target instanceof Monster && pet.getType() != null) {
            baseDamage *= pet.getType().getCombatMultiplier();
        }

        // 최종 데미지 설정
        event.setDamage(baseDamage);

        // 치명타 이펙트
        if (isCrit && target instanceof LivingEntity) {
            ((LivingEntity) target).getWorld().spawnParticle(
                    org.bukkit. Particle.CRIT,
                    ((LivingEntity) target).getEyeLocation(),
                    10, 0.2, 0.2, 0.2, 0.1
            );
        }
    }

    // ===== 주인 전투 보조 =====

    /**
     * 주인이 공격받을 때 펫 반응
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOwnerDamaged(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player owner = (Player) event.getEntity();
        UUID playerId = owner. getUniqueId();

        // 활성 펫 확인
        if (!plugin.getPetManager().hasActivePet(playerId)) {
            return;
        }

        LivingEntity attacker = getActualDamager(event.getDamager());
        if (attacker == null || attacker.equals(owner)) {
            return;
        }

        // 펫이 공격자를 타겟으로 설정 (방어적/공격적 모드)
        for (PetEntity petEntity : plugin.getPetManager().getActivePets(playerId)) {
            Pet pet = petEntity.getPet();
            
            if (pet.getBehavior() == PetBehavior. DEFENSIVE || 
                pet.getBehavior() == PetBehavior. AGGRESSIVE) {
                
                // 펫이 아닌 경우만 타겟
                if (!plugin.getPetEntityManager().isPetEntity(attacker)) {
                    petEntity.setAttackTarget(attacker);
                }
            }
        }
    }

    /**
     * 주인이 공격할 때 펫 보조
     */
    @EventHandler(priority = EventPriority. MONITOR, ignoreCancelled = true)
    public void onOwnerAttack(EntityDamageByEntityEvent event) {
        LivingEntity attacker = getActualDamager(event.getDamager());
        
        if (!(attacker instanceof Player)) {
            return;
        }

        Player owner = (Player) attacker;
        UUID playerId = owner.getUniqueId();
        Entity target = event.getEntity();

        if (!(target instanceof LivingEntity)) {
            return;
        }

        // 활성 펫 확인
        if (!plugin.getPetManager().hasActivePet(playerId)) {
            return;
        }

        // 공격적 모드 펫이 같이 공격
        for (PetEntity petEntity : plugin.getPetManager().getActivePets(playerId)) {
            Pet pet = petEntity. getPet();
            
            if (pet.getBehavior() == PetBehavior.AGGRESSIVE) {
                // 펫이 아닌 경우만 타겟
                if (!plugin.getPetEntityManager().isPetEntity(target)) {
                    petEntity.setAttackTarget((LivingEntity) target);
                }
            }
        }
    }

    // ===== 킬 처리 =====

    /**
     * 엔티티 사망 시 펫 킬 카운트
     */
    @EventHandler(priority = EventPriority. MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        
        // 펫이 죽은 경우
        if (plugin.getPetEntityManager().isPetEntity(entity)) {
            handlePetDeath(entity);
            return;
        }

        // 펫이 죽인 경우
        Player killer = entity.getKiller();
        if (killer == null) {
            // 마지막 데미지가 펫인지 확인
            EntityDamageEvent lastDamage = entity.getLastDamageCause();
            if (lastDamage instanceof EntityDamageByEntityEvent) {
                Entity damager = ((EntityDamageByEntityEvent) lastDamage).getDamager();
                LivingEntity actualDamager = getActualDamager(damager);
                
                if (actualDamager != null && plugin.getPetEntityManager().isPetEntity(actualDamager)) {
                    PetEntity petEntity = plugin.getPetEntityManager().getPetEntityByEntity(actualDamager);
                    if (petEntity != null) {
                        handlePetKill(petEntity, entity);
                    }
                }
            }
        }
    }

    /**
     * 펫 킬 처리
     */
    private void handlePetKill(PetEntity petEntity, LivingEntity victim) {
        Pet pet = petEntity. getPet();
        Player owner = petEntity.getOwner();

        // 킬 카운트 증가
        pet.incrementKillCount();

        // 경험치 획득
        int expAmount = calculateExpFromKill(victim);
        if (expAmount > 0) {
            plugin.getPetLevelManager().addExperience(pet, expAmount, owner);
        }

        // 저장
        if (owner != null) {
            plugin.getPetManager().savePetData(owner. getUniqueId(), pet);
        }

        // 타겟 해제
        petEntity.clearAttackTarget();
    }

    /**
     * 펫 사망 처리
     */
    private void handlePetDeath(LivingEntity entity) {
        PetEntity petEntity = plugin.getPetEntityManager().getPetEntityByEntity(entity);
        if (petEntity == null) return;

        Pet pet = petEntity.getPet();
        Player owner = petEntity.getOwner();

        // 드롭 제거
        // (EntityDeathEvent에서 처리됨)

        // 이벤트 발생
        if (owner != null) {
            PetDeathEvent event = new PetDeathEvent(owner, pet, entity. getLastDamageCause());
            Bukkit.getPluginManager().callEvent(event);
        }

        // 디스폰은 PetEntity. onFaint()에서 처리됨
    }

    /**
     * 킬에서 경험치 계산
     */
    private int calculateExpFromKill(LivingEntity victim) {
        double baseExp = 10;

        // 몬스터 타입에 따른 경험치
        if (victim instanceof Monster) {
            baseExp = 20;
            
            // 체력에 비례
            baseExp += victim.getMaxHealth() * 0.5;
        }

        // 랜덤 변동
        baseExp *= (0.8 + Math.random() * 0.4);

        return (int) baseExp;
    }

    /**
     * 실제 공격자 확인 (투사체 발사자 포함)
     */
    private LivingEntity getActualDamager(Entity damager) {
        if (damager instanceof LivingEntity) {
            return (LivingEntity) damager;
        }

        if (damager instanceof Projectile) {
            Projectile projectile = (Projectile) damager;
            if (projectile. getShooter() instanceof LivingEntity) {
                return (LivingEntity) projectile.getShooter();
            }
        }

        return null;
    }

    /**
     * 투사체 히트 시 처리
     */
    @EventHandler(priority = EventPriority. MONITOR, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (!(projectile.getShooter() instanceof LivingEntity)) {
            return;
        }

        LivingEntity shooter = (LivingEntity) projectile.getShooter();

        // 펫이 발사한 투사체
        if (plugin. getPetEntityManager().isPetEntity(shooter)) {
            // 특별한 처리 필요 시 여기에 추가
        }
    }
}