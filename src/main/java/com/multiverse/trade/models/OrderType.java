package com.multiverse. trade.models;

public enum OrderType {
    
    SELL("판매"),
    BUY("구매");

    private final String displayName;

    OrderType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isSell() {
        return this == SELL;
    }

    public boolean isBuy() {
        return this == BUY;
    }

    public OrderType getOpposite() {
        return this == SELL ? BUY : SELL;
    }

    public String getActionName() {
        return this == SELL ?  "판매" :  "구매";
    }

    public String getColor() {
        return this == SELL ? "&c" : "&a";
    }
}