package com.multiverse.pvp. enums;

public enum ZoneType {

    SAFE("안전 지역", "&a", false, 0.0, 1.0, 0.0),
    COMBAT("전투 지역", "&e", true, 1.5, 1.5, 1.0),
    CHAOS("혼돈 지역", "&c", true, 3.0, 2.0, 2.0);

    private final String displayName;
    private final String color;
    private final boolean pvpEnabled;
    private final double defaultRewardMultiplier;
    private final double defaultExpMultiplier;
    private final double defaultDeathPenalty;

    ZoneType(String displayName, String color, boolean pvpEnabled,
             double defaultRewardMultiplier, double defaultExpMultiplier, double defaultDeathPenalty) {
        this.displayName = displayName;
        this.color = color;
        this.pvpEnabled = pvpEnabled;
        this.defaultRewardMultiplier = defaultRewardMultiplier;
        this.defaultExpMultiplier = defaultExpMultiplier;
        this.defaultDeathPenalty = defaultDeathPenalty;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }

    public double getDefaultRewardMultiplier() {
        return defaultRewardMultiplier;
    }

    public double getDefaultExpMultiplier() {
        return defaultExpMultiplier;
    }

    public double getDefaultDeathPenalty() {
        return defaultDeathPenalty;
    }

    public String getFormattedName() {
        return color + displayName;
    }

    /**
     * 안전 지역인지 확인
     */
    public boolean isSafe() {
        return this == SAFE;
    }

    /**
     * 전투 지역인지 확인
     */
    public boolean isCombat() {
        return this == COMBAT;
    }

    /**
     * 혼돈 지역인지 확인
     */
    public boolean isChaos() {
        return this == CHAOS;
    }

    /**
     * 위험 지역인지 확인
     */
    public boolean isDangerous() {
        return this == COMBAT || this == CHAOS;
    }

    /**
     * 아이템 드롭 여부
     */
    public boolean dropsItems() {
        return this != SAFE;
    }

    /**
     * 비행 허용 여부
     */
    public boolean allowsFlight() {
        return this == SAFE;
    }

    /**
     * 텔레포트 허용 여부
     */
    public boolean allowsTeleport() {
        return this == SAFE;
    }

    /**
     * 인벤토리 유지 여부
     */
    public boolean keepsInventory() {
        return this == SAFE;
    }

    /**
     * 기본 입장 메시지
     */
    public String getDefaultEnterMessage() {
        switch (this) {
            case SAFE: 
                return "&a[안전 지역] &f이곳은 안전 지역입니다.  PvP가 비활성화됩니다.";
            case COMBAT: 
                return "&e[전투 지역] &f이곳은 전투 지역입니다. PvP에 주의하세요! ";
            case CHAOS:
                return "&c[혼돈 지역] &f이곳은 혼돈 지역입니다. 모든 위험을 감수하세요!";
            default:
                return "&7지역에 입장했습니다. ";
        }
    }

    /**
     * 기본 퇴장 메시지
     */
    public String getDefaultLeaveMessage() {
        switch (this) {
            case SAFE:
                return "&a[안전 지역] &f안전 지역을 벗어났습니다. ";
            case COMBAT:
                return "&e[전투 지역] &f전투 지역을 벗어났습니다.";
            case CHAOS:
                return "&c[혼돈 지역] &f혼돈 지역을 벗어났습니다.";
            default:
                return "&7지역을 벗어났습니다.";
        }
    }

    /**
     * 위험 레벨 (0-10)
     */
    public int getDangerLevel() {
        switch (this) {
            case SAFE: 
                return 0;
            case COMBAT:
                return 5;
            case CHAOS:
                return 10;
            default:
                return 0;
        }
    }

    /**
     * 존 타입별 아이콘 (GUI용)
     */
    public org.bukkit. Material getIcon() {
        switch (this) {
            case SAFE:
                return org.bukkit.Material.EMERALD_BLOCK;
            case COMBAT:
                return org.bukkit.Material.GOLD_BLOCK;
            case CHAOS: 
                return org. bukkit.Material. REDSTONE_BLOCK;
            default: 
                return org.bukkit.Material. STONE;
        }
    }

    /**
     * 보스바 색상
     */
    public org.bukkit.boss. BarColor getBarColor() {
        switch (this) {
            case SAFE: 
                return org. bukkit.boss. BarColor.GREEN;
            case COMBAT:
                return org.bukkit. boss.BarColor.YELLOW;
            case CHAOS:
                return org.bukkit. boss.BarColor.RED;
            default:
                return org.bukkit. boss.BarColor.WHITE;
        }
    }

    /**
     * 문자열로부터 ZoneType 반환
     */
    public static ZoneType fromString(String str) {
        if (str == null) {
            return SAFE;
        }
        
        try {
            return valueOf(str. toUpperCase());
        } catch (IllegalArgumentException e) {
            for (ZoneType type : values()) {
                if (type.displayName.equalsIgnoreCase(str)) {
                    return type;
                }
            }
            return SAFE;
        }
    }
}