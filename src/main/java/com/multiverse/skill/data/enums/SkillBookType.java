package com.multiverse.skill. data.enums;

/**
 * 스킬 북 타입
 */
public enum SkillBookType {
    LEARN("습득", "§a", "새로운 스킬을 습득"),
    UPGRADE("업그레이드", "§b", "스킬 레벨을 상승"),
    RESET("초기화", "§c", "스킬을 초기화 (포인트 환급)");

    private final String displayName;
    private final String colorCode;
    private final String description;

    SkillBookType(String displayName, String colorCode, String description) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 색상이 적용된 이름
     */
    public String getColoredName() {
        return colorCode + "§l" + displayName;
    }

    /**
     * 스킬 습득 필요 북인지 확인
     */
    public boolean isLearnBook() {
        return this == LEARN;
    }

    /**
     * 스킬 업그레이드 필요 북인지 확인
     */
    public boolean isUpgradeBook() {
        return this == UPGRADE;
    }

    /**
     * 리셋 북인지 확인
     */
    public boolean isResetBook() {
        return this == RESET;
    }

    /**
     * 소비형 북인지 확인 (사용하면 사라짐)
     */
    public boolean isConsumable() {
        return true; // 모든 스킬북은 일회용 (귀속 제외)
    }
}