package com.multiverse.pvp. enums;

public enum ArenaType {

    DUEL_1V1("1v1 듀얼", "&e", 2, 2, 1, 300, "1대1 결투 아레나"),
    TEAM_DEATHMATCH("팀 데스매치", "&c", 6, 10, 5, 600, "팀 간 킬 수 경쟁"),
    BATTLE_ROYALE("배틀로얄", "&6", 10, 50, 1, 900, "최후의 1인이 승리"),
    CAPTURE_POINT("점령전", "&b", 6, 12, 6, 600, "거점을 점령하여 승리"),
    KING_OF_HILL("언덕의 왕", "&d", 4, 16, 1, 480, "중앙 지점을 점령하여 승리");

    private final String displayName;
    private final String color;
    private final int minPlayers;
    private final int maxPlayers;
    private final int defaultTeamSize;
    private final int defaultDuration;
    private final String description;

    ArenaType(String displayName, String color, int minPlayers, int maxPlayers, 
              int defaultTeamSize, int defaultDuration, String description) {
        this.displayName = displayName;
        this.color = color;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.defaultTeamSize = defaultTeamSize;
        this.defaultDuration = defaultDuration;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getDefaultTeamSize() {
        return defaultTeamSize;
    }

    public int getDefaultDuration() {
        return defaultDuration;
    }

    public String getDescription() {
        return description;
    }

    public String getFormattedName() {
        return color + displayName;
    }

    /**
     * 팀전 여부
     */
    public boolean isTeamBased() {
        return this == TEAM_DEATHMATCH || this == CAPTURE_POINT;
    }

    /**
     * FFA (Free For All) 여부
     */
    public boolean isFreeForAll() {
        return this == BATTLE_ROYALE || this == KING_OF_HILL;
    }

    /**
     * 1대1 여부
     */
    public boolean isDuel() {
        return this == DUEL_1V1;
    }

    /**
     * 점령 기반 여부
     */
    public boolean isCaptureBased() {
        return this == CAPTURE_POINT || this == KING_OF_HILL;
    }

    /**
     * 생존 기반 여부 (마지막 1인/팀 승리)
     */
    public boolean isSurvivalBased() {
        return this == BATTLE_ROYALE;
    }

    /**
     * 킬 기반 승리 여부
     */
    public boolean isKillBased() {
        return this == TEAM_DEATHMATCH || this == DUEL_1V1;
    }

    /**
     * 보상 배율
     */
    public double getRewardMultiplier() {
        switch (this) {
            case DUEL_1V1:
                return 1.0;
            case TEAM_DEATHMATCH: 
                return 1.5;
            case BATTLE_ROYALE: 
                return 2.0;
            case CAPTURE_POINT:
                return 1.5;
            case KING_OF_HILL:
                return 1.3;
            default: 
                return 1.0;
        }
    }

    /**
     * 레이팅 변동 배율
     */
    public double getRatingMultiplier() {
        switch (this) {
            case DUEL_1V1:
                return 1.0;
            case TEAM_DEATHMATCH:
                return 0.7;
            case BATTLE_ROYALE:
                return 1.2;
            case CAPTURE_POINT: 
                return 0.7;
            case KING_OF_HILL:
                return 0.8;
            default: 
                return 1.0;
        }
    }

    /**
     * 필요한 팀 수
     */
    public int getRequiredTeams() {
        switch (this) {
            case DUEL_1V1:
                return 2;
            case TEAM_DEATHMATCH:
                return 2;
            case BATTLE_ROYALE: 
                return 0; // FFA
            case CAPTURE_POINT:
                return 2;
            case KING_OF_HILL:
                return 0; // FFA
            default:
                return 2;
        }
    }

    /**
     * 문자열로부터 ArenaType 반환
     */
    public static ArenaType fromString(String str) {
        if (str == null) {
            return DUEL_1V1;
        }
        
        try {
            return valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (ArenaType type : values()) {
                if (type. displayName.equalsIgnoreCase(str)) {
                    return type;
                }
            }
            return DUEL_1V1;
        }
    }

    /**
     * 아레나 타입별 아이콘 (GUI용)
     */
    public org.bukkit.Material getIcon() {
        switch (this) {
            case DUEL_1V1:
                return org. bukkit.Material. IRON_SWORD;
            case TEAM_DEATHMATCH:
                return org.bukkit.Material.DIAMOND_SWORD;
            case BATTLE_ROYALE:
                return org.bukkit.Material.GOLDEN_APPLE;
            case CAPTURE_POINT:
                return org.bukkit.Material.BEACON;
            case KING_OF_HILL:
                return org.bukkit. Material.GOLD_BLOCK;
            default: 
                return org. bukkit.Material. IRON_SWORD;
        }
    }
}