package com.multiverse.economy.data.storage;

import com.multiverse.economy.models.Currency;

import java.util.HashMap;
import java.util.Map;

public class CurrencyStorage {

    // 화폐 정보 메모리 저장소 (파일 등으로 연동될 수 있음)
    private final Map<String, Currency> currencyMap = new HashMap<>();

    public CurrencyStorage() { }

    public void saveCurrency(Currency currency) {
        currencyMap.put(currency.getId(), currency);
    }

    public Currency getCurrency(String currencyId) {
        return currencyMap.get(currencyId);
    }

    public void removeCurrency(String currencyId) {
        currencyMap.remove(currencyId);
    }

    public Map<String, Currency> getAllCurrencies() {
        return currencyMap;
    }

    public void loadAll(Map<String, Currency> all) {
        currencyMap.clear();
        currencyMap.putAll(all);
    }

    public void clear() {
        currencyMap.clear();
    }
}