package com.multiverse.combat.models. enums;

/**
 * 스킬 카테고리 열거형
 * 스킬의 용도별 분류를 정의합니다.
 */
public enum SkillCategory {
    /**
     * 전투 - 공격 스킬
     */
    COMBAT("전투"),
    
    /**
     * 방어 - 방어 및 회피 스킬
     */
    DEFENSE("방어"),
    
    /**
     * 유틸리티 - 보조 기능 스킬
     */
    UTILITY("유틸리티"),
    
    /**
     * 이동 - 이동 관련 스킬
     */
    MOVEMENT("이동"),
    
    /**
     * 군중 제어 - CC 스킬
     */
    CROWD_CONTROL("군중제어");
    
    private final String displayName;
    
    /**
     * SkillCategory 생성자
     * @param displayName 표시할 이름
     */
    SkillCategory(String displayName) {
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
     * 문자열로부터 SkillCategory 찾기
     * @param name 찾을 이름
     * @return 해당하는 SkillCategory, 없으면 null
     */
    public static SkillCategory fromString(String name) {
        if (name == null) return null;
        try {
            return SkillCategory.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}