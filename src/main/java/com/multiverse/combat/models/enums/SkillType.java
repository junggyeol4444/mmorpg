package com.multiverse.combat. models. enums;

/**
 * 스킬 타입 열거형
 * 스킬의 기본 분류를 정의합니다.
 */
public enum SkillType {
    /**
     * 활성 스킬 - 플레이어가 직접 발동하는 스킬
     */
    ACTIVE("활성"),
    
    /**
     * 패시브 스킬 - 항상 적용되는 스킬
     */
    PASSIVE("패시브"),
    
    /**
     * 버프 스킬 - 자신 또는 파티원 강화
     */
    BUFF("버프"),
    
    /**
     * 디버프 스킬 - 적 약화
     */
    DEBUFF("디버프"),
    
    /**
     * 소환 스킬 - 소환물 생성
     */
    SUMMON("소환");
    
    private final String displayName;
    
    /**
     * SkillType 생성자
     * @param displayName 표시할 이름
     */
    SkillType(String displayName) {
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
     * 문자열로부터 SkillType 찾기
     * @param name 찾을 이름
     * @return 해당하는 SkillType, 없으면 null
     */
    public static SkillType fromString(String name) {
        if (name == null) return null;
        try {
            return SkillType.valueOf(name. toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}