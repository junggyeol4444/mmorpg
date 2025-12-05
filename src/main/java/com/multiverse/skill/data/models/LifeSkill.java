package com.multiverse.skill. data.models;

import com. multiverse.skill.data.enums.LifeSkillType;

import java.util.*;

public class LifeSkill {

    private LifeSkillType type;
    private int level;
    private long experience;
    private long experienceToNext;
    
    // 스킬별 보너스
    private Map<String, Double> bonuses;

    public LifeSkill() {
        this.bonuses = new HashMap<>();
        this.level = 1;
        this. experience = 0;
        this.experienceToNext = 1000;
    }

    public LifeSkill(LifeSkillType type) {
        this();
        this.type = type;
    }

    // Getters and Setters

    public LifeSkillType getType() {
        return type;
    }

    public void setType(LifeSkillType type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public long getExperienceToNext() {
        return experienceToNext;
    }

    public void setExperienceToNext(long experienceToNext) {
        this.experienceToNext = experienceToNext;
    }

    public Map<String, Double> getBonuses() {
        return bonuses;
    }

    public void setBonuses(Map<String, Double> bonuses) {
        this.bonuses = bonuses;
    }

    /**
     * 경험치 추가
     */
    public void addExperience(long amount) {
        this.experience += amount;
    }

    /**
     * 경험치 비율 (0.  0 ~ 1.0)
     */
    public double getExperiencePercent() {
        if (experienceToNext <= 0) {
            return 1.0;
        }
        return Math.min((double) experience / experienceToNext, 1.0);
    }

    /**
     * 남은 경험치
     */
    public long getRemainingExperience() {
        return Math.max(0, experienceToNext - experience);
    }

    /**
     * 보너스 추가
     */
    public void addBonus(String bonusKey, double value) {
        bonuses.put(bonusKey, value);
    }

    /**
     * 보너스 조회
     */
    public double getBonus(String bonusKey) {
        return bonuses. getOrDefault(bonusKey, 1.0);
    }

    /**
     * 채광 속도 보너스
     */
    public double getMiningSpeedBonus() {
        return 1.0 + (level * 0.05);
    }

    /**
     * 드롭율 보너스
     */
    public double getDropChanceBonus() {
        return Math.min(1.0 + (level * 0.02), 0.95);
    }

    /**
     * 경험치 보너스
     */
    public double getExperienceMultiplier() {
        return 1.0 + (level * 0.03);
    }

    /**
     * 생활 스킬 정보 문자열
     */
    public String getInfoString() {
        return String. format("%s Lv. %d | 경험치: %d/%d (%.1f%%) | 채광속도: x%. 2f | 드롭율: %.1f%%",
                type.getDisplayName(),
                level,
                experience,
                experienceToNext,
                getExperiencePercent() * 100,
                getMiningSpeedBonus(),
                getDropChanceBonus() * 100);
    }

    @Override
    public String toString() {
        return "LifeSkill{" +
                "type=" + type +
                ", level=" + level +
                ", experience=" + experience +
                '}';
    }
}