package com.multiverse.economy.data.storage;

import com.multiverse.economy.models.InflationControl;

import java.util.HashMap;
import java.util.Map;

public class InflationStorage {

    // 인플레이션 통제 정보 저장소
    private final Map<String, InflationControl> controlMap = new HashMap<>();

    public InflationStorage() { }

    public void saveControl(String currencyId, InflationControl control) {
        controlMap.put(currencyId, control);
    }

    public InflationControl getControl(String currencyId) {
        return controlMap.get(currencyId);
    }

    public void removeControl(String currencyId) {
        controlMap.remove(currencyId);
    }

    public Map<String, InflationControl> getAllControls() {
        return controlMap;
    }

    public void loadAll(Map<String, InflationControl> all) {
        controlMap.clear();
        controlMap.putAll(all);
    }

    public void clear() {
        controlMap.clear();
    }
}