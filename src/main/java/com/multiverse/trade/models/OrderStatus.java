package com. multiverse.trade. models;

public enum OrderStatus {
    
    ACTIVE("활성"),
    PARTIAL("부분 체결"),
    FILLED("체결 완료"),
    CANCELLED("취소"),
    EXPIRED("만료");

    private final String displayName;

    OrderStatus(String displayName) {
        this. displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == ACTIVE || this == PARTIAL;
    }

    public boolean isCompleted() {
        return this == FILLED;
    }

    public boolean isCancelled() {
        return this == CANCELLED;
    }

    public boolean isExpired() {
        return this == EXPIRED;
    }

    public boolean isFinished() {
        return this == FILLED || this == CANCELLED || this == EXPIRED;
    }

    public boolean canCancel() {
        return this == ACTIVE || this == PARTIAL;
    }

    public boolean canMatch() {
        return this == ACTIVE || this == PARTIAL;
    }

    public String getColor() {
        switch (this) {
            case ACTIVE: 
                return "&a";
            case PARTIAL:
                return "&e";
            case FILLED:
                return "&2";
            case CANCELLED:
                return "&c";
            case EXPIRED: 
                return "&7";
            default: 
                return "&f";
        }
    }
}