package com.multiverse.guild.model;

import java.util.UUID;

public class Transaction {
    private TransactionType type;
    private String currency;
    private double amount;
    private UUID player;
    private long timestamp;
    private String reason;

    public Transaction(TransactionType type, String currency, double amount, UUID player, long timestamp, String reason) {
        this.type = type;
        this.currency = currency;
        this.amount = amount;
        this.player = player;
        this.timestamp = timestamp;
        this.reason = reason;
    }

    public TransactionType getType() { return type; }
    public String getCurrency() { return currency; }
    public double getAmount() { return amount; }
    public UUID getPlayer() { return player; }
    public long getTimestamp() { return timestamp; }
    public String getReason() { return reason; }
}