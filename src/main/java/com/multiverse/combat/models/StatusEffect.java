package com. multiverse.combat.models;

import com.multiverse.combat. models.enums. StatusEffectType;
import java.util.UUID;

/**
 * 상태이상 데이터 클래스
 * 플레이어에게 적용된 상태이상 정보를 저장합니다.
 */
public class StatusEffect {
    
    private String effectId;
    private StatusEffectType type;
    private int level;
    
    private long duration;  // 밀리초
    private long startTime;
    private int tickInterval;  // 밀리초
    private int lastTickCount = 0;
    
    private UUID source;  // 시전자
    
    // 스택
    private boolean stackable;
    private int maxStacks;
    private int currentStacks;
    
    /**
     * StatusEffect 생성자
     * @param type 상태이상 타입
     * @param level 레벨
     * @param duration 지속 시간 (밀리초)
     */
    public StatusEffect(StatusEffectType type, int level, long duration) {
        this.type = type;
        this.level = Math.max(1, level);
        this.duration = duration;
        this.tickInterval = 500;  // 기본 500ms
        this.stackable = false;
        this.maxStacks = 1;
        this. currentStacks = 1;
        this.effectId = UUID.randomUUID(). toString();
    }
    
    // ===== Getter & Setter =====
    
    public String getEffectId() { return effectId; }
    public void setEffectId(String effectId) { this.effectId = effectId; }
    
    public StatusEffectType getType() { return type; }
    public void setType(StatusEffectType type) { this.type = type; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(1, level); }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this. startTime = startTime; }
    
    public int getTickInterval() { return tickInterval; }
    public void setTickInterval(int tickInterval) { this.tickInterval = tickInterval; }
    
    public int getLastTickCount() { return lastTickCount; }
    public void setLastTickCount(int lastTickCount) { this. lastTickCount = lastTickCount; }
    
    public UUID getSource() { return source; }
    public void setSource(UUID source) { this.source = source; }
    
    public boolean isStackable() { return stackable; }
    public void setStackable(boolean stackable) { this.stackable = stackable; }
    
    public int getMaxStacks() { return maxStacks; }
    public void setMaxStacks(int maxStacks) { this.maxStacks = maxStacks; }
    
    public int getCurrentStacks() { return currentStacks; }
    public void setCurrentStacks(int currentStacks) { this.currentStacks = Math.min(currentStacks, maxStacks); }
    
    /**
     * 스택 추가
     */
    public void addStack() {
        if (stackable && currentStacks < maxStacks) {
            currentStacks++;
        }
    }
}