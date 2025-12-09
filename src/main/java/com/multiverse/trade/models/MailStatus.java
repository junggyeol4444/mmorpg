package com.  multiverse.trade.  models;

public enum MailStatus {
    
    UNREAD("읽지 않음"),
    READ("읽음"),
    CLAIMED("수령함"),
    EXPIRED("만료");

    private final String displayName;

    MailStatus(String displayName) {
        this.  displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isUnread() {
        return this == UNREAD;
    }

    public boolean isRead() {
        return this == READ || this == CLAIMED;
    }

    public boolean isClaimed() {
        return this == CLAIMED;
    }

    public boolean isExpired() {
        return this == EXPIRED;
    }

    public boolean canClaim() {
        return this == UNREAD || this == READ;
    }

    public boolean canDelete() {
        return this == CLAIMED || this == EXPIRED;
    }

    public String getColor() {
        switch (this) {
            case UNREAD:
                return "&e";
            case READ:  
                return "&a";
            case CLAIMED:
                return "&7";
            case EXPIRED:  
                return "&c";
            default:  
                return "&f";
        }
    }

    public String getIcon() {
        switch (this) {
            case UNREAD:
                return "✉";
            case READ: 
                return "📖";
            case CLAIMED:
                return "✔";
            case EXPIRED: 
                return "✖";
            default:  
                return "? ";
        }
    }
}