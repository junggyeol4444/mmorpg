package com.multiverse. trade.models;

public enum TradeStatus {
    
    REQUESTED("요청됨"),
    ACTIVE("진행 중"),
    CONFIRMING("확인 중"),
    COMPLETED("완료"),
    CANCELLED("취소");

    private final String displayName;

    TradeStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == ACTIVE || this == CONFIRMING;
    }

    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED;
    }

    public boolean canModify() {
        return this == ACTIVE;
    }

    public boolean canCancel() {
        return this == REQUESTED || this == ACTIVE || this == CONFIRMING;
    }
}