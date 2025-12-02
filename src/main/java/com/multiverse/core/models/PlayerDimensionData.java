package com.multiverse.core.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDimensionData {
    private final UUID uuid;
    private String name;
    private Map<String, DimensionVisitData> dimensionData = new HashMap<>();
    private WarmupData warmup;

    public PlayerDimensionData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) { this.name = name; }

    public Map<String, DimensionVisitData> getDimensionData() {
        return dimensionData;
    }
    public void setDimensionData(Map<String, DimensionVisitData> dimensionData) {
        this.dimensionData = dimensionData;
    }

    public WarmupData getWarmup() {
        return warmup;
    }
    public void setWarmup(WarmupData warmup) {
        this.warmup = warmup;
    }
}