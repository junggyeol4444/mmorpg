package com.multiverse.economy.data.storage;

import com.multiverse.economy.models.ExchangeRate;

import java.util.HashMap;
import java.util.Map;

public class ExchangeRateStorage {

    // 환율 정보 저장소 (from-to 키로 관리)
    private final Map<String, ExchangeRate> rateMap = new HashMap<>();

    public ExchangeRateStorage() { }

    public void saveRate(ExchangeRate rate) {
        rateMap.put(rate.getFromCurrency() + "_" + rate.getToCurrency(), rate);
    }

    public ExchangeRate getRate(String from, String to) {
        return rateMap.get(from + "_" + to);
    }

    public void removeRate(String from, String to) {
        rateMap.remove(from + "_" + to);
    }

    public Map<String, ExchangeRate> getAllRates() {
        return rateMap;
    }

    public void loadAll(Map<String, ExchangeRate> all) {
        rateMap.clear();
        rateMap.putAll(all);
    }

    public void clear() {
        rateMap.clear();
    }
}