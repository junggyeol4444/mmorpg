package com.multiverse.skill.data. models;

import com.multiverse.skill.data.enums. DamageType;
import com. multiverse.skill.data.enums.EffectType;
import com.multiverse.skill. data.enums.TargetType;

import java.util.*;

public class SkillEffect {

    private String effectId;
    private EffectType type;
    
    // 데미지/힐
    private double baseValue;
    private DamageType damageType;
    private Map<String, Double> scaling;
    
    // 범위
    private TargetType targetType;
    private double range;
    private double radius;
    private int maxTargets;
    
    // 투사체
    private ProjectileConfig projectile;
    
    // 지속 효과
    private int duration;
    private int tickInterval;
    
    // 소환
    private SummonConfig summon;
    
    // 추가 효과
    private List<SkillEffect> additionalEffects;

    // 버프/디버프
    private Map<String, Object> buffEffects;
    private Map<String, Object> debuffEffects;

    public SkillEffect() {
        this. scaling = new HashMap<>();
        this. additionalEffects = new ArrayList<>();
        this.buffEffects = new HashMap<>();
        this.debuffEffects = new HashMap<>();
    }

    // Getters and Setters

    public String getEffectId() {
        return effectId;
    }

    public void setEffectId(String effectId) {
        this.effectId = effectId;
    }

    public EffectType getType() {
        return type;
    }

    public void setType(EffectType type) {
        this.type = type;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(double baseValue) {
        this.baseValue = baseValue;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType damageType) {
        this. damageType = damageType;
    }

    public Map<String, Double> getScaling() {
        return scaling;
    }

    public void setScaling(Map<String, Double> scaling) {
        this.scaling = scaling;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getMaxTargets() {
        return maxTargets;
    }

    public void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    public ProjectileConfig getProjectileConfig() {
        return projectile;
    }

    public void setProjectileConfig(ProjectileConfig projectile) {
        this. projectile = projectile;
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

    public SummonConfig getSummonConfig() {
        return summon;
    }

    public void setSummonConfig(SummonConfig summon) {
        this.summon = summon;
    }

    public List<SkillEffect> getAdditionalEffects() {
        return additionalEffects;
    }

    public void setAdditionalEffects(List<SkillEffect> additionalEffects) {
        this.additionalEffects = additionalEffects;
    }

    public Map<String, Object> getBuffEffects() {
        return buffEffects;
    }

    public void setBuffEffects(Map<String, Object> buffEffects) {
        this.buffEffects = buffEffects;
    }

    public Map<String, Object> getDebuffEffects() {
        return debuffEffects;
    }

    public void setDebuffEffects(Map<String, Object> debuffEffects) {
        this.debuffEffects = debuffEffects;
    }

    /**
     * 스케일링 계수 추가
     */
    public void addScaling(String stat, double coefficient) {
        scaling.put(stat, coefficient);
    }

    /**
     * 스케일링 계수 조회
     */
    public double getScalingCoefficient(String stat) {
        return scaling.getOrDefault(stat, 0.0);
    }

    /**
     * 추가 효과 추가
     */
    public void addAdditionalEffect(SkillEffect effect) {
        additionalEffects.add(effect);
    }

    /**
     * 버프 효과 추가
     */
    public void addBuff(String buffType, Object value) {
        buffEffects. put(buffType, value);
    }

    /**
     * 디버프 효과 추가
     */
    public void addDebuff(String debuffType, Object value) {
        debuffEffects.put(debuffType, value);
    }

    /**
     * 효과 정보 문자열
     */
    public String getInfoString() {
        return String.format("Effect: %s | Type: %s | Base: %.1f | Range: %.1f | Duration: %ds",
                effectId,
                type. name(),
                baseValue,
                range,
                duration);
    }

    @Override
    public String toString() {
        return "SkillEffect{" +
                "effectId='" + effectId + '\'' +
                ", type=" + type +
                ", baseValue=" + baseValue +
                '}';
    }
}