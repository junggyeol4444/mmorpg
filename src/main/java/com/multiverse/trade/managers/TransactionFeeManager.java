package com.multiverse. trade.managers;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.models.OrderType;

public class TransactionFeeManager {

    private final TradeCore plugin;
    
    private double totalBurned = 0;
    private double totalEventFund = 0;
    private double totalServerFund = 0;

    public TransactionFeeManager(TradeCore plugin) {
        this. plugin = plugin;
    }

    public double calculateTradeFee(double amount) {
        if (! plugin.getConfig().getBoolean("direct-trade.tax. enabled", true)) {
            return 0;
        }
        double rate = plugin.getConfig().getDouble("direct-trade.tax. rate", 1.0) / 100.0;
        return amount * rate;
    }

    public double calculateShopFee(double amount) {
        double rate = plugin.getConfig().getDouble("player-shops.fees.sale-fee", 3.0) / 100.0;
        return amount * rate;
    }

    public double calculateAuctionListingFee() {
        return plugin.getConfig().getDouble("auction. fees.listing-fee", 100.0);
    }

    public double calculateAuctionSellerFee(double amount) {
        double rate = plugin.getConfig().getDouble("auction.fees.seller-fee", 5.0) / 100.0;
        return amount * rate;
    }

    public double calculateMarketFee(double amount, OrderType type) {
        double rate;
        if (type == OrderType. SELL) {
            rate = plugin.getConfig().getDouble("market.fees.sell-order", 2.0) / 100.0;
        } else {
            rate = plugin. getConfig().getDouble("market.fees. buy-order", 0.0) / 100.0;
        }
        return amount * rate;
    }

    public double calculateInstantTradeFee(double amount) {
        double rate = plugin.getConfig().getDouble("market.fees.instant-trade", 3.0) / 100.0;
        return amount * rate;
    }

    public void collectFee(double amount) {
        distributeFee(amount);
    }

    public void distributeFee(double amount) {
        if (amount <= 0) {
            return;
        }

        double burnRate = plugin.getConfig().getDouble("fee-distribution.burn", 50.0) / 100.0;
        double eventRate = plugin.getConfig().getDouble("fee-distribution.event", 30.0) / 100.0;
        double serverRate = plugin.getConfig().getDouble("fee-distribution.server", 20.0) / 100.0;

        double burnAmount = amount * burnRate;
        double eventAmount = amount * eventRate;
        double serverAmount = amount * serverRate;

        totalBurned += burnAmount;
        totalEventFund += eventAmount;
        totalServerFund += serverAmount;

        plugin.getLogger().fine("수수료 분배: 소각=" + burnAmount + ", 이벤트=" + eventAmount + ", 서버=" + serverAmount);
    }

    public double getTotalBurned() {
        return totalBurned;
    }

    public double getTotalEventFund() {
        return totalEventFund;
    }

    public double getTotalServerFund() {
        return totalServerFund;
    }

    public double withdrawEventFund(double amount) {
        if (amount > totalEventFund) {
            amount = totalEventFund;
        }
        totalEventFund -= amount;
        return amount;
    }

    public double withdrawServerFund(double amount) {
        if (amount > totalServerFund) {
            amount = totalServerFund;
        }
        totalServerFund -= amount;
        return amount;
    }

    public void resetStatistics() {
        totalBurned = 0;
        totalEventFund = 0;
        totalServerFund = 0;
    }

    public String getStatistics() {
        return String.format(
            "수수료 통계:\n- 총 소각:  %.2f\n- 이벤트 기금: %.2f\n- 서버 기금: %. 2f",
            totalBurned, totalEventFund, totalServerFund
        );
    }
}