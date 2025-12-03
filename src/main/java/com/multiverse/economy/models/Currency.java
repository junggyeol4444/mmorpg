package com.multiverse.economy.models;

public class Currency {
    private final String id;
    private String name;
    private String symbol;
    private double initialRate;
    private boolean enabled;

    public Currency(String id, String name, String symbol, double initialRate) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.initialRate = initialRate;
        this.enabled = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getInitialRate() {
        return initialRate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setInitialRate(double initialRate) {
        this.initialRate = initialRate;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}