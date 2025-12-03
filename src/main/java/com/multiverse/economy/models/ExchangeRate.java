package com.multiverse.economy.models;

public class ExchangeRate {
    private final String fromCurrencyId;
    private final String toCurrencyId;
    private double rate;

    public ExchangeRate(String fromCurrencyId, String toCurrencyId, double rate) {
        this.fromCurrencyId = fromCurrencyId;
        this.toCurrencyId = toCurrencyId;
        this.rate = rate;
    }

    public String getFromCurrencyId() {
        return fromCurrencyId;
    }

    public String getToCurrencyId() {
        return toCurrencyId;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}