package com.multiverse.economy.models;

public class EconomyStatistics {
    private double totalBalance;
    private double totalLoans;
    private double totalBurned;
    private int transactionCount;
    private double inflationRate;

    public EconomyStatistics() {
        this.totalBalance = 0.0;
        this.totalLoans = 0.0;
        this.totalBurned = 0.0;
        this.transactionCount = 0;
        this.inflationRate = 0.0;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public double getTotalLoans() {
        return totalLoans;
    }

    public void setTotalLoans(double totalLoans) {
        this.totalLoans = totalLoans;
    }

    public double getTotalBurned() {
        return totalBurned;
    }

    public void setTotalBurned(double totalBurned) {
        this.totalBurned = totalBurned;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public double getInflationRate() {
        return inflationRate;
    }

    public void setInflationRate(double inflationRate) {
        this.inflationRate = inflationRate;
    }
}