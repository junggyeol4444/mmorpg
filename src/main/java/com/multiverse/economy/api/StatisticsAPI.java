package com.multiverse.economy.api;

import com.multiverse.economy.models.EconomyStatistics;

public class StatisticsAPI {

    public static void updateStatistics(EconomyStatistics stats, int transactionCount, double totalBalance, double totalLoans, double totalBurned, double inflationRate) {
        stats.setTransactionCount(transactionCount);
        stats.setTotalBalance(totalBalance);
        stats.setTotalLoans(totalLoans);
        stats.setTotalBurned(totalBurned);
        stats.setInflationRate(inflationRate);
    }

    // Additional statistics methods can be added as needed
}