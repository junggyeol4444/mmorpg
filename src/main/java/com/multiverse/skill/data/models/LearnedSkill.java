package com.multiverse.skill. data.models;

public class LearnedSkill {

    private String skillId;
    private int level;
    private long experience;
    
    // 쿨다운
    private long lastUsedTime;
    
    // 통계
    private int timesUsed;
    private long totalDamage;

    public LearnedSkill() {
    }

    public LearnedSkill(String skillId) {
        this.skillId = skillId;
        this.level = 1;
        this. experience = 0;
        this.lastUsedTime = 0;
        this.timesUsed = 0;
        this.totalDamage = 0;
    }

    // Getters and Setters

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
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

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long lastUsedTime) {
        this. lastUsedTime = lastUsedTime;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
        this. timesUsed = timesUsed;
    }

    public long getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(long totalDamage) {
        this.totalDamage = totalDamage;
    }

    /**
     * 스킬 경험치 추가
     */
    public void addExperience(long amount) {
        this.experience += amount;
    }

    /**
     * 사용 횟수 증가
     */
    public void incrementTimesUsed() {
        this.timesUsed++;
    }

    /**
     * 데미지 추가
     */
    public void addDamage(long damage) {
        this.totalDamage += damage;
    }

    /**
     * 평균 데미지 계산
     */
    public double getAverageDamage() {
        if (timesUsed == 0) {
            return 0;
        }
        return (double) totalDamage / timesUsed;
    }

    /**
     * 스킬 통계 문자열
     */
    public String getStatsString() {
        return String.format("Lv.  %d | 사용: %d회 | 총 데미지: %d | 평균: %.2f",
                level, timesUsed, totalDamage, getAverageDamage());
    }

    @Override
    public String toString() {
        return "LearnedSkill{" +
                "skillId='" + skillId + '\'' +
                ", level=" + level +
                ", experience=" + experience +
                ", timesUsed=" + timesUsed +
                '}';
    }
}