package com.multiverse.skill.data.enums;

/**
 * 스킬 타입 (활성/수동)
 */
public enum SkillType {
    ACTIVE("활성", "플레이어가 직접 발동하는 스킬"),
    PASSIVE("수동", "자동으로 활성화되는 스킬"),
    TOGGLE("토글", "ON/OFF를 전환할 수 있는 스킬");

    private final String displayName;
    private final String description;

    SkillType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 활성 스킬 여부 확인
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 수동 스킬 여부 확인
     */
    public boolean isPassive() {
        return this == PASSIVE;
    }

    /**
     * 토글 스킬 여부 확인
     */
    public boolean isToggle() {
        return this == TOGGLE;
    }
}