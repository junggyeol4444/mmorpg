package com.multiverse.pvp.enums;

public enum PvPType {

    ALWAYS_ON("항상 PvP", "&c", "모든 지역에서 PvP가 가능합니다. "),
    CONSENSUAL("합의 PvP", "&e", "양측이 동의해야 PvP가 가능합니다. "),
    ZONE_BASED("지역 PvP", "&a", "PvP 지역에서만 전투가 가능합니다. "),
    GUILD_WAR("길드 전쟁", "&6", "길드 간 전쟁 상태에서만 PvP가 가능합니다.");

    private final String displayName;
    private final String color;
    private final String description;

    PvPType(String displayName, String color, String description) {
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
     * 문자열로부터 PvPType 반환
     */
    public static PvPType fromString(String str) {
        if (str == null) {
            return CONSENSUAL;
        }
        
        try {
            return valueOf(str.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 디스플레이 이름으로 검색
            for (PvPType type : values()) {
                if (type. displayName.equalsIgnoreCase(str)) {
                    return type;
                }
            }
            return CONSENSUAL;
        }
    }

    /**
     * PvP 활성화 필수 여부
     */
    public boolean requiresPvPEnabled() {
        return this == CONSENSUAL;
    }

    /**
     * 지역 기반 PvP인지 확인
     */
    public boolean isZoneBased() {
        return this == ZONE_BASED;
    }

    /**
     * 길드 기반 PvP인지 확인
     */
    public boolean isGuildBased() {
        return this == GUILD_WAR;
    }

    /**
     * 항상 PvP 가능한지 확인
     */
    public boolean isAlwaysOn() {
        return this == ALWAYS_ON;
    }
}