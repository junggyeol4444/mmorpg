package com.multiverse.item. data;

public class SetBonus {
    
    private int requiredCount; // 필요한 세트 아이템 개수
    private String description; // 설명
    private SetEffect effect; // 세트 효과
    private double activationRate; // 발동 확률 (%)
    private int cooldown; // 쿨타임 (틱)
    
    /**
     * 기본 생성자
     */
    public SetBonus() {
        this.activationRate = 100.0;
        this.cooldown = 0;
    }
    
    /**
     * 필수 파라미터가 있는 생성자
     */
    public SetBonus(int requiredCount, String description) {
        this.requiredCount = requiredCount;
        this.description = description;
        this.activationRate = 100. 0;
        this.cooldown = 0;
    }
    
    /**
     * 모든 파라미터가 있는 생성자
     */
    public SetBonus(int requiredCount, String description, SetEffect effect, double activationRate, int cooldown) {
        this.requiredCount = requiredCount;
        this.description = description;
        this.effect = effect;
        this.activationRate = Math.max(0, Math.min(activationRate, 100. 0));
        this.cooldown = Math.max(0, cooldown);
    }
    
    // Getters and Setters
    public int getRequiredCount() {
        return requiredCount;
    }
    
    public void setRequiredCount(int requiredCount) {
        this.requiredCount = Math.max(0, requiredCount);
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public SetEffect getEffect() {
        return effect;
    }
    
    public void setEffect(SetEffect effect) {
        this.effect = effect;
    }
    
    public double getActivationRate() {
        return activationRate;
    }
    
    public void setActivationRate(double activationRate) {
        this.activationRate = Math.max(0, Math.min(activationRate, 100.0));
    }
    
    public int getCooldown() {
        return cooldown;
    }
    
    public void setCooldown(int cooldown) {
        this.cooldown = Math.max(0, cooldown);
    }
    
    /**
     * 발동 확률을 기반으로 발동 여부 결정
     */
    public boolean shouldActivate() {
        return Math.random() * 100 < activationRate;
    }
    
    /**
     * 세트 보너스 활성화 여부 확인
     */
    public boolean isActive(int equippedCount) {
        return equippedCount >= requiredCount;
    }
    
    /**
     * 보너스 강도 반환 (requiredCount 기반)
     */
    public double getBonusStrength() {
        return requiredCount * 0.1; // 필요 개수당 10% 강화
    }
    
    /**
     * 세트 보너스 정보 출력
     */
    @Override
    public String toString() {
        return "SetBonus{" +
                "requiredCount=" + requiredCount +
                ", description='" + description + '\'' +
                ", activationRate=" + activationRate +
                ", cooldown=" + cooldown +
                '}';
    }
}