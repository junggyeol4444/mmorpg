package com.multiverse.economy.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Check {
    private final String id;
    private final UUID issuerId;
    private final UUID recipientId;
    private final double amount;
    private final String currencyId;
    private final LocalDateTime issueDate;
    private boolean cashed;

    public Check(String id, UUID issuerId, UUID recipientId, double amount, String currencyId) {
        this.id = id;
        this.issuerId = issuerId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.currencyId = currencyId;
        this.issueDate = LocalDateTime.now();
        this.cashed = false;
    }

    public String getId() {
        return id;
    }

    public UUID getIssuerId() {
        return issuerId;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public boolean isCashed() {
        return cashed;
    }

    public void setCashed(boolean cashed) {
        this.cashed = cashed;
    }
}