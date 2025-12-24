package com.multiverse.pvp. enums;

public enum TitleCategory {

    KILLS("킬", "&c", "처치 수 달성으로 획득하는 칭호"),
    WINS("승리", "&a", "승리 횟수 달성으로 획득하는 칭호"),
    STREAK("스트릭", "&6", "연속 킬로 획득하는 칭호"),
    TIER("티어", "&b", "랭크 티어 달성으로 획득하는 칭호"),
    SEASONAL("시즌", "&d", "시즌 보상으로 획득하는 칭호"),
    SPECIAL("특별", "&e", "특별한 업적으로 획득하는 칭호"),
    EVENT("이벤트", "&5", "이벤트 참여로 획득하는 칭호"),
    ACHIEVEMENT("업적", "&9", "다양한 업적 달성으로 획득하는 칭호");

    private final String displayName;
    private final String color;
    private final String description;

    TitleCategory(String displayName, String color, String description) {
        this.displayName = displayName;
        this.color = color;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public String getFormattedName() {
        return color + displayName;
    }

    /**
     * 킬 기반 카테고리인지 확인
     */
    public boolean isKillBased() {
        return this == KILLS || this == STREAK;
    }

    /**
     * 전투 관련 카테고리인지 확인
     */
    public boolean isCombatRelated() {
        return this == KILLS || this == WINS || this == STREAK;
    }

    /**
     * 랭크 관련 카테고리인지 확인
     */
    public boolean isRankRelated() {
        return this == TIER || this == SEASONAL;
    }

    /**
     * 기간 한정 카테고리인지 확인
     */
    public boolean isTimeLimited() {
        return this == SEASONAL || this == EVENT;
    }

    /**
     * 영구 카테고리인지 확인
     */
    public boolean isPermanent() {
        return ! isTimeLimited();
    }

    /**
     * 자동 해금 카테고리인지 확인 (조건 충족 시 자동 해금)
     */
    public boolean isAutoUnlock() {
        return this == KILLS || this == WINS || this == STREAK || this == TIER;
    }

    /**
     * 수동 지급 카테고리인지 확인
     */
    public boolean isManualGrant() {
        return this == SPECIAL || this == EVENT;
    }

    /**
     * 카테고리별 정렬 순서
     */
    public int getSortOrder() {
        switch (this) {
            case KILLS: 
                return 1;
            case WINS:
                return 2;
            case STREAK: 
                return 3;
            case TIER:
                return 4;
            case SEASONAL:
                return 5;
            case ACHIEVEMENT:
                return 6;
            case SPECIAL:
                return 7;
            case EVENT: 
                return 8;
            default: 
                return 99;
        }
    }

    /**
     * 카테고리별 아이콘 (GUI용)
     */
    public org.bukkit.Material getIcon() {
        switch (this) {
            case KILLS:
                return org.bukkit. Material.DIAMOND_SWORD;
            case WINS:
                return org.bukkit. Material.GOLDEN_APPLE;
            case STREAK:
                return org.bukkit. Material.BLAZE_POWDER;
            case TIER:
                return org.bukkit. Material.NETHER_STAR;
            case SEASONAL:
                return org.bukkit.Material.CLOCK;
            case SPECIAL:
                return org.bukkit.Material.ENCHANTED_BOOK;
            case EVENT: 
                return org. bukkit.Material. FIREWORK_ROCKET;
            case ACHIEVEMENT:
                return org.bukkit.Material.GOLD_BLOCK;
            default: 
                return org. bukkit.Material. NAME_TAG;
        }
    }

    /**
     * 문자열로부터 TitleCategory 반환
     */
    public static TitleCategory fromString(String str) {
        if (str == null) {
            return KILLS;
        }
        
        try {
            return valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (TitleCategory category :  values()) {
                if (category.displayName.equalsIgnoreCase(str)) {
                    return category;
                }
            }
            return KILLS;
        }
    }
}