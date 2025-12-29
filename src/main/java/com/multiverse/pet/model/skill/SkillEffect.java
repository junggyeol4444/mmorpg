package com.multiverse.pet.model.skill;

import com.multiverse.pet.model.Pet;
import org.bukkit.Location;
import org. bukkit.entity. Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org. bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * 스킬 효과 데이터 클래스
 * 스킬 발동 시 적용되는 효과를 정의
 */
public class SkillEffect {

    // 기본 정보
    private String skillId;
    private String effectId;
    private SkillType effectType;

    // 효과 값
    private Map<String, Double> values;

    // 타겟팅
    private PetSkill. SkillTarget target;
    private double range;
    private int maxTargets;

    // 지속 시간
    private int duration;           // 초 (0이면 즉시 효과)
    private int tickInterval;       // 틱 간격
    private long startTime;
    private long endTime;

    // 상태
    private boolean active;
    private int ticksRemaining;

    // 적용 대상
    private UUID sourceEntityId;    // 스킬 사용자 (펫)
    private UUID ownerPlayerId;     // 펫 주인
    private List<UUID> affectedEntities;

    // 스택
    private int currentStacks;
    private int maxStacks;
    private boolean stackable;

    // 포션 효과
    private List<PotionEffectData> potionEffects;

    // 특수 효과
    private Map<String, Object> customData;

    /**
     * 기본 생성자
     */
    public SkillEffect() {
        this.values = new HashMap<>();
        this.affectedEntities = new ArrayList<>();
        this.potionEffects = new ArrayList<>();
        this.customData = new HashMap<>();
        this.target = PetSkill. SkillTarget. SELF;
        this. range = 0;
        this.maxTargets = 1;
        this. duration = 0;
        this.tickInterval = 20;
        this.active = false;
        this.ticksRemaining = 0;
        this.currentStacks = 1;
        this. maxStacks = 1;
        this.stackable = false;
    }

    /**
     * 스킬 기반 생성자
     */
    public SkillEffect(PetSkill skill) {
        this();
        this.skillId = skill.getSkillId();
        this.effectId = skill.getSkillId() + "_effect";
        this.effectType = skill.getType();
        this.values = new HashMap<>(skill.getAllEffects());
        this.target = skill. getTarget();
        this.range = skill.getRange();
        this.maxTargets = skill.getMaxTargets();
        this.duration = skill.getDuration();
        this.tickInterval = skill.getTickInterval();
    }

    // ===== 효과 활성화/비활성화 =====

    /**
     * 효과 활성화
     */
    public void activate() {
        this.active = true;
        this.startTime = System.currentTimeMillis();
        if (duration > 0) {
            this.endTime = startTime + (duration * 1000L);
            this.ticksRemaining = duration * 20; // 초 -> 틱
        } else {
            this.endTime = startTime;
            this.ticksRemaining = 0;
        }
    }

    /**
     * 효과 비활성화
     */
    public void deactivate() {
        this.active = false;
        this.ticksRemaining = 0;
    }

    /**
     * 효과 만료 여부 확인
     */
    public boolean isExpired() {
        if (! active) return true;
        if (duration <= 0) return true; // 즉시 효과는 바로 만료
        return System.currentTimeMillis() >= endTime;
    }

    /**
     * 남은 시간 (초)
     */
    public double getRemainingTime() {
        if (!active || duration <= 0) return 0;
        long remaining = endTime - System.currentTimeMillis();
        return Math.max(0, remaining / 1000.0);
    }

    /**
     * 틱 처리 (지속 효과용)
     *
     * @return 계속 진행 여부
     */
    public boolean tick() {
        if (!active || duration <= 0) return false;
        
        ticksRemaining--;
        if (ticksRemaining <= 0) {
            deactivate();
            return false;
        }
        return true;
    }

    /**
     * 틱 간격 체크
     *
     * @param currentTick 현재 틱
     * @return 효과 적용 여부
     */
    public boolean shouldApplyThisTick(int currentTick) {
        return currentTick % tickInterval == 0;
    }

    // ===== 효과 값 관련 =====

    /**
     * 효과 값 가져오기
     */
    public double getValue(String key) {
        return values.getOrDefault(key, 0.0);
    }

    /**
     * 효과 값 설정
     */
    public void setValue(String key, double value) {
        values.put(key, value);
    }

    /**
     * 스택 적용된 효과 값 가져오기
     */
    public double getStackedValue(String key) {
        double baseValue = getValue(key);
        return baseValue * currentStacks;
    }

    /**
     * 데미지 값 가져오기
     */
    public double getDamage() {
        return getStackedValue("damage");
    }

    /**
     * 힐량 가져오기
     */
    public double getHealing() {
        return getStackedValue("healing");
    }

    /**
     * 버프/디버프 수치 가져오기
     */
    public double getModifier(String statName) {
        return getStackedValue(statName + "_modifier");
    }

    // ===== 스택 관련 =====

    /**
     * 스택 추가
     *
     * @return 스택 추가 성공 여부
     */
    public boolean addStack() {
        if (! stackable) return false;
        if (currentStacks >= maxStacks) return false;
        currentStacks++;
        return true;
    }

    /**
     * 스택 제거
     */
    public void removeStack() {
        currentStacks = Math.max(1, currentStacks - 1);
    }

    /**
     * 스택 리셋
     */
    public void resetStacks() {
        currentStacks = 1;
    }

    /**
     * 최대 스택인지 확인
     */
    public boolean isMaxStacks() {
        return currentStacks >= maxStacks;
    }

    // ===== 타겟 관련 =====

    /**
     * 영향 받는 엔티티 추가
     */
    public void addAffectedEntity(UUID entityId) {
        if (! affectedEntities. contains(entityId)) {
            affectedEntities.add(entityId);
        }
    }

    /**
     * 영향 받는 엔티티 제거
     */
    public void removeAffectedEntity(UUID entityId) {
        affectedEntities.remove(entityId);
    }

    /**
     * 엔티티가 영향 받고 있는지 확인
     */
    public boolean isAffected(UUID entityId) {
        return affectedEntities.contains(entityId);
    }

    /**
     * 최대 타겟 수 도달 여부
     */
    public boolean isMaxTargetsReached() {
        return affectedEntities.size() >= maxTargets;
    }

    /**
     * 범위 내 타겟 찾기
     *
     * @param center 중심 위치
     * @param pet 스킬 사용 펫
     * @param owner 펫 주인
     * @return 타겟 엔티티 목록
     */
    public List<LivingEntity> findTargets(Location center, Pet pet, Player owner) {
        List<LivingEntity> targets = new ArrayList<>();
        
        if (center == null || center.getWorld() == null) {
            return targets;
        }

        Collection<Entity> nearbyEntities = center.getWorld()
                .getNearbyEntities(center, range, range, range);

        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity) entity;

            if (isValidTarget(living, pet, owner)) {
                targets.add(living);
                if (targets.size() >= maxTargets) break;
            }
        }

        return targets;
    }

    /**
     * 유효한 타겟인지 확인
     */
    private boolean isValidTarget(LivingEntity entity, Pet pet, Player owner) {
        switch (target) {
            case SELF: 
                // 펫 자신만 대상
                return entity. getUniqueId().equals(sourceEntityId);
                
            case OWNER:
                // 주인만 대상
                return entity instanceof Player && 
                       entity.getUniqueId().equals(ownerPlayerId);
                
            case ENEMY:
                // 적만 대상 (주인과 펫 제외)
                if (entity instanceof Player) {
                    return !entity.getUniqueId().equals(ownerPlayerId);
                }
                return ! entity.getUniqueId().equals(sourceEntityId);
                
            case ALLY:
                // 아군만 대상 (주인 포함)
                if (entity instanceof Player) {
                    return entity.getUniqueId().equals(ownerPlayerId);
                }
                return entity.getUniqueId().equals(sourceEntityId);
                
            case AREA:
            case ALL: 
                // 범위 내 모든 대상
                return true;
                
            default:
                return false;
        }
    }

    // ===== 포션 효과 관련 =====

    /**
     * 포션 효과 추가
     */
    public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
        potionEffects.add(new PotionEffectData(type, duration, amplifier));
    }

    /**
     * 포션 효과 적용
     */
    public void applyPotionEffects(LivingEntity entity) {
        for (PotionEffectData data : potionEffects) {
            PotionEffect effect = new PotionEffect(
                    data.getType(),
                    data. getDuration() * 20, // 초 -> 틱
                    data.getAmplifier(),
                    false,
                    true
            );
            entity.addPotionEffect(effect);
        }
    }

    /**
     * 포션 효과 제거
     */
    public void removePotionEffects(LivingEntity entity) {
        for (PotionEffectData data :  potionEffects) {
            entity. removePotionEffect(data.getType());
        }
    }

    // ===== 효과 타입별 적용 =====

    /**
     * 데미지 효과 적용
     */
    public void applyDamage(LivingEntity target, double additionalDamage) {
        double damage = getDamage() + additionalDamage;
        if (damage > 0) {
            target.damage(damage);
        }
    }

    /**
     * 힐 효과 적용
     */
    public void applyHealing(LivingEntity target, double additionalHealing) {
        double healing = getHealing() + additionalHealing;
        if (healing > 0) {
            double newHealth = Math.min(
                    target.getHealth() + healing,
                    target.getMaxHealth()
            );
            target. setHealth(newHealth);
        }
    }

    /**
     * 펫에 힐 효과 적용
     */
    public void applyHealingToPet(Pet pet, double additionalHealing) {
        double healing = getHealing() + additionalHealing;
        if (healing > 0) {
            pet.heal(healing);
        }
    }

    // ===== 커스텀 데이터 =====

    /**
     * 커스텀 데이터 설정
     */
    public void setCustomData(String key, Object value) {
        customData.put(key, value);
    }

    /**
     * 커스텀 데이터 가져오기
     */
    @SuppressWarnings("unchecked")
    public <T> T getCustomData(String key, Class<T> type) {
        Object value = customData. get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 커스텀 데이터 존재 여부
     */
    public boolean hasCustomData(String key) {
        return customData. containsKey(key);
    }

    // ===== Getter/Setter =====

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getEffectId() {
        return effectId;
    }

    public void setEffectId(String effectId) {
        this.effectId = effectId;
    }

    public SkillType getEffectType() {
        return effectType;
    }

    public void setEffectType(SkillType effectType) {
        this.effectType = effectType;
    }

    public Map<String, Double> getValues() {
        return values;
    }

    public void setValues(Map<String, Double> values) {
        this.values = values;
    }

    public PetSkill. SkillTarget getTarget() {
        return target;
    }

    public void setTarget(PetSkill.SkillTarget target) {
        this.target = target;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public int getMaxTargets() {
        return maxTargets;
    }

    public void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getTicksRemaining() {
        return ticksRemaining;
    }

    public void setTicksRemaining(int ticksRemaining) {
        this.ticksRemaining = ticksRemaining;
    }

    public UUID getSourceEntityId() {
        return sourceEntityId;
    }

    public void setSourceEntityId(UUID sourceEntityId) {
        this. sourceEntityId = sourceEntityId;
    }

    public UUID getOwnerPlayerId() {
        return ownerPlayerId;
    }

    public void setOwnerPlayerId(UUID ownerPlayerId) {
        this.ownerPlayerId = ownerPlayerId;
    }

    public List<UUID> getAffectedEntities() {
        return affectedEntities;
    }

    public void setAffectedEntities(List<UUID> affectedEntities) {
        this.affectedEntities = affectedEntities;
    }

    public int getCurrentStacks() {
        return currentStacks;
    }

    public void setCurrentStacks(int currentStacks) {
        this.currentStacks = currentStacks;
    }

    public int getMaxStacks() {
        return maxStacks;
    }

    public void setMaxStacks(int maxStacks) {
        this. maxStacks = maxStacks;
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public List<PotionEffectData> getPotionEffects() {
        return potionEffects;
    }

    public void setPotionEffects(List<PotionEffectData> potionEffects) {
        this. potionEffects = potionEffects;
    }

    public Map<String, Object> getCustomData() {
        return customData;
    }

    public void setCustomData(Map<String, Object> customData) {
        this.customData = customData;
    }

    @Override
    public String toString() {
        return "SkillEffect{" +
                "skillId='" + skillId + '\'' +
                ", effectType=" + effectType +
                ", duration=" + duration +
                ", active=" + active +
                ", stacks=" + currentStacks + "/" + maxStacks +
                '}';
    }

    // ===== 내부 클래스 =====

    /**
     * 포션 효과 데이터
     */
    public static class PotionEffectData {
        private PotionEffectType type;
        private int duration;       // 초
        private int amplifier;

        public PotionEffectData() {}

        public PotionEffectData(PotionEffectType type, int duration, int amplifier) {
            this. type = type;
            this.duration = duration;
            this.amplifier = amplifier;
        }

        public PotionEffectType getType() {
            return type;
        }

        public void setType(PotionEffectType type) {
            this.type = type;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this. duration = duration;
        }

        public int getAmplifier() {
            return amplifier;
        }

        public void setAmplifier(int amplifier) {
            this.amplifier = amplifier;
        }
    }
}