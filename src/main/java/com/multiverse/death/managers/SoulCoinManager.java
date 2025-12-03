package com.multiverse.death.managers;

import com.multiverse.death.DeathAndRebirthCore;
import com.multiverse.death.data.DataManager;
import com.multiverse.death.models.SoulCoinAccount;
import com.multiverse.death.models.SoulCoinTransaction;
import com.multiverse.death.models.enums.TransactionType;
import org.bukkit.entity.Player;

import java.util.List;

public class SoulCoinManager {

    private final DeathAndRebirthCore plugin;
    private final DataManager dataManager;

    public SoulCoinManager(DeathAndRebirthCore plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    // 잔액 관리
    public double getBalance(Player player) {
        return dataManager.getSoulCoinBalance(player);
    }

    public void setBalance(Player player, double amount) {
        dataManager.setSoulCoinBalance(player, amount);
    }

    public void addBalance(Player player, double amount, String reason) {
        double newBalance = getBalance(player) + amount;
        setBalance(player, newBalance);
        recordTransaction(player, createTransaction(TransactionType.EARN, amount, reason));
    }

    public void removeBalance(Player player, double amount, String reason) {
        double newBalance = getBalance(player) - amount;
        if (newBalance < 0) newBalance = 0;
        setBalance(player, newBalance);
        recordTransaction(player, createTransaction(TransactionType.SPEND, -amount, reason));
    }

    // 거래 확인
    public boolean hasEnough(Player player, double amount) {
        return getBalance(player) >= amount;
    }

    // 소각
    public void burnCoins(double amount, String reason) {
        double currentTotalBurned = dataManager.getTotalSoulCoinBurned();
        dataManager.setTotalSoulCoinBurned(currentTotalBurned + amount);
        dataManager.recordGlobalBurn(reason, amount);
    }

    public double getTotalBurned() {
        return dataManager.getTotalSoulCoinBurned();
    }

    public double getTotalCirculation() {
        return dataManager.getTotalSoulCoinCirculation();
    }

    public double getTotalEarned() {
        return dataManager.getTotalSoulCoinEarned();
    }

    public double getTotalSpent() {
        return dataManager.getTotalSoulCoinSpent();
    }

    // 거래 내역
    public void recordTransaction(Player player, SoulCoinTransaction transaction) {
        dataManager.addSoulCoinTransaction(player, transaction);
    }

    public List<SoulCoinTransaction> getTransactions(Player player, int limit) {
        return dataManager.getSoulCoinTransactions(player, limit);
    }

    private SoulCoinTransaction createTransaction(TransactionType type, double amount, String reason) {
        SoulCoinTransaction tx = new SoulCoinTransaction();
        tx.setType(type);
        tx.setAmount(amount);
        tx.setReason(reason);
        tx.setTimestamp(System.currentTimeMillis());
        return tx;
    }
}