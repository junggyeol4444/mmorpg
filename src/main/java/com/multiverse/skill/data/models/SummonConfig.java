package com.multiverse.skill.data.models;

import com.multiverse.skill.data.enums.SummonAI;

public class SummonConfig {

    private String entityType;
    private int duration;
    private int maxSummons;
    private SummonAI ai;
    private double healthMultiplier;
    private double damageMultiplier;

    public SummonConfig() {
    }

    // Getters and Setters

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMaxSummons() {
        return maxSummons;
    }

    public void setMaxSummons(int maxSummons) {
        this.maxSummons = maxSummons;
    }

    public SummonAI getAi() {
        return ai;
    }

    public void setAi(SummonAI ai) {
        this. ai = ai;
    }

    public double getHealthMultiplier() {
        return healthMultiplier;
    }

    public void setHealthMultiplier(double healthMultiplier) {
        this.healthMultiplier = healthMultiplier;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    /**
     * 소환수 체력 계산
     */
    public double calculateHealth(double baseHealth) {
        return baseHealth * healthMultiplier;
    }

    /**
     * 소환수 데미지 계산
     */
    public double calculateDamage(double baseDamage) {
        return baseDamage * damageMultiplier;
    }

    /**
     * 소환 시간 초 단위로 변환
     */
    public long getDurationTicks() {
        return (long) duration * 20;
    }

    /**
     * 소환 설정 정보 문자열
     */
    public String getInfoString() {
        return String.format("Entity: %s | Duration: %ds | MaxSummons: %d | AI: %s | Health: x%. 2f | Damage: x%. 2f",
                entityType,
                duration,
                maxSummons,
                ai. name(),
                healthMultiplier,
                damageMultiplier);
    }

    @Override
    public String toString() {
        return "SummonConfig{" +
                "entityType='" + entityType + '\'' +
                ", duration=" + duration +
                ", maxSummons=" + maxSummons +
                ", ai=" + ai +
                '}';
    }
}