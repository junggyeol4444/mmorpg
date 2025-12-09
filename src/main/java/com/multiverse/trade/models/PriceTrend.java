package com. multiverse.trade. models;

public enum PriceTrend {
    
    RISING("상승", "&a", "↑"),
    STABLE("안정", "&e", "→"),
    FALLING("하락", "&c", "↓");

    private final String displayName;
    private final String color;
    private final String symbol;

    PriceTrend(String displayName, String color, String symbol) {
        this.  displayName = displayName;
        this.  color = color;
        this. symbol = symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColor() {
        return color;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getColoredDisplay() {
        return color + symbol + " " + displayName;
    }

    public boolean isRising() {
        return this == RISING;
    }

    public boolean isStable() {
        return this == STABLE;
    }

    public boolean isFalling() {
        return this == FALLING;
    }

    public boolean isPositive() {
        return this == RISING;
    }

    public boolean isNegative() {
        return this == FALLING;
    }

    public static PriceTrend fromChange(double changePercent) {
        if (changePercent > 5) {
            return RISING;
        } else if (changePercent < -5) {
            return FALLING;
        } else {
            return STABLE;
        }
    }

    public static PriceTrend fromChange(double changePercent, double threshold) {
        if (changePercent > threshold) {
            return RISING;
        } else if (changePercent < -threshold) {
            return FALLING;
        } else {
            return STABLE;
        }
    }
}