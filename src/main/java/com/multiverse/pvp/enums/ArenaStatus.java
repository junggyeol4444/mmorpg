package com.multiverse.pvp.enums;

public enum ArenaStatus {

    WAITING("대기 중", "&a", true, false),
    PREPARING("준비 중", "&e", false, false),
    ACTIVE("진행 중", "&c", false, true),
    ENDING("종료 중", "&6", false, false),
    MAINTENANCE("점검 중", "&7", false, false);

    private final String displayName;
    private final String color;
    private final boolean canJoin;
    private final boolean canFight;

    ArenaStatus(String displayName, String color, boolean canJoin, boolean canFight) {
        this.displayName = displayName;
        this.color = color;
        this.canJoin = canJoin;
        this.canFight = canFight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public boolean canJoin() {
        return canJoin;
    }

    public boolean canFight() {
        return canFight;
    }

    public String getFormattedName() {
        return color + displayName;
    }

    /**
     * 아레나가 활성 상태인지 확인
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 아레나가 대기 상태인지 확인
     */
    public boolean isWaiting() {
        return this == WAITING;
    }

    /**
     * 아레나가 진행 중인지 확인 (준비 ~ 종료 중)
     */
    public boolean isInProgress() {
        return this == PREPARING || this == ACTIVE || this == ENDING;
    }

    /**
     * 아레나가 이용 가능한지 확인
     */
    public boolean isAvailable() {
        return this != MAINTENANCE;
    }

    /**
     * 관전 가능 여부
     */
    public boolean canSpectate() {
        return this == ACTIVE || this == PREPARING;
    }

    /**
     * 퇴장 가능 여부
     */
    public boolean canLeave() {
        return this == WAITING || this == PREPARING;
    }

    /**
     * 다음 상태로 전환
     */
    public ArenaStatus getNextStatus() {
        switch (this) {
            case WAITING: 
                return PREPARING;
            case PREPARING:
                return ACTIVE;
            case ACTIVE: 
                return ENDING;
            case ENDING:
                return WAITING;
            case MAINTENANCE:
                return WAITING;
            default:
                return WAITING;
        }
    }

    /**
     * 상태별 아이콘 (GUI용)
     */
    public org.bukkit.Material getIcon() {
        switch (this) {
            case WAITING:
                return org.bukkit. Material.LIME_WOOL;
            case PREPARING:
                return org.bukkit. Material.YELLOW_WOOL;
            case ACTIVE: 
                return org. bukkit.Material. RED_WOOL;
            case ENDING: 
                return org. bukkit.Material. ORANGE_WOOL;
            case MAINTENANCE: 
                return org. bukkit.Material. GRAY_WOOL;
            default: 
                return org. bukkit.Material. WHITE_WOOL;
        }
    }

    /**
     * 문자열로부터 ArenaStatus 반환
     */
    public static ArenaStatus fromString(String str) {
        if (str == null) {
            return WAITING;
        }
        
        try {
            return valueOf(str. toUpperCase());
        } catch (IllegalArgumentException e) {
            for (ArenaStatus status : values()) {
                if (status.displayName.equalsIgnoreCase(str)) {
                    return status;
                }
            }
            return WAITING;
        }
    }
}