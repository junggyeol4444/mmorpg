package com.multiverse.item.data;

import java.util.*;

public class Gem {
    
    private String gemId;
    private String name;
    private GemType type;
    private GemRarity rarity;
    private SocketColor color;
    private Map<String, Double> stats;
    private String description;
    private int level;
    private double experience;
    private double experienceRequired;
    
    /**
     * 기본 생성자
     */
    public Gem() {
        this.stats = new HashMap<>();
        this.level = 1;
        this.experience = 0;
        this.experienceRequired = 100;
    }
    
    /**
     * 주요 파라미터가 있는 생성자
     */
    public Gem(String gemId, String name, GemType type, GemRarity rarity, SocketColor color) {
        this.gemId = gemId;
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this. color = color;
        this. stats = new HashMap<>();
        this.level = 1;
        this. experience = 0;
        this.experienceRequired = 100;
    }
    
    // Getters and Setters
    public String getGemId() {
        return gemId;
    }
    
    public void setGemId(String gemId) {
        this.gemId = gemId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public GemType getType() {
        return type;
    }
    
    public void setType(GemType type) {
        this.type = type;
    }
    
    public GemRarity getRarity() {
        return rarity;
    }
    
    public void setRarity(GemRarity rarity) {
        this.rarity = rarity;
    }
    
    public SocketColor getColor() {
        return color;
    }
    
    public void setColor(SocketColor color) {
        this.color = color;
    }
    
    public Map<String, Double> getStats() {
        return stats;
    }
    
    public void setStats(Map<String, Double> stats) {
        this.stats = stats;
    }
    
    public void addStat(String statName, double value) {
        stats.put(statName, stats.getOrDefault(statName, 0.0) + value);
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }
    
    public double getExperience() {
        return experience;
    }
    
    public void addExperience(double exp) {
        this.experience += exp;
        
        // 레벨업 확인
        while (experience >= experienceRequired && level < 20) {
            experience -= experienceRequired;
            level++;
            experienceRequired *= 1.1; // 다음 레벨 필요 경험치 10% 증가
        }
    }
    
    public double getExperienceRequired() {
        return experienceRequired;
    }
    
    public void setExperienceRequired(double experienceRequired) {
        this.experienceRequired = Math.max(1, experienceRequired);
    }
    
    /**
     * 경험치 진행률 (%)
     */
    public double getExperienceProgress() {
        return (experience / experienceRequired) * 100;
    }
    
    /**
     * 보석 효과 배수 (등급 기반)
     */
    public double getEffectMultiplier() {
        return rarity.getStatMultiplier();
    }
    
    /**
     * 보석 효과 배수 (레벨 기반)
     */
    public double getLevelMultiplier() {
        return 1.0 + ((level - 1) * 0. 05); // 레벨당 5% 증가
    }
    
    /**
     * 최종 효과 배수
     */
    public double getFinalMultiplier() {
        return getEffectMultiplier() * getLevelMultiplier();
    }
    
    /**
     * 보석의 효과적인 스탯 반환
     */
    public Map<String, Double> getEffectiveStats() {
        Map<String, Double> effectiveStats = new HashMap<>();
        double multiplier = getFinalMultiplier();
        
        for (String statName : stats.keySet()) {
            double originalValue = stats.get(statName);
            effectiveStats.put(statName, originalValue * multiplier);
        }
        
        return effectiveStats;
    }
    
    /**
     * 보석 색상 코드 반환
     */
    public String getColorCode() {
        switch (color) {
            case RED:
                return "&c";
            case BLUE:
                return "&9";
            case GREEN:
                return "&a";
            case YELLOW:
                return "&e";
            case PURPLE:
                return "&5";
            case WHITE:
                return "&f";
            default:
                return "&7";
        }
    }
    
    /**
     * 보석 정보 출력
     */
    @Override
    public String toString() {
        return "Gem{" +
                "gemId='" + gemId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", rarity=" + rarity +
                ", level=" + level +
                ", stats=" + stats. size() +
                '}';
    }
}