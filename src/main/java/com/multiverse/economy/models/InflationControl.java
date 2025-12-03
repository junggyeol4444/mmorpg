package com.multiverse.economy.models;

public class InflationControl {
    private final String currencyId;
    private double inflationRate;
    private double controlThreshold;
    private boolean active;

    public InflationControl(String currencyId, double inflationRate, double controlThreshold, boolean active) {
        this.currencyId = currencyId;
        this.inflationRate = inflationRate;
        this.controlThreshold = controlThreshold;
        this.active = active;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public double getInflationRate() {
        return inflationRate;
    }

    public void setInflationRate(double inflationRate) {
        this.inflationRate = inflationRate;
    }

    public double getControlThreshold() {
        return controlThreshold;
    }

    public void setControlThreshold(double controlThreshold) {
        this.controlThreshold = controlThreshold;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}