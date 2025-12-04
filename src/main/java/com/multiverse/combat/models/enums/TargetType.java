package com.multiverse.combat. models.enums;

/**
 * 대상 타입 열거형
 * 스킬의 효과 범위를 정의합니다.
 */
public enum TargetType {
    /**
     * 자신 - 자신에게만 적용
     */
    SELF("자신"),
    
    /**
     * 단일 대상 - 지정된 한 엔티티
     */
    TARGET("단일대상"),
    
    /**
     * 범위 - 원형 범위 내 모든 엔티티
     */
    AOE("범위"),
    
    /**
     * 원뿔 - 원뿔 모양 범위
     */
    CONE("원뿔"),
    
    /**
     * 직선 - 직선 방향 범위
     */
    LINE("직선");
    
    private final String displayName;
    
    /**
     * TargetType 생성자
     * @param displayName 표시할 이름
     */
    TargetType(String displayName) {
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
     * 문자열로부터 TargetType 찾기
     * @param name 찾을 이름
     * @return 해당하는 TargetType, 없으면 TARGET
     */
    public static TargetType fromString(String name) {
        if (name == null) return TARGET;
        try {
            return TargetType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TARGET;
        }
    }
}