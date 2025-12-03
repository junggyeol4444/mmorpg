package com.multiverse.economy.data.storage;

import com.multiverse.economy.models.EconomyStatistics;

import java.util.HashMap;
import java.util.Map;

public class StatisticsStorage {

    // 통계 정보 저장소
    private final Map<String, EconomyStatistics> statisticsMap = new HashMap<>();

    public StatisticsStorage() { }

    public void saveStatistics(String currencyId, EconomyStatistics stat) {
        statisticsMap.put(currencyId, stat);
    }

    public EconomyStatistics getStatistics(String currencyId) {
        return statisticsMap.get(currencyId);
    }

    public void removeStatistics(String currencyId) {
        statisticsMap.remove(currencyId);
    }

    public Map<String, EconomyStatistics> getAllStatistics() {
        return statisticsMap;
    }

    public void loadAll(Map<String, EconomyStatistics> all) {
        statisticsMap.clear();
        statisticsMap.putAll(all);
    }

    public void clear() {
        statisticsMap.clear();
    }
}