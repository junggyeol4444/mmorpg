package com.multiverse.core.models;

public class WarmupData {
    private boolean active;
    private String destination;
    private long startTime;

    public WarmupData(boolean active, String destination, long startTime) {
        this.active = active;
        this.destination = destination;
        this.startTime = startTime;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}