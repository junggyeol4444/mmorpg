package com.multiverse.death.models;

import java.util.UUID;

/**
 * 플레이어의 소울 코인 계정 정보 모델
 */
public class SoulCoinAccount {
    private UUID playerUUID;
    private double balance;
    private double totalEarned;
    private double totalSpent;

    public UUID getPlayerUUID() {
        return playerUUID;
    }
    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getTotalEarned() {
        return totalEarned;
    }
    public void setTotalEarned(double totalEarned) {
        this.totalEarned = totalEarned;
    }

    public double getTotalSpent() {
        return totalSpent;
    }
    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }
}