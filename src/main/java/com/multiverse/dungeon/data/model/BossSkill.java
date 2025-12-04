package com.multiverse.dungeon.data.model;

import com.multiverse.dungeon.data.enums.BossSkillType;

/**
 * 보스 스킬 데이터 클래스
 */
public class BossSkill {

    private String skillId; // 스킬 ID
    private String name; // 스킬 이름
    private BossSkillType type; // 스킬 타입
    
    private double healthThreshold; // 사용 조건 HP (%)
    private int cooldown; // 쿨다운 (초)
    private long lastUsedTime; // 마지막 사용 시간
    
    private double damage; // 데미지
    private double radius; // 범위 (블록)
    private int duration; // 지속 시간 (초)
    
    private String castMessage; // 시전 메시지

    /**
     * 생성자
     */
    public BossSkill(String skillId, String name, BossSkillType type) {
        this.skillId = skillId;
        this.name = name;
        this.type = type;
        this.healthThreshold = 0.0;
        this.cooldown = 10;
        this.lastUsedTime = 0;
        this.damage = 0.0;
        this.radius = 0.0;
        this.duration = 0;
        this.castMessage = "";
    }

    /**
     * 기본 생성자
     */
    public BossSkill() {
        this("", "", BossSkillType.AOE_DAMAGE);
    }

    // ===== Getters & Setters =====

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BossSkillType getType() {
        return type;
    }

    public void setType(BossSkillType type) {
        this.type = type;
    }

    public double getHealthThreshold() {
        return healthThreshold;
    }

    public void setHealthThreshold(double healthThreshold) {
        this.healthThreshold = Math.max(0, Math.min(100, healthThreshold));
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = Math.max(0, cooldown);
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long lastUsedTime) {
        this. lastUsedTime = lastUsedTime;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = Math.max(0, damage);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = Math.max(0, radius);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this. duration = Math.max(0, duration);
    }

    public String getCastMessage() {
        return castMessage;
    }

    public void setCastMessage(String castMessage) {
        this. castMessage = castMessage != null ? castMessage : "";
    }

    /**
     * 스킬 사용 가능 여부 확인
     *
     * @param currentHealthPercent 보스 현재 HP (%)
     * @return 사용 가능하면 true
     */
    public boolean canUse(double currentHealthPercent) {
        // HP 조건 확인
        if (currentHealthPercent < healthThreshold) {
            return false;
        }

        // 쿨다운 확인
        long currentTime = System.currentTimeMillis();
        long timeSinceLastUse = currentTime - lastUsedTime;
        return timeSinceLastUse >= (cooldown * 1000L);
    }

    /**
     * 스킬 사용 (시간 업데이트)
     */
    public void use() {
        this.lastUsedTime = System. currentTimeMillis();
    }

    /**
     * 쿨다운 남은 시간 (초)
     *
     * @return 남은 시간 (0 이상)
     */
    public int getRemainingCooldown() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastUse = currentTime - lastUsedTime;
        long remaining = (cooldown * 1000L) - timeSinceLastUse;
        return (int) Math.max(0, remaining / 1000);
    }

    @Override
    public String toString() {
        return "BossSkill{" +
                "skillId='" + skillId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", cooldown=" + cooldown +
                ", damage=" + damage +
                ", radius=" + radius +
                '}';
    }
}