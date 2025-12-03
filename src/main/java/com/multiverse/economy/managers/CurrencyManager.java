package com.multiverse.economy.managers;

import com.multiverse.economy.EconomyCore;
import com.multiverse.economy.models.Currency;
import com.multiverse.economy.models.enums.CurrencyType;
import com.multiverse.economy.data.DataManager;
import com.multiverse.economy.utils.ConfigUtil;
import com.multiverse.economy.utils.MessageUtil;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyManager {

    private final EconomyCore plugin;
    private final ConfigUtil config;
    private final EconomyDataManager economyDataManager;
    private final MessageUtil msg;

    // 서버 전체 화폐 정보
    private final Map<String, Currency> currencies = new ConcurrentHashMap<>();
    private final Set<String> dimensionCurrencies = new HashSet<>();
    private String universalCurrencyId = null;

    public CurrencyManager(EconomyCore plugin, ConfigUtil config, EconomyDataManager economyDataManager, MessageUtil msg) {
        this.plugin = plugin;
        this.config = config;
        this.economyDataManager = economyDataManager;
        this.msg = msg;
        loadCurrencies();
    }

    private void loadCurrencies() {
        // currencies.yml로부터 화폐 정보 로드
        currencies.clear();
        dimensionCurrencies.clear();
        universalCurrencyId = null;
        Map<String, Object> raw = config.getYaml("currencies.yml");
        Map<String, Object> section = (Map<String, Object>) raw.get("currencies");
        for (String id : section.keySet()) {
            Map<?,?> c = (Map<?,?>) section.get(id);
            Currency currency = new Currency(
                id,
                (String) c.get("name"),
                (String) c.get("symbol"),
                (String) c.get("dimension"),
                CurrencyType.valueOf((String) c.get("type")),
                c.containsKey("display-format") ? (String) c.get("display-format") : "{amount}{symbol}",
                c.containsKey("use-decimals") && (Boolean) c.get("use-decimals"),
                c.containsKey("decimal-places") ? ((Number)c.get("decimal-places")).intValue() : 0,
                ((Number) c.get("max-balance")).doubleValue(),
                ((Number) c.get("starting-balance")).doubleValue()
            );
            currencies.put(id, currency);
            if (currency.getType() == CurrencyType.DIMENSION) dimensionCurrencies.add(id);
            if (currency.getType() == CurrencyType.UNIVERSAL) universalCurrencyId = id;
        }
    }

    public Currency getCurrency(String id) {
        return currencies.get(id);
    }

    public List<Currency> getAllCurrencies() {
        return new ArrayList<>(currencies.values());
    }

    public Currency getDimensionCurrency(String dimension) {
        for (Currency currency : currencies.values()) {
            if (currency.getDimension().equalsIgnoreCase(dimension) && currency.getType() == CurrencyType.DIMENSION) {
                return currency;
            }
        }
        return null;
    }

    public Currency getUniversalCurrency() {
        return currencies.get(universalCurrencyId);
    }

    // 플레이어 잔액
    public double getBalance(Player player, String currencyId) {
        return economyDataManager.getBalance(player, currencyId);
    }

    public void setBalance(Player player, String currencyId, double amount) {
        Currency currency = getCurrency(currencyId);
        if (currency == null) return;
        double val = Math.max(0, Math.min(amount, currency.getMaxBalance()));
        double old = getBalance(player, currencyId);
        economyDataManager.setBalance(player, currencyId, val);
        // 이벤트 발생 등 필요시 추가
    }

    public void addBalance(Player player, String currencyId, double amount) {
        double old = getBalance(player, currencyId);
        double newAmount = old + amount;
        setBalance(player, currencyId, newAmount);
    }

    public void removeBalance(Player player, String currencyId, double amount) {
        double old = getBalance(player, currencyId);
        double newAmount = Math.max(0, old - amount);
        setBalance(player, currencyId, newAmount);
    }

    public boolean hasEnough(Player player, String currencyId, double amount) {
        return getBalance(player, currencyId) >= amount;
    }

    public Map<String, Double> getAllBalances(Player player) {
        return economyDataManager.getAllBalances(player);
    }
}