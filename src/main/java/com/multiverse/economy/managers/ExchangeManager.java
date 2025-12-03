package com.multiverse.economy.managers;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.models.ExchangeRate;
import com.multiverse.economy.models.Currency;
import com.multiverse.economy.models.enums.CurrencyType;
import com.multiverse.economy.data.DataManager;
import com.multiverse.economy.models.Transaction;
import com.multiverse.economy.models.enums.TransactionType;
import com.multiverse.economy.utils.MessageUtil;
import com.multiverse.economy.utils.ConfigUtil;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExchangeManager {

    private final EconomyCore plugin;
    private final ConfigUtil config;
    private final CurrencyManager currencyManager;
    private final EconomyDataManager economyDataManager;
    private final MessageUtil msg;
    private final StatisticsManager statisticsManager;

    private final Map<String, ExchangeRate> rates = new ConcurrentHashMap<>();

    public ExchangeManager(EconomyCore plugin, ConfigUtil config,
                          CurrencyManager currencyManager,
                          EconomyDataManager economyDataManager,
                          MessageUtil msg,
                          StatisticsManager statisticsManager) {
        this.plugin = plugin;
        this.config = config;
        this.currencyManager = currencyManager;
        this.economyDataManager = economyDataManager;
        this.msg = msg;
        this.statisticsManager = statisticsManager;
        loadExchangeRates();
    }

    private void loadExchangeRates() {
        // exchange_rates.yml에서 환율 정보 로드
        rates.clear();
        Map<String, Object> raw = config.getYaml("exchange_rates.yml");
        Map<String, Object> section = (Map<String, Object>) raw.get("rates");
        for (String id : section.keySet()) {
            Map<?,?> r = (Map<?,?>) section.get(id);
            ExchangeRate rate = new ExchangeRate(
                (String) r.get("from"),
                (String) r.get("to"),
                ((Number) r.get("rate")).doubleValue(),
                r.containsKey("fee-percentage") ? ((Number)r.get("fee-percentage")).doubleValue() : 0.0,
                r.containsKey("last-update") ? ((Number)r.get("last-update")).longValue() : System.currentTimeMillis()
            );
            rates.put(rate.getFromCurrency() + "_" + rate.getToCurrency(), rate);
        }
    }

    public ExchangeRate getRate(String from, String to) {
        return rates.get(from + "_" + to);
    }

    public void setRate(String from, String to, double rate) {
        ExchangeRate r = getRate(from, to);
        if (r != null) {
            r.setRate(rate);
            r.setLastUpdate(System.currentTimeMillis());
        } else {
            rates.put(from + "_" + to, new ExchangeRate(from, to, rate, 0.0, System.currentTimeMillis()));
        }
        // 환율 변경을 파일에 저장하는 기능
        config.saveExchangeRates(rates);
    }

    public List<ExchangeRate> getAllRates() {
        return new ArrayList<>(rates.values());
    }

    public double calculateExchange(String from, String to, double amount) {
        ExchangeRate rate = getRate(from, to);
        if (rate == null) return 0;
        double fee = calculateFee(from, to, amount);
        return (amount * rate.getRate()) - fee;
    }

    public double calculateFee(String from, String to, double amount) {
        ExchangeRate rate = getRate(from, to);
        if (rate == null) return 0;
        return amount * rate.getRate() * rate.getFee() / 100.0;
    }

    public boolean canExchange(Player player, String from, String to, double amount) {
        Currency fromCurrency = currencyManager.getCurrency(from);
        Currency toCurrency = currencyManager.getCurrency(to);

        if (fromCurrency == null || toCurrency == null) return false;
        if (currencyManager.getBalance(player, from) < amount) return false;

        // 차원 화폐 간 직접 환전 불가
        if (fromCurrency.getType() == CurrencyType.DIMENSION && toCurrency.getType() == CurrencyType.DIMENSION)
            return false;
        // SoulCoin(공통) 필요시만 차원간 가능
        return true;
    }

    public void exchange(Player player, String from, String to, double amount) {
        if (!canExchange(player, from, to, amount)) return;
        double fee = calculateFee(from, to, amount);
        double toAmount = calculateExchange(from, to, amount);

        currencyManager.removeBalance(player, from, amount);
        currencyManager.addBalance(player, to, toAmount);

        // 수수료 징수
        collectFee(fee);

        // 거래 기록
        Transaction transaction = new Transaction(UUID.randomUUID(), System.currentTimeMillis(), TransactionType.PLAYER_TRANSFER,
                player.getUniqueId(), player.getUniqueId(), from, amount, fee, toAmount, "환전");
        statisticsManager.recordTransaction(transaction);
    }

    public void collectFee(double feeAmount) {
        // 수수료 분배 및 소각
        // config.yml의 fee-distribution 따라 처리
        double burnPercent = config.getDouble("exchange.fee-distribution.burn", 50.0);
        double adminPercent = config.getDouble("exchange.fee-distribution.admin", 30.0);
        double eventPercent = config.getDouble("exchange.fee-distribution.event", 20.0);

        double burn = feeAmount * burnPercent / 100.0;
        double admin = feeAmount * adminPercent / 100.0;
        double event = feeAmount * eventPercent / 100.0;

        // 소각 처리 by InflationManager
        plugin.getInflationManager().burnCurrency(currencyManager.getUniversalCurrency().getId(), burn, "환전 수수료 소각");
        // 관리자 기금/이벤트는 각 별도 로그로 처리
        statisticsManager.recordTax(admin + event);
    }

    public long getTotalExchanged(String from, String to) {
        return statisticsManager.getTotalExchanged(from, to);
    }

    public double getTotalFeeCollected() {
        return statisticsManager.getTotalFeeCollected();
    }
}