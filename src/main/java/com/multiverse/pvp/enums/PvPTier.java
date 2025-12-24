package com. multiverse.pvp.enums;

public enum PvPTier {

    BRONZE(0, 999, "&7", "브론즈", "Bronze", 1),
    SILVER(1000, 1499, "&f", "실버", "Silver", 2),
    GOLD(1500, 1999, "&6", "골드", "Gold", 3),
    PLATINUM(2000, 2499, "&b", "플래티넘", "Platinum", 4),
    DIAMOND(2500, 2999, "&9", "다이아몬드", "Diamond", 5),
    MASTER(3000, 9999, "&d", "마스터", "Master", 6);

    private final int minRating;
    private final int maxRating;
    private final String color;
    private final String displayName;
    private final String englishName;
    private final int level;

    PvPTier(int minRating, int maxRating, String color, String displayName, String englishName, int level) {
        this.minRating = minRating;
        this. maxRating = maxRating;
        this.color = color;
        this. displayName = displayName;
        this. englishName = englishName;
        this.level = level;
    }

    public int getMinRating() {
        return minRating;
    }

    public int getMaxRating() {
        return maxRating;
    }

    public String getColor() {
        return color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public int getLevel() {
        return level;
    }

    public String getFormattedName() {
        return color + displayName;
    }

    public String getFormattedEnglishName() {
        return color + englishName;
    }

    /**
     * 레이팅으로 티어 조회
     */
    public static PvPTier getTier(int rating) {
        for (PvPTier tier : values()) {
            if (rating >= tier.minRating && rating <= tier.maxRating) {
                return tier;
            }
        }
        return BRONZE;
    }

    /**
     * 다음 티어 반환
     */
    public PvPTier getNextTier() {
        switch (this) {
            case BRONZE: 
                return SILVER;
            case SILVER:
                return GOLD;
            case GOLD: 
                return PLATINUM;
            case PLATINUM:
                return DIAMOND;
            case DIAMOND:
                return MASTER;
            case MASTER: 
                return null; // 최고 티어
            default:
                return null;
        }
    }

    /**
     * 이전 티어 반환
     */
    public PvPTier getPreviousTier() {
        switch (this) {
            case BRONZE:
                return null; // 최저 티어
            case SILVER: 
                return BRONZE;
            case GOLD:
                return SILVER;
            case PLATINUM:
                return GOLD;
            case DIAMOND: 
                return PLATINUM;
            case MASTER:
                return DIAMOND;
            default:
                return null;
        }
    }

    /**
     * 티어 보상 배율
     */
    public double getRewardMultiplier() {
        switch (this) {
            case BRONZE: 
                return 1.0;
            case SILVER:
                return 1.1;
            case GOLD:
                return 1.2;
            case PLATINUM:
                return 1.3;
            case DIAMOND: 
                return 1.5;
            case MASTER:
                return 2.0;
            default:
                return 1.0;
        }
    }

    /**
     * 시즌 보상 배율
     */
    public double getSeasonRewardMultiplier() {
        switch (this) {
            case BRONZE: 
                return 1.0;
            case SILVER:
                return 1.5;
            case GOLD:
                return 2.0;
            case PLATINUM:
                return 3.0;
            case DIAMOND: 
                return 5.0;
            case MASTER: 
                return 10.0;
            default:
                return 1.0;
        }
    }

    /**
     * 최고 티어인지 확인
     */
    public boolean isMaxTier() {
        return this == MASTER;
    }

    /**
     * 최저 티어인지 확인
     */
    public boolean isMinTier() {
        return this == BRONZE;
    }

    /**
     * 다른 티어보다 높은지 확인
     */
    public boolean isHigherThan(PvPTier other) {
        return this. level > other.level;
    }

    /**
     * 다른 티어보다 낮은지 확인
     */
    public boolean isLowerThan(PvPTier other) {
        return this.level < other.level;
    }

    /**
     * 티어 진행률 계산 (0.0 ~ 1.0)
     */
    public double getProgress(int rating) {
        if (rating < minRating) {
            return 0.0;
        }
        if (rating > maxRating) {
            return 1.0;
        }
        
        int range = maxRating - minRating;
        if (range <= 0) {
            return 1.0;
        }
        
        return (double) (rating - minRating) / range;
    }

    /**
     * 다음 티어까지 필요한 레이팅
     */
    public int getRatingToNext(int currentRating) {
        PvPTier next = getNextTier();
        if (next == null) {
            return 0;
        }
        return Math.max(0, next.minRating - currentRating);
    }

    /**
     * 강등까지 남은 레이팅
     */
    public int getRatingToDemotion(int currentRating) {
        if (this == BRONZE) {
            return currentRating; // 브론즈 아래는 없음
        }
        return Math.max(0, currentRating - minRating);
    }

    /**
     * 티어별 아이콘 (GUI용)
     */
    public org.bukkit.Material getIcon() {
        switch (this) {
            case BRONZE: 
                return org. bukkit.Material. BRICK;
            case SILVER:
                return org.bukkit. Material.IRON_INGOT;
            case GOLD:
                return org.bukkit.Material.GOLD_INGOT;
            case PLATINUM:
                return org.bukkit.Material.DIAMOND;
            case DIAMOND:
                return org.bukkit. Material.DIAMOND_BLOCK;
            case MASTER:
                return org.bukkit. Material.NETHER_STAR;
            default: 
                return org. bukkit.Material. STONE;
        }
    }

    /**
     * 문자열로부터 PvPTier 반환
     */
    public static PvPTier fromString(String str) {
        if (str == null) {
            return BRONZE;
        }
        
        try {
            return valueOf(str. toUpperCase());
        } catch (IllegalArgumentException e) {
            for (PvPTier tier : values()) {
                if (tier. displayName.equalsIgnoreCase(str) || tier.englishName. equalsIgnoreCase(str)) {
                    return tier;
                }
            }
            return BRONZE;
        }
    }

    /**
     * 레벨로부터 PvPTier 반환
     */
    public static PvPTier fromLevel(int level) {
        for (PvPTier tier : values()) {
            if (tier.level == level) {
                return tier;
            }
        }
        return BRONZE;
    }
}