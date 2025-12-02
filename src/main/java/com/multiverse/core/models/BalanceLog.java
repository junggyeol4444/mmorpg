package com.multiverse.core.models;

public class BalanceLog {
    private final String dimension;
    private final int oldValue;
    private final int newValue;
    private final int delta;
    private final String reason;
    private final String changedBy;
    private final long timestamp;

    public BalanceLog(String dimension, int oldValue, int newValue, int delta, String reason, String changedBy, long timestamp) {
        this.dimension = dimension;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.delta = delta;
        this.reason = reason;
        this.changedBy = changedBy;
        this.timestamp = timestamp;
    }

    public String getDimension() {
        return dimension;
    }

    public int getOldValue() {
        return oldValue;
    }

    public int getNewValue() {
        return newValue;
    }

    public int getDelta() {
        return delta;
    }

    public String getReason() {
        return reason;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public long getTimestamp() {
        return timestamp;
    }
}