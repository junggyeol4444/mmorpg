package com. multiverse.item.data;

public class StatusEffect {
    
    private String effectId;
    private String name;
    private EffectType type;
    private int amplifier; // 효과 강도 (0-5)
    private int duration; // 남은 지속 시간 (틱)
    private int maxDuration; // 최대 지속 시간
    private boolean isActive;
    private long startTime;
    private double power; // 효과의 강도
    
    /**
     * 기본 생성자
     */
    public StatusEffect() {
        this.amplifier = 0;
        this.duration = 0;
        this.isActive = false;
        this. startTime = System.currentTimeMillis();
        this.power = 1.0;
    }
    
    /**
     * 주요 파라미터가 있는 생성자
     */
    public StatusEffect(String effectId, String name, EffectType type, int duration) {
        this.effectId = effectId;
        this.name = name;
        this.type = type;
        this.duration = duration;
        this.maxDuration = duration;
        this.amplifier = 0;
        this.isActive = true;
        this.startTime = System.currentTimeMillis();
        this.power = 1. 0;
    }
    
    // Getters and Setters
    public String getEffectId() {
        return effectId;
    }
    
    public void setEffectId(String effectId) {
        this. effectId = effectId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public EffectType getType() {
        return type;
    }
    
    public void setType(EffectType type) {
        this.type = type;
    }
    
    public int getAmplifier() {
        return amplifier;
    }
    
    public void setAmplifier(int amplifier) {
        this.amplifier = Math.max(0, Math.min(amplifier, 5));
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = Math. max(0, duration);
    }
    
    public int getMaxDuration() {
        return maxDuration;
    }
    
    public void setMaxDuration(int maxDuration) {
        this.maxDuration = Math.max(0, maxDuration);
    }
    
    public boolean isActive() {
        return isActive && duration > 0;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this. startTime = startTime;
    }
    
    public double getPower() {
        return power;
    }
    
    public void setPower(double power) {
        this.power = Math. max(0, power);
    }
    
    /**
     * 효과 진행률 (%)
     */
    public double getProgress() {
        if (maxDuration == 0) {
            return 0;
        }
        return ((double) (maxDuration - duration) / maxDuration) * 100;
    }
    
    /**
     * 효과 강도에 따른 배수
     */
    public double getAmplifierMultiplier() {
        return 1.0 + (amplifier * 0.2); // 강도당 20% 증가
    }
    
    /**
     * 최종 효과 값
     */
    public double getFinalPower() {
        return power * getAmplifierMultiplier();
    }
    
    /**
     * 효과 지속 시간 감소
     */
    public void tick() {
        if (duration > 0) {
            duration--;
        }
        
        if (duration <= 0) {
            isActive = false;
        }
    }
    
    /**
     * 효과 지속 시간 연장
     */
    public void extend(int ticks) {
        duration = Math.min(duration + ticks, maxDuration);
    }
    
    /**
     * 효과 강화
     */
    public void amplify() {
        if (amplifier < 5) {
            amplifier++;
        }
    }
    
    /**
     * 경과 시간 (초 단위)
     */
    public long getElapsedSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
    
    /**
     * 경과 시간 (분 단위)
     */
    public long getElapsedMinutes() {
        return getElapsedSeconds() / 60;
    }
    
    /**
     * 효과 설명 반환
     */
    public String getFormattedDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ");
        
        if (amplifier > 0) {
            sb.append("(레벨 ").append(amplifier + 1).append(") ");
        }
        
        sb.append("- ").append(duration).append("틱 남음");
        
        return sb.toString();
    }
    
    /**
     * 상태 효과 정보 출력
     */
    @Override
    public String toString() {
        return "StatusEffect{" +
                "effectId='" + effectId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", amplifier=" + amplifier +
                ", duration=" + duration +
                ", isActive=" + isActive +
                ", power=" + power +
                '}';
    }
}