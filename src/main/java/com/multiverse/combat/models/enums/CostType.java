package com.multiverse.combat.models. enums;

/**
 * 스킬 비용 타입 열거형
 * 스킬 사용 시 소모되는 자원을 정의합니다.
 */
public enum CostType {
    /**
     * 마나 - 마법 사용자용
     */
    MANA("마나"),
    
    /**
     * 기력 - 체력 기반 자원
     */
    QI("기력"),
    
    /**
     * 스태미나 - 음식 레벨로 관리
     */
    STAMINA("스태미나"),
    
    /**
     * 생명력 - HP 직접 소모
     */
    HP("생명력"),
    
    /**
     * 없음 - 무료 스킬
     */
    NONE("무료");
    
    private final String displayName;
    
    /**
     * CostType 생성자
     * @param displayName 표시할 이름
     */
    CostType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * 표시 이름 반환
     * @return 표시 이름
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 문자열로부터 CostType 찾기
     * @param name 찾을 이름
     * @return 해당하는 CostType, 없으면 NONE
     */
    public static CostType fromString(String name) {
        if (name == null) return NONE;
        try {
            return CostType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}