package com.multiverse.economy.models;

import java.time.LocalDateTime;

public class Transaction {
    private final String id;
    private final BankAccount senderAccount;
    private final BankAccount receiverAccount;
    private final double amount;
    private final String currencyId;
    private final LocalDateTime timestamp;
    private final String reason;

    public Transaction(String id, BankAccount senderAccount, BankAccount receiverAccount, double amount, String currencyId, String reason) {
        this.id = id;
        this.senderAccount = senderAccount;
        this.receiverAccount = receiverAccount;
        this.amount = amount;
        this.currencyId = currencyId;
        this.timestamp = LocalDateTime.now();
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public BankAccount getSenderAccount() {
        return senderAccount;
    }

    public BankAccount getReceiverAccount() {
        return receiverAccount;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getReason() {
        return reason;
    }
}