package com.multiverse.skill. data.enums;

/**
 * 대상 타입
 */
public enum TargetType {
    SELF("자기자신", "자신에게만 적용"),
    TARGET("단일 대상", "한 명의 대상 선택"),
    AREA("범위", "원형 범위 내 모든 대상"),
    CONE("원뿔", "원뿔 형태 범위"),
    LINE("직선", "직선 형태 범위"),
    PROJECTILE("투사체", "투사체로 발사"),
    ALL_HOSTILE("모든 적", "시야 내 모든 적"),
    ALL_ALLIES("모든 아군", "시야 내 모든 아군");

    private final String displayName;
    private final String description;

    TargetType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 범위 기반 타입 여부
     */
    public boolean isAreaBased() {
        return this == AREA || this == CONE || this == LINE || 
               this == ALL_HOSTILE || this == ALL_ALLIES;
    }

    /**
     * 단일 대상 타입 여부
     */
    public boolean isSingleTarget() {
        return this == SELF || this == TARGET;
    }

    /**
     * 투사체 타입 여부
     */
    public boolean isProjectile() {
        return this == PROJECTILE;
    }

    /**
     * 적 대상 여부
     */
    public boolean isHostile() {
        return this == TARGET || this == PROJECTILE || this == ALL_HOSTILE;
    }

    /**
     * 아군 대상 여부
     */
    public boolean isAlly() {
        return this == SELF || this == ALL_ALLIES;
    }
}