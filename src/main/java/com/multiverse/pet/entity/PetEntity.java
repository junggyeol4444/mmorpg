package com.multiverse.pet. entity;

import com.multiverse.pet.PetCore;
import com. multiverse.pet. model.Pet;
import com. multiverse.pet. model.PetBehavior;
import com.multiverse.pet.model.PetStatus;
import org.bukkit.Location;
import org. bukkit. Particle;
import org. bukkit.entity. Entity;
import org.bukkit.entity.EntityType;
import org. bukkit.entity. LivingEntity;
import org.bukkit.entity.Player;
import org. bukkit.entity. Tameable;
import org.bukkit.metadata.FixedMetadataValue;
import org. bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util. UUID;

/**
 * 펫 엔티티 래퍼 클래스
 * 버킷 엔티티와 펫 데이터를 연결
 */
public class PetEntity {

    private final PetCore plugin;
    private final Pet pet;
    private final UUID ownerPlayerId;

    // 버킷 엔티티
    private LivingEntity entity;
    private UUID entityUUID;

    // AI 시스템
    private PetAI petAI;

    // 상태
    private boolean isSpawned;
    private boolean isFollowing;
    private boolean isAttacking;
    private boolean isSitting;

    // 타겟
    private LivingEntity attackTarget;
    private Location moveTarget;

    // 마지막 업데이트 시간
    private long lastUpdate;
    private long lastDamageTime;

    // 메타데이터 키
    public static final String METADATA_KEY = "MultiVerse_Pet";
    public static final String PET_ID_KEY = "pet_id";
    public static final String OWNER_ID_KEY = "owner_id";

    /**
     * 생성자
     */
    public PetEntity(PetCore plugin, Pet pet, UUID ownerPlayerId) {
        this.plugin = plugin;
        this.pet = pet;
        this.ownerPlayerId = ownerPlayerId;
        this.isSpawned = false;
        this.isFollowing = true;
        this. isAttacking = false;
        this. isSitting = false;
        this. lastUpdate = System. currentTimeMillis();
    }

    // ===== 스폰/디스폰 =====

    /**
     * 엔티티 스폰
     */
    public boolean spawn(Location location) {
        if (isSpawned && entity != null && entity.isValid()) {
            return false;
        }

        try {
            // 엔티티 생성
            EntityType entityType = pet.getEntityType();
            if (entityType == null) {
                entityType = EntityType. WOLF; // 기본값
            }

            Entity spawned = location.getWorld().spawnEntity(location, entityType);
            if (!(spawned instanceof LivingEntity)) {
                spawned. remove();
                return false;
            }

            entity = (LivingEntity) spawned;
            entityUUID = entity.getUniqueId();

            // 엔티티 설정
            setupEntity();

            // AI 초기화
            petAI = new PetAI(plugin, this);
            petAI. start();

            isSpawned = true;
            pet.setStatus(PetStatus. ACTIVE);

            // 스폰 이펙트
            playSpawnEffect();

            return true;

        } catch (Exception e) {
            plugin.getLogger().warning("펫 스폰 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 엔티티 디스폰
     */
    public void despawn() {
        if (!isSpawned || entity == null) {
            return;
        }

        // AI 중지
        if (petAI != null) {
            petAI.stop();
            petAI = null;
        }

        // 디스폰 이펙트
        playDespawnEffect();

        // 엔티티 제거
        if (entity. isValid()) {
            entity.remove();
        }

        entity = null;
        entityUUID = null;
        isSpawned = false;
        isFollowing = false;
        isAttacking = false;
        attackTarget = null;
    }

    /**
     * 엔티티 설정
     */
    private void setupEntity() {
        // 메타데이터 설정
        entity.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, true));
        entity.setMetadata(PET_ID_KEY, new FixedMetadataValue(plugin, pet.getPetId().toString()));
        entity.setMetadata(OWNER_ID_KEY, new FixedMetadataValue(plugin, ownerPlayerId.toString()));

        // 이름 설정
        updateDisplayName();

        // 체력 설정
        double maxHealth = pet. getMaxHealth();
        entity.setMaxHealth(Math.max(1, maxHealth));
        entity.setHealth(Math.min(pet.getHealth(), entity.getMaxHealth()));

        // AI 비활성화 (커스텀 AI 사용)
        entity.setAI(false);

        // 길들이기 설정 (Tameable인 경우)
        if (entity instanceof Tameable) {
            Tameable tameable = (Tameable) entity;
            tameable.setTamed(true);
            Player owner = plugin.getServer().getPlayer(ownerPlayerId);
            if (owner != null) {
                tameable.setOwner(owner);
            }
        }

        // 무적 설정 (일정 조건)
        entity.setRemoveWhenFarAway(false);

        // 소리 없음 (선택적)
        entity.setSilent(pet.getBehavior() == PetBehavior.STEALTH);

        // 커스텀 모델 데이터 (리소스팩 연동)
        if (pet.getCustomModelId() != null) {
            // 커스텀 모델 적용 로직
        }
    }

    // ===== 이름 표시 =====

    /**
     * 표시 이름 업데이트
     */
    public void updateDisplayName() {
        if (entity == null) return;

        String displayName = buildDisplayName();
        entity.setCustomName(displayName);
        entity.setCustomNameVisible(true);
    }

    /**
     * 표시 이름 생성
     */
    private String buildDisplayName() {
        StringBuilder sb = new StringBuilder();

        // 희귀도 색상
        if (pet. getRarity() != null) {
            sb. append(pet.getRarity().getColorCode().replace("&", "§"));
        }

        // 펫 이름
        sb.append(pet. getPetName());

        // 레벨
        sb.append(" §7Lv.").append(pet.getLevel());

        // 체력 바
        sb.append(" ");
        sb.append(buildHealthBar());

        return sb.toString();
    }

    /**
     * 체력 바 생성
     */
    private String buildHealthBar() {
        double healthPercent = pet. getHealth() / pet.getMaxHealth();
        int filledBars = (int) (healthPercent * 10);

        StringBuilder bar = new StringBuilder("§8[");
        
        // 체력에 따른 색상
        String color;
        if (healthPercent > 0.5) {
            color = "§a";
        } else if (healthPercent > 0.25) {
            color = "§e";
        } else {
            color = "§c";
        }

        for (int i = 0; i < 10; i++) {
            if (i < filledBars) {
                bar.append(color).append("|");
            } else {
                bar. append("§7|");
            }
        }

        bar.append("§8]");
        return bar.toString();
    }

    // ===== 이동 =====

    /**
     * 위치로 텔레포트
     */
    public void teleport(Location location) {
        if (entity != null && entity.isValid()) {
            entity.teleport(location);
        }
    }

    /**
     * 주인에게 텔레포트
     */
    public void teleportToOwner() {
        Player owner = getOwner();
        if (owner != null && entity != null) {
            Location targetLoc = getSpawnLocationNearPlayer(owner);
            teleport(targetLoc);
        }
    }

    /**
     * 플레이어 근처 스폰 위치 계산
     */
    private Location getSpawnLocationNearPlayer(Player player) {
        Location playerLoc = player.getLocation();
        double angle = Math.random() * 2 * Math.PI;
        double distance = 2.0;

        double x = playerLoc.getX() + Math.cos(angle) * distance;
        double z = playerLoc.getZ() + Math.sin(angle) * distance;
        double y = playerLoc.getY();

        Location spawnLoc = new Location(playerLoc.getWorld(), x, y, z);

        // 안전한 위치 찾기
        while (! spawnLoc. getBlock().isPassable() && spawnLoc.getY() < playerLoc.getY() + 10) {
            spawnLoc.add(0, 1, 0);
        }

        spawnLoc.setYaw(playerLoc.getYaw());
        spawnLoc.setPitch(0);

        return spawnLoc;
    }

    /**
     * 이동 목표 설정
     */
    public void setMoveTarget(Location location) {
        this.moveTarget = location;
        if (petAI != null) {
            petAI.setMoveTarget(location);
        }
    }

    // ===== 전투 =====

    /**
     * 공격 대상 설정
     */
    public void setAttackTarget(LivingEntity target) {
        this.attackTarget = target;
        this.isAttacking = target != null;
        if (petAI != null) {
            petAI.setAttackTarget(target);
        }
    }

    /**
     * 공격 대상 해제
     */
    public void clearAttackTarget() {
        this.attackTarget = null;
        this.isAttacking = false;
        if (petAI != null) {
            petAI.clearAttackTarget();
        }
    }

    /**
     * 대상 공격
     */
    public void attack(LivingEntity target) {
        if (entity == null || ! entity.isValid() || target == null || ! target.isValid()) {
            return;
        }

        // 공격력 계산
        double damage = pet.getTotalStat("attack");

        // 치명타 계산
        double critChance = pet.getTotalStat("critical_chance");
        boolean isCrit = Math.random() * 100 < critChance;
        if (isCrit) {
            double critDamage = pet.getTotalStat("critical_damage");
            damage *= (1 + critDamage / 100);
        }

        // 데미지 적용
        target.damage(damage, entity);

        // 이펙트
        if (isCrit) {
            target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 10);
        }

        // 킬 카운트
        if (target. isDead()) {
            pet.incrementKillCount();
        }
    }

    /**
     * 데미지 받음
     */
    public void takeDamage(double damage, Entity damager) {
        if (entity == null) return;

        // 방어력 적용
        double defense = pet.getTotalStat("defense");
        double reduction = defense / (defense + 100);
        double finalDamage = damage * (1 - reduction);

        // 체력 감소
        pet.decreaseHealth(finalDamage);

        // 엔티티 체력 동기화
        double newHealth = Math.max(0.1, pet.getHealth());
        entity.setHealth(Math.min(newHealth, entity.getMaxHealth()));

        // 이름 업데이트
        updateDisplayName();

        // 마지막 데미지 시간
        lastDamageTime = System.currentTimeMillis();

        // 체력 0이면 기절
        if (pet.getHealth() <= 0) {
            onFaint();
        }
    }

    /**
     * 기절 처리
     */
    private void onFaint() {
        // 디스폰
        despawn();

        // 상태 변경
        pet.setStatus(PetStatus.FAINTED);

        // 알림
        Player owner = getOwner();
        if (owner != null) {
            plugin.getMessageUtil().sendMessage(owner, 
                plugin.getConfigManager().getMessage("pet.fainted")
                    .replace("{name}", pet.getPetName()));
        }
    }

    /**
     * 회복
     */
    public void heal(double amount) {
        pet.heal(amount);
        
        if (entity != null && entity.isValid()) {
            double newHealth = Math. min(pet.getHealth(), entity.getMaxHealth());
            entity.setHealth(Math.max(0.1, newHealth));
        }

        updateDisplayName();
    }

    // ===== 행동 =====

    /**
     * 행동 모드 업데이트
     */
    public void updateBehavior(PetBehavior behavior) {
        if (petAI != null) {
            petAI.setBehavior(behavior);
        }

        // 스텔스 모드면 조용히
        if (entity != null) {
            entity. setSilent(behavior == PetBehavior.STEALTH);
        }
    }

    /**
     * 앉기/서기 토글
     */
    public void toggleSit() {
        isSitting = !isSitting;
        
        if (petAI != null) {
            if (isSitting) {
                petAI.setSitting(true);
            } else {
                petAI.setSitting(false);
            }
        }

        Player owner = getOwner();
        if (owner != null) {
            String message = isSitting ? "pet.sit" : "pet.stand";
            plugin. getMessageUtil().sendMessage(owner, 
                plugin.getConfigManager().getMessage(message)
                    .replace("{name}", pet.getPetName()));
        }
    }

    /**
     * 따라오기 토글
     */
    public void toggleFollow() {
        isFollowing = !isFollowing;

        if (petAI != null) {
            petAI.setFollowing(isFollowing);
        }

        Player owner = getOwner();
        if (owner != null) {
            String message = isFollowing ? "pet.follow" : "pet. stay";
            plugin. getMessageUtil().sendMessage(owner, 
                plugin.getConfigManager().getMessage(message)
                    .replace("{name}", pet.getPetName()));
        }
    }

    // ===== 이펙트 =====

    /**
     * 스폰 이펙트
     */
    private void playSpawnEffect() {
        if (entity == null) return;
        Location loc = entity.getLocation();
        loc.getWorld().spawnParticle(Particle.CLOUD, loc. add(0, 0. 5, 0), 20, 0.3, 0. 3, 0.3, 0.05);
    }

    /**
     * 디스폰 이펙트
     */
    private void playDespawnEffect() {
        if (entity == null) return;
        Location loc = entity.getLocation();
        loc.getWorld().spawnParticle(Particle. SMOKE_NORMAL, loc.add(0, 0.5, 0), 20, 0.3, 0.3, 0.3, 0.05);
    }

    /**
     * 레벨업 이펙트
     */
    public void playLevelUpEffect() {
        if (entity == null) return;
        Location loc = entity.getLocation();
        loc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, loc.add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
        loc.getWorld().spawnParticle(Particle. FIREWORKS_SPARK, loc, 20, 0.3, 0.5, 0.3, 0.05);
    }

    /**
     * 행복 이펙트
     */
    public void playHappyEffect() {
        if (entity == null) return;
        Location loc = entity.getLocation();
        loc.getWorld().spawnParticle(Particle.HEART, loc.add(0, 1.5, 0), 5, 0.3, 0.2, 0.3, 0);
    }

    /**
     * 스킬 이펙트
     */
    public void playSkillEffect(Particle particle, int count) {
        if (entity == null) return;
        Location loc = entity.getLocation();
        loc.getWorld().spawnParticle(particle, loc. add(0, 1, 0), count, 0.5, 0.5, 0.5, 0.1);
    }

    // ===== 버프/디버프 =====

    /**
     * 포션 효과 적용
     */
    public void applyPotionEffect(PotionEffectType type, int duration, int amplifier) {
        if (entity != null && entity.isValid()) {
            entity.addPotionEffect(new PotionEffect(type, duration, amplifier));
        }
    }

    /**
     * 포션 효과 제거
     */
    public void removePotionEffect(PotionEffectType type) {
        if (entity != null && entity.isValid()) {
            entity.removePotionEffect(type);
        }
    }

    /**
     * 모든 포션 효과 제거
     */
    public void clearPotionEffects() {
        if (entity != null && entity.isValid()) {
            for (PotionEffect effect : entity.getActivePotionEffects()) {
                entity.removePotionEffect(effect. getType());
            }
        }
    }

    // ===== 틱 업데이트 =====

    /**
     * 틱 업데이트 (AI에서 호출)
     */
    public void tick() {
        if (! isSpawned || entity == null || !entity.isValid()) {
            return;
        }

        lastUpdate = System.currentTimeMillis();

        // 체력 동기화
        syncHealth();

        // 주인과의 거리 확인
        checkOwnerDistance();
    }

    /**
     * 체력 동기화
     */
    private void syncHealth() {
        if (entity == null) return;

        double entityHealth = entity.getHealth();
        double petHealth = pet. getHealth();

        // 엔티티 체력이 변경되었으면 펫 데이터 업데이트
        if (Math.abs(entityHealth - petHealth) > 0.1) {
            pet.setHealth(entityHealth);
            updateDisplayName();
        }
    }

    /**
     * 주인과의 거리 확인
     */
    private void checkOwnerDistance() {
        Player owner = getOwner();
        if (owner == null || entity == null) return;

        double distance = entity.getLocation().distance(owner.getLocation());
        double maxDistance = plugin.getConfigManager().getPetSettings().getTeleportDistance();

        if (distance > maxDistance) {
            teleportToOwner();
        }
    }

    // ===== Getter/Setter =====

    public Pet getPet() {
        return pet;
    }

    public UUID getOwnerPlayerId() {
        return ownerPlayerId;
    }

    public Player getOwner() {
        return plugin.getServer().getPlayer(ownerPlayerId);
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }

    public boolean isSpawned() {
        return isSpawned;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        this.isFollowing = following;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public boolean isSitting() {
        return isSitting;
    }

    public void setSitting(boolean sitting) {
        this. isSitting = sitting;
    }

    public LivingEntity getAttackTarget() {
        return attackTarget;
    }

    public Location getMoveTarget() {
        return moveTarget;
    }

    public PetAI getPetAI() {
        return petAI;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public long getLastDamageTime() {
        return lastDamageTime;
    }

    /**
     * 유효성 확인
     */
    public boolean isValid() {
        return isSpawned && entity != null && entity. isValid();
    }
}