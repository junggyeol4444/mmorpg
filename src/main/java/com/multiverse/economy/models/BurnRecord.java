package com.multiverse.economy.models;

import java.time.LocalDateTime;

public class BurnRecord {
    private final String id;
    private final String currencyId;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String reason;

    public BurnRecord(String id, String currencyId, double amount, String reason) {
        this.id = id;
        this.currencyId = currencyId;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getReason() {
        return reason;
    }
}