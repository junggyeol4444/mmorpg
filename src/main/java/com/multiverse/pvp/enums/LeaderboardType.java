package com.multiverse.pvp. enums;

public enum LeaderboardType {

    KILLS("킬", "&c", "총 처치 수 기준 순위"),
    STREAK("스트릭", "&6", "최고 연속 킬 기준 순위"),
    WINS("승리", "&a", "총 승리 수 기준 순위"),
    RATING("레이팅", "&b", "레이팅 점수 기준 순위"),
    KDA("KDA", "&e", "KDA 비율 기준 순위"),
    WIN_RATE("승률", "&d", "승률 기준 순위"),
    DAMAGE("데미지", "&4", "총 데미지 기준 순위"),
    PLAYTIME("플레이타임", "&7", "PvP 플레이 시간 기준 순위");

    private final String displayName;
    private final String color;
    private final String description;

    LeaderboardType(String displayName, String color, String description) {
        this. displayName = displayName;
        this. color = color;
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
     * 숫자 값인지 확인 (높을수록 좋음)
     */
    public boolean isNumericValue() {
        return this == KILLS || this == STREAK || this == WINS || 
               this == RATING || this == DAMAGE || this == PLAYTIME;
    }

    /**
     * 비율 값인지 확인
     */
    public boolean isRatioValue() {
        return this == KDA || this == WIN_RATE;
    }

    /**
     * 시간 값인지 확인
     */
    public boolean isTimeValue() {
        return this == PLAYTIME;
    }

    /**
     * 정렬 방향 (true = 내림차순, false = 오름차순)
     */
    public boolean isDescending() {
        return true; // 모든 리더보드는 높은 값이 상위
    }

    /**
     * 값 포맷팅
     */
    public String formatValue(double value) {
        switch (this) {
            case KILLS: 
            case STREAK: 
            case WINS: 
            case RATING: 
            case DAMAGE: 
                return String.format("%,d", (int) value);
            case KDA:
                return String.format("%. 2f", value);
            case WIN_RATE:
                return String.format("%.1f%%", value);
            case PLAYTIME:
                long seconds = (long) value / 1000;
                long hours = seconds / 3600;
                long minutes = (seconds % 3600) / 60;
                return String.format("%d시간 %d분", hours, minutes);
            default:
                return String.valueOf((int) value);
        }
    }

    /**
     * 일일 리더보드 지원 여부
     */
    public boolean supportsDailyLeaderboard() {
        return this == KILLS || this == STREAK || this == WINS;
    }

    /**
     * 주간 리더보드 지원 여부
     */
    public boolean supportsWeeklyLeaderboard() {
        return this == KILLS || this == STREAK || this == WINS || this == RATING;
    }

    /**
     * 시즌 리더보드 지원 여부
     */
    public boolean supportsSeasonLeaderboard() {
        return true; // 모든 타입 지원
    }

    /**
     * 최소 게임 수 요구 여부 (비율 기반 리더보드)
     */
    public int getMinimumGamesRequired() {
        switch (this) {
            case KDA:
            case WIN_RATE:
                return 10; // 최소 10게임
            default:
                return 0;
        }
    }

    /**
     * 리더보드 타입별 아이콘 (GUI용)
     */
    public org.bukkit. Material getIcon() {
        switch (this) {
            case KILLS:
                return org.bukkit.Material.DIAMOND_SWORD;
            case STREAK: 
                return org. bukkit.Material. BLAZE_POWDER;
            case WINS:
                return org.bukkit.Material.GOLDEN_APPLE;
            case RATING: 
                return org. bukkit.Material. NETHER_STAR;
            case KDA:
                return org.bukkit.Material.GOLDEN_SWORD;
            case WIN_RATE: 
                return org.bukkit.Material. EMERALD;
            case DAMAGE:
                return org.bukkit.Material.TNT;
            case PLAYTIME: 
                return org.bukkit.Material. CLOCK;
            default: 
                return org. bukkit.Material. PAPER;
        }
    }

    /**
     * 보상 지급 대상 여부
     */
    public boolean givesRewards() {
        return this == KILLS || this == WINS || this == RATING;
    }

    /**
     * 기본 리더보드 타입인지 확인
     */
    public boolean isPrimary() {
        return this == RATING;
    }

    /**
     * 문자열로부터 LeaderboardType 반환
     */
    public static LeaderboardType fromString(String str) {
        if (str == null) {
            return RATING;
        }
        
        try {
            return valueOf(str.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            for (LeaderboardType type : values()) {
                if (type. displayName.equalsIgnoreCase(str)) {
                    return type;
                }
            }
            return RATING;
        }
    }

    /**
     * 정렬을 위한 비교자 반환
     */
    public java.util.Comparator<com.multiverse.pvp.data.PvPRanking> getComparator() {
        switch (this) {
            case KILLS:
                return (a, b) -> Integer.compare(b. getKills(), a.getKills());
            case STREAK:
                return (a, b) -> Integer.compare(b.getWinStreak(), a.getWinStreak());
            case WINS:
                return (a, b) -> Integer.compare(b.getWins(), a.getWins());
            case RATING:
                return (a, b) -> Integer.compare(b.getRating(), a.getRating());
            case KDA:
                return (a, b) -> Double.compare(b.getKDA(), a.getKDA());
            case WIN_RATE: 
                return (a, b) -> Double.compare(b. getWinRate(), a.getWinRate());
            default:
                return (a, b) -> Integer.compare(b.getRating(), a.getRating());
        }
    }
}