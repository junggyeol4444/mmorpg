package com.multiverse.item.data;

import java.util.*;

public class SkillEffect {
    
    private String effectId;
    private String name;
    private EffectType type;
    private double power; // 효과의 강도
    private int duration; // 지속 시간 (틱)
    private int cooldown; // 쿨타임 (틱)
    private double triggerChance; // 발동 확률 (%)
    private String description;
    private Map<String, Double> parameters; // 추가 파라미터
    
    /**
     * 기본 생성자
     */
    public SkillEffect() {
        this.parameters = new HashMap<>();
        this.power = 1.0;
        this.duration = 0;
        this.cooldown = 0;
        this.triggerChance = 100.0;
    }
    
    /**
     * 주요 파라미터가 있는 생성자
     */
    public SkillEffect(String effectId, String name, EffectType type, double power) {
        this.effectId = effectId;
        this.name = name;
        this.type = type;
        this.power = power;
        this.parameters = new HashMap<>();
        this. triggerChance = 100.0;
    }
    
    // Getters and Setters
    public String getEffectId() {
        return effectId;
    }
    
    public void setEffectId(String effectId) {
        this.effectId = effectId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this. name = name;
    }
    
    public EffectType getType() {
        return type;
    }
    
    public void setType(EffectType type) {
        this. type = type;
    }
    
    public double getPower() {
        return power;
    }
    
    public void setPower(double power) {
        this.power = Math.max(0, power);
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = Math.max(0, duration);
    }
    
    public int getCooldown() {
        return cooldown;
    }
    
    public void setCooldown(int cooldown) {
        this.cooldown = Math.max(0, cooldown);
    }
    
    public double getTriggerChance() {
        return triggerChance;
    }
    
    public void setTriggerChance(double triggerChance) {
        this. triggerChance = Math.max(0, Math.min(triggerChance, 100.0));
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<String, Double> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Double> parameters) {
        this.parameters = parameters;
    }
    
    public void addParameter(String key, double value) {
        parameters.put(key, value);
    }
    
    /**
     * 효과 발동 여부
     */
    public boolean shouldTrigger() {
        return Math.random() * 100 < triggerChance;
    }
    
    /**
     * 파라미터 값 조회
     */
    public double getParameter(String key, double defaultValue) {
        return parameters.getOrDefault(key, defaultValue);
    }
    
    /**
     * 효과 강도 계산 (파워 기반)
     */
    public double calculateEffectValue(double baseValue) {
        return baseValue * power;
    }
    
    /**
     * 지속 효과 여부
     */
    public boolean isDurationEffect() {
        return duration > 0;
    }
    
    /**
     * 쿨타임이 있는 효과인지 확인
     */
    public boolean hasCooldown() {
        return cooldown > 0;
    }
    
    /**
     * 효과 정보 출력
     */
    @Override
    public String toString() {
        return "SkillEffect{" +
                "effectId='" + effectId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", power=" + power +
                ", duration=" + duration +
                ", cooldown=" + cooldown +
                ", triggerChance=" + triggerChance +
                '}';
    }
}