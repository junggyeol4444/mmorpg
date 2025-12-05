package com.multiverse.item. data;

public enum EffectType {
    // 스탯 관련
    STAT_INCREASE("스탯 증가", "기본 스탯을 증가시킵니다. "),
    STAT_BUFF("스탯 버프", "일시적으로 스탯을 증가시킵니다."),
    
    // 피해 관련
    DAMAGE_INCREASE("피해 증가", "피해를 증가시킵니다."),
    DAMAGE_BUFF("피해 버프", "일시적으로 피해를 증가시킵니다."),
    SPLASH_DAMAGE("범위 피해", "주변 적에게 피해를 입힙니다."),
    
    // 방어 관련
    DEFENSE_INCREASE("방어력 증가", "방어력을 증가시킵니다."),
    DEFENSE_BUFF("방어력 버프", "일시적으로 방어력을 증가시킵니다. "),
    DAMAGE_REDUCTION("피해 감소", "받는 피해를 감소시킵니다."),
    SHIELD("보호막", "일시적인 보호막을 생성합니다."),
    
    // 치유 관련
    HEAL("치유", "체력을 회복합니다."),
    REGEN("재생", "지속적으로 체력을 회복합니다."),
    
    // 특수 효과
    SPEED_INCREASE("이동속도 증가", "이동 속도를 증가시킵니다."),
    SLOW("둔화", "대상의 이동 속도를 감소시킵니다."),
    STUN("기절", "대상을 일시적으로 기절시킵니다."),
    FREEZE("빙결", "대상을 움직이지 못하게 합니다."),
    BURN("화상", "지속적인 피해를 입힙니다."),
    POISON("중독", "독 피해를 입힙니다."),
    BLIND("실명", "시야를 제한합니다."),
    
    // 특수 능력
    LIFESTEAL("생명력 흡수", "피해의 일부를 생명력으로 흡수합니다."),
    REFLECT("반사", "받는 피해의 일부를 반사합니다."),
    TELEPORT("순간이동", "대상 위치로 순간이동합니다."),
    SUMMON("소환", "아군을 소환합니다.");
    
    private String name;
    private String description;
    
    EffectType(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    /**
     * 이름 반환
     */
    public String getEffectName() {
        return name;
    }
    
    /**
     * 설명 반환
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 긍정 효과인지 확인
     */
    public boolean isPositive() {
        return this != SLOW && this != STUN && this != FREEZE && 
               this != BURN && this != POISON && this != BLIND;
    }
    
    /**
     * 부정 효과인지 확인
     */
    public boolean isNegative() {
        return ! isPositive();
    }
    
    /**
     * 지속 효과인지 확인
     */
    public boolean isDuration() {
        return this == STAT_BUFF || this == DAMAGE_BUFF || this == DEFENSE_BUFF ||
               this == REGEN || this == SLOW || this == BURN || this == POISON ||
               this == BLIND || this == FREEZE;
    }
    
    /**
     * 즉시 효과인지 확인
     */
    public boolean isInstant() {
        return ! isDuration();
    }
    
    /**
     * 효과 카테고리
     */
    public String getCategory() {
        if (this. name.contains("증가") || this.name.contains("버프")) {
            return "BUFF";
        } else if (this.name.contains("감소") || this.name.contains("약화") || this.name.contains("기절") || this.name.contains("빙결")) {
            return "DEBUFF";
        } else if (this == HEAL || this == REGEN) {
            return "HEAL";
        } else if (this == DAMAGE_INCREASE || this == DAMAGE_BUFF || this == SPLASH_DAMAGE) {
            return "DAMAGE";
        } else {
            return "SPECIAL";
        }
    }
}