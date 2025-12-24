package com.multiverse.pvp.enums;

public enum DuelStatus {

    REQUESTED("요청됨", "&e", "듀얼 요청이 전송되었습니다. "),
    ACCEPTED("수락됨", "&a", "듀얼 요청이 수락되었습니다."),
    PREPARING("준비 중", "&6", "듀얼 준비 중입니다."),
    ACTIVE("진행 중", "&c", "듀얼이 진행 중입니다."),
    ENDED("종료", "&7", "듀얼이 종료되었습니다.");

    private final String displayName;
    private final String color;
    private final String message;

    DuelStatus(String displayName, String color, String message) {
        this.displayName = displayName;
        this.color = color;
        this. message = message;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedName() {
        return color + displayName;
    }

    /**
     * 듀얼이 대기 상태인지 확인
     */
    public boolean isPending() {
        return this == REQUESTED || this == ACCEPTED;
    }

    /**
     * 듀얼이 진행 중인지 확인
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 듀얼이 종료되었는지 확인
     */
    public boolean isEnded() {
        return this == ENDED;
    }

    /**
     * 듀얼이 취소 가능한지 확인
     */
    public boolean isCancellable() {
        return this == REQUESTED || this == ACCEPTED;
    }

    /**
     * 듀얼이 수락 가능한지 확인
     */
    public boolean isAcceptable() {
        return this == REQUESTED;
    }

    /**
     * 듀얼이 준비 중인지 확인
     */
    public boolean isPreparing() {
        return this == PREPARING;
    }

    /**
     * 항복 가능 여부
     */
    public boolean canSurrender() {
        return this == ACTIVE;
    }

    /**
     * 전투 가능 여부
     */
    public boolean canFight() {
        return this == ACTIVE;
    }

    /**
     * 다음 상태로 전환
     */
    public DuelStatus getNextStatus() {
        switch (this) {
            case REQUESTED: 
                return ACCEPTED;
            case ACCEPTED:
                return PREPARING;
            case PREPARING:
                return ACTIVE;
            case ACTIVE: 
                return ENDED;
            case ENDED:
                return ENDED;
            default: 
                return ENDED;
        }
    }

    /**
     * 문자열로부터 DuelStatus 반환
     */
    public static DuelStatus fromString(String str) {
        if (str == null) {
            return REQUESTED;
        }
        
        try {
            return valueOf(str. toUpperCase());
        } catch (IllegalArgumentException e) {
            for (DuelStatus status : values()) {
                if (status.displayName.equalsIgnoreCase(str)) {
                    return status;
                }
            }
            return REQUESTED;
        }
    }
}