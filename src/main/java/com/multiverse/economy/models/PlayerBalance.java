package com.multiverse.economy.models;

import java.util.UUID;

public class PlayerBalance {
    private final UUID playerId;
    private String currencyId;
    private double balance;

    public PlayerBalance(UUID playerId, String currencyId, double balance) {
        this.playerId = playerId;
        this.currencyId = currencyId;
        this.balance = balance;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public boolean withdraw(double amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }
}