package com.multiverse.skill.data.enums;

/**
 * 소환수 AI 타입
 */
public enum SummonAI {
    MELEE("근접 공격", "주인 근처에서 근접 공격"),
    RANGED("원거리 공격", "거리를 유지하고 원거리 공격"),
    SUPPORT("지원", "주인에게 버프를 주고 보호"),
    TANK("방어형", "주인 앞에서 방어 역할"),
    PASSIVE("수동", "주인을 따라다니기만 함"),
    AGGRESSIVE("공격형", "주변 모든 적에게 공격"),
    DEFENSIVE("방어형", "주인 주변 영역 방어"),
    NONE("AI 없음", "움직이지 않음");

    private final String displayName;
    private final String description;

    SummonAI(String displayName, String description) {
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
     * 공격형 AI 여부
     */
    public boolean isOffensive() {
        return this == MELEE || this == RANGED || this == AGGRESSIVE;
    }

    /**
     * 방어형 AI 여부
     */
    public boolean isDefensive() {
        return this == SUPPORT || this == TANK || this == DEFENSIVE;
    }

    /**
     * 움직임 AI 여부
     */
    public boolean canMove() {
        return this != PASSIVE && this != NONE;
    }

    /**
     * 공격 가능 AI 여부
     */
    public boolean canAttack() {
        return this. isOffensive() || this.isDefensive();
    }

    /**
     * AI 행동 범위 (블록)
     */
    public int getActionRange() {
        return switch (this) {
            case MELEE -> 5;
            case RANGED -> 30;
            case SUPPORT -> 15;
            case TANK -> 10;
            case PASSIVE -> 0;
            case AGGRESSIVE -> 50;
            case DEFENSIVE -> 20;
            default -> 0;
        };
    }
}