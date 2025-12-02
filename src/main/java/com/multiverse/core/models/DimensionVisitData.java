package com.multiverse.core.models;

import java.util.List;

public class DimensionVisitData {
    private long lastVisit;
    private int visitCount;
    private List<String> discoveredRegions;

    public DimensionVisitData(long lastVisit, int visitCount, List<String> discoveredRegions) {
        this.lastVisit = lastVisit;
        this.visitCount = visitCount;
        this.discoveredRegions = discoveredRegions;
    }

    public long getLastVisit() {
        return lastVisit;
    }
    public void setLastVisit(long lastVisit) {
        this.lastVisit = lastVisit;
    }

    public int getVisitCount() {
        return visitCount;
    }
    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public List<String> getDiscoveredRegions() {
        return discoveredRegions;
    }
    public void setDiscoveredRegions(List<String> discoveredRegions) {
        this.discoveredRegions = discoveredRegions;
    }
}