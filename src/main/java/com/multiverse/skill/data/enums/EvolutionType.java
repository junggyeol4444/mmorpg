package com.multiverse. skill.data.enums;

/**
 * 스킬 진화 타입
 */
public enum EvolutionType {
    ENHANCE("강화", "§b", "기존 스킬을 더 강력하게 강화"),
    MUTATE("변이", "§d", "완전히 다른 스킬로 변경"),
    MERGE("합성", "§e", "두 스킬을 하나로 합성");

    private final String displayName;
    private final String colorCode;
    private final String description;

    EvolutionType(String displayName, String colorCode, String description) {
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
     * 강화형 진화 여부
     */
    public boolean isEnhance() {
        return this == ENHANCE;
    }

    /**
     * 변이형 진화 여부
     */
    public boolean isMutate() {
        return this == MUTATE;
    }

    /**
     * 합성형 진화 여부
     */
    public boolean isMerge() {
        return this == MERGE;
    }

    /**
     * 진화 난이도 (1~5)
     */
    public int getDifficulty() {
        return switch (this) {
            case ENHANCE -> 2;
            case MUTATE -> 4;
            case MERGE -> 5;
        };
    }

    /**
     * 진화 필요 포인트
     */
    public int getRequiredPoints() {
        return switch (this) {
            case ENHANCE -> 5;
            case MUTATE -> 10;
            case MERGE -> 15;
        };
    }
}