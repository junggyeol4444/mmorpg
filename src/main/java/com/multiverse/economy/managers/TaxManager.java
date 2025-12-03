package com.multiverse.economy.managers;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.models.Transaction;
import com.multiverse.economy.models.enums.TransactionType;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.utils.MessageUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TaxManager {

    private final EconomyCore plugin;
    private final ConfigUtil config;
    private final MessageUtil msg;
    private final StatisticsManager statisticsManager;
    private final InflationManager inflationManager;

    private final Map<TransactionType, Double> taxRates = new EnumMap<>(TransactionType.class);

    public TaxManager(EconomyCore plugin,
                      ConfigUtil config,
                      MessageUtil msg,
                      StatisticsManager statisticsManager,
                      InflationManager inflationManager) {
        this.plugin = plugin;
        this.config = config;
        this.msg = msg;
        this.statisticsManager = statisticsManager;
        this.inflationManager = inflationManager;
        loadTaxRates();
    }

    private void loadTaxRates() {
        // config.yml tax.rates에서 세율 읽어옴
        for (TransactionType type : TransactionType.values()) {
            String path = "tax.rates." + type.name().replace('_', '-').toLowerCase();
            double rate = config.getDouble(path, 0);
            taxRates.put(type, rate);
        }
    }

    public double getTaxRate(TransactionType type) {
        return taxRates.getOrDefault(type, 0.0);
    }

    public double calculateTax(TransactionType type, double amount) {
        double rate = getTaxRate(type);
        return amount * rate / 100.0;
    }

    // 세금 적용 대상 결정
    public boolean shouldApplyTax(TransactionType type) {
        // 예외 처리(상점 구매 등)
        String path = "tax.exemptions." + type.name().replace('_', '-').toLowerCase();
        return !config.getBoolean(path, false);
    }

    public void collectTax(Transaction transaction) {
        double tax = transaction.getTax();
        // 분배: 소각, 이벤트, 공공, 관리자
        double burn = tax * config.getDouble("tax.distribution.burn", 40.0) / 100.0;
        double event = tax * config.getDouble("tax.distribution.event", 30.0) / 100.0;
        double publicFund = tax * config.getDouble("tax.distribution.public", 20.0) / 100.0;
        double adminFund = tax * config.getDouble("tax.distribution.admin", 10.0) / 100.0;

        // 소각 분배
        inflationManager.burnCurrency(transaction.getCurrencyId(), burn, "거래세 소각");
        statisticsManager.recordTaxDistribution(transaction.getCurrencyId(), burn, event, publicFund, adminFund);

        statisticsManager.recordTax(tax);
    }

    public void distributeTax(double taxAmount) {
        // 별도 호출성 로직(통계용)
        double burn = taxAmount * config.getDouble("tax.distribution.burn", 40.0) / 100.0;
        double event = taxAmount * config.getDouble("tax.distribution.event", 30.0) / 100.0;
        double publicFund = taxAmount * config.getDouble("tax.distribution.public", 20.0) / 100.0;
        double adminFund = taxAmount * config.getDouble("tax.distribution.admin", 10.0) / 100.0;
        inflationManager.burnCurrency("soul_coin", burn, "세금 소각"); // universal
        statisticsManager.recordTaxDistribution("soul_coin", burn, event, publicFund, adminFund);
    }

    public double getTotalTaxCollected() {
        return statisticsManager.getTotalTaxCollected();
    }

    public double getTotalTaxBurned() {
        return statisticsManager.getTotalTaxBurned();
    }

    public Map<String, Double> getTaxDistribution() {
        return statisticsManager.getTaxDistribution();
    }
}