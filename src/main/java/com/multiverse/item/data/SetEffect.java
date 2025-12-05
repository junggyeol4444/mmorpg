package com. multiverse.item.data;

import java.util.*;

public class SetEffect {
    
    private String effectId;
    private String name;
    private EffectType effectType;
    private Map<String, Double> parameters; // 효과 파라미터
    private int duration; // 지속 시간 (틱)
    private int amplifier; // 효과 강도
    private boolean isPositive; // 긍정 효과 여부
    
    /**
     * 기본 생성자
     */
    public SetEffect() {
        this.parameters = new HashMap<>();
        this. duration = 0;
        this.amplifier = 0;
        this.isPositive = true;
    }
    
    /**
     * 주요 파라미터가 있는 생성자
     */
    public SetEffect(String effectId, String name, EffectType effectType) {
        this.effectId = effectId;
        this.name = name;
        this.effectType = effectType;
        this.parameters = new HashMap<>();
        this.duration = 0;
        this.amplifier = 0;
        this.isPositive = true;
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
        this.name = name;
    }
    
    public EffectType getEffectType() {
        return effectType;
    }
    
    public void setEffectType(EffectType effectType) {
        this.effectType = effectType;
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
    
    public Double getParameter(String key) {
        return parameters.get(key);
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = Math.max(0, duration);
    }
    
    public int getAmplifier() {
        return amplifier;
    }
    
    public void setAmplifier(int amplifier) {
        this.amplifier = Math. max(0, amplifier);
    }
    
    public boolean isPositive() {
        return isPositive;
    }
    
    public void setPositive(boolean positive) {
        isPositive = positive;
    }
    
    /**
     * 효과 파라미터 값 조회 (기본값 포함)
     */
    public double getParameterOrDefault(String key, double defaultValue) {
        return parameters.getOrDefault(key, defaultValue);
    }
    
    /**
     * 효과 강도에 따른 값 계산
     */
    public double getAmplifiedValue(double baseValue) {
        return baseValue * (1. 0 + (amplifier * 0.1)); // 강도당 10% 증가
    }
    
    /**
     * 효과 설명 반환
     */
    public String getDescription() {
        switch (effectType) {
            case STAT_INCREASE:
                return "스탯을 증가시킵니다. ";
            case DAMAGE_INCREASE:
                return "피해를 증가시킵니다.";
            case DEFENSE_INCREASE:
                return "방어력을 증가시킵니다.";
            case SPEED_INCREASE:
                return "이동 속도를 증가시킵니다.";
            case SPECIAL_ABILITY:
                return "특수한 능력이 활성화됩니다.";
            default:
                return "알 수 없는 효과입니다.";
        }
    }
    
    /**
     * 효과 정보 출력
     */
    @Override
    public String toString() {
        return "SetEffect{" +
                "effectId='" + effectId + '\'' +
                ", name='" + name + '\'' +
                ", effectType=" + effectType +
                ", duration=" + duration +
                ", amplifier=" + amplifier +
                ", isPositive=" + isPositive +
                '}';
    }
}