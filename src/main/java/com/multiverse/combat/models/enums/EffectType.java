package com.multiverse.combat. models.enums;

/**
 * 효과 타입 열거형
 * 스킬이 가질 수 있는 효과 종류를 정의합니다.
 */
public enum EffectType {
    /**
     * 데미지 - 적에게 데미지 입히기
     */
    DAMAGE("데미지"),
    
    /**
     * 치유 - 자신 또는 아군 회복
     */
    HEAL("치유"),
    
    /**
     * 버프 - 자신 또는 아군 강화
     */
    BUFF("버프"),
    
    /**
     * 디버프 - 적 약화
     */
    DEBUFF("디버프"),
    
    /**
     * 텔레포트 - 순간이동
     */
    TELEPORT("텔레포트"),
    
    /**
     * 소환 - 소환물 생성
     */
    SUMMON("소환"),
    
    /**
     * 군중 제어 - CC 효과
     */
    CROWD_CONTROL("군중제어");
    
    private final String displayName;
    
    /**
     * EffectType 생성자
     * @param displayName 표시할 이름
     */
    EffectType(String displayName) {
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
     * 문자열로부터 EffectType 찾기
     * @param name 찾을 이름
     * @return 해당하는 EffectType, 없으면 null
     */
    public static EffectType fromString(String name) {
        if (name == null) return null;
        try {
            return EffectType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}