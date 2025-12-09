package com. multiverse.trade.models;

public enum MailType {
    
    PLAYER("플레이어"),
    SYSTEM("시스템"),
    ADMIN("관리자"),
    TRADE("거래"),
    AUCTION("경매"),
    MARKET("거래소"),
    SHOP("상점");

    private final String displayName;

    MailType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPlayer() {
        return this == PLAYER;
    }

    public boolean isSystem() {
        return this == SYSTEM || this == ADMIN;
    }

    public boolean isAutomated() {
        return this == TRADE || this == AUCTION || this == MARKET || this == SHOP;
    }

    public boolean canReply() {
        return this == PLAYER;
    }

    public boolean canBlock() {
        return this == PLAYER;
    }

    public String getColor() {
        switch (this) {
            case PLAYER:
                return "&f";
            case SYSTEM:
                return "&6";
            case ADMIN:
                return "&c";
            case TRADE:
                return "&a";
            case AUCTION: 
                return "&e";
            case MARKET:
                return "&b";
            case SHOP:
                return "&d";
            default:
                return "&f";
        }
    }

    public String getPrefix() {
        switch (this) {
            case PLAYER: 
                return "";
            case SYSTEM:
                return "[시스템] ";
            case ADMIN:
                return "[관리자] ";
            case TRADE:
                return "[거래] ";
            case AUCTION:
                return "[경매] ";
            case MARKET: 
                return "[거래소] ";
            case SHOP: 
                return "[상점] ";
            default:
                return "";
        }
    }
}