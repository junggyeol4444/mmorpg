package com.multiverse.combat.models.enums;

/**
 * 데미지 타입 열거형
 * 데미지의 종류와 감소 방식을 정의합니다.
 */
public enum DamageType {
    /**
     * 물리 데미지 - 방어력으로 감소
     */
    PHYSICAL("물리", true, true),
    
    /**
     * 마법 데미지 - 마법 저항으로 감소
     */
    MAGICAL("마법", true, true),
    
    /**
     * 참 데미지 - 방어 무시
     */
    TRUE_DAMAGE("참", false, true),
    
    /**
     * 순수 데미지 - 모든 감소 무시
     */
    PURE_DAMAGE("순수", false, false);
    
    private final String displayName;
    private final boolean reducible;      // 방어/저항으로 감소 가능
    private final boolean canBuff;        // 버프로 증가 가능
    
    /**
     * DamageType 생성자
     * @param displayName 표시할 이름
     * @param reducible 감소 가능 여부
     * @param canBuff 버프 가능 여부
     */
    DamageType(String displayName, boolean reducible, boolean canBuff) {
        this.displayName = displayName;
        this.reducible = reducible;
        this. canBuff = canBuff;
    }
    
    /**
     * 표시 이름 반환
     * @return 표시 이름
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 감소 가능 여부
     * @return 감소 가능하면 true
     */
    public boolean isReducible() {
        return reducible;
    }
    
    /**
     * 버프 가능 여부
     * @return 버프 가능하면 true
     */
    public boolean canBeBuffed() {
        return canBuff;
    }
    
    /**
     * 문자열로부터 DamageType 찾기
     * @param name 찾을 이름
     * @return 해당하는 DamageType, 없으면 null
     */
    public static DamageType fromString(String name) {
        if (name == null) return null;
        try {
            return DamageType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}