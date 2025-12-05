package com.multiverse.skill.data.enums;

/**
 * 생활 스킬 타입
 */
public enum LifeSkillType {
    MINING("채광", "§8", "광석과 광물을 채굴"),
    WOODCUTTING("벌목", "§6", "나무를 자르고 목재 수집"),
    FISHING("낚시", "§9", "물에서 생선과 아이템 획득"),
    HERBALISM("채초", "§2", "식물과 허브 수집"),
    SMITHING("대장장이", "§7", "금속으로 도구 제작"),
    ALCHEMY("연금술", "§d", "재료로 물약 제조"),
    COOKING("요리", "§e", "음식 재료로 요리"),
    ENCHANTING("마법부여", "§5", "아이템에 마법 부여");

    private final String displayName;
    private final String colorCode;
    private final String description;

    LifeSkillType(String displayName, String colorCode, String description) {
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
     * 채집형 생활 스킬 여부
     */
    public boolean isGathering() {
        return this == MINING || this == WOODCUTTING || this == FISHING || this == HERBALISM;
    }

    /**
     * 제작형 생활 스킬 여부
     */
    public boolean isCrafting() {
        return this == SMITHING || this == ALCHEMY || this == COOKING || this == ENCHANTING;
    }

    /**
     * 기본 경험치 (행동당)
     */
    public int getBaseExperience() {
        return switch (this) {
            case MINING -> 50;
            case WOODCUTTING -> 40;
            case FISHING -> 60;
            case HERBALISM -> 35;
            case SMITHING -> 100;
            case ALCHEMY -> 120;
            case COOKING -> 80;
            case ENCHANTING -> 150;
        };
    }

    /**
     * 최대 레벨
     */
    public int getMaxLevel() {
        return 100;
    }

    /**
     * 레벨별 보너스 (1레벨당)
     */
    public double getLevelBonus() {
        return 0.05; // 레벨당 5% 증가
    }
}