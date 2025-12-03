package com.multiverse.death.data.storage;

import com.multiverse.death.models.SoulCoinAccount;
import com.multiverse.death.models.SoulCoinTransaction;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Optional storage layer for SoulCoin data,
 * allows migration to other persistence engines.
 */
public interface SoulCoinStorage {

    double getSoulCoinBalance(Player player);
    void setSoulCoinBalance(Player player, double balance);

    void addSoulCoinTransaction(Player player, SoulCoinTransaction transaction);
    List<SoulCoinTransaction> getSoulCoinTransactions(Player player, int limit);

    double getTotalSoulCoinBurned();
    void setTotalSoulCoinBurned(double burned);

    double getTotalSoulCoinCirculation();
    double getTotalSoulCoinEarned();
    double getTotalSoulCoinSpent();

    void recordGlobalBurn(String reason, double amount);
}