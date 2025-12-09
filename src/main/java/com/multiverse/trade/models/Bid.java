package com. multiverse.trade. models;

import java.util.UUID;

public class Bid {

    private UUID bidder;
    private double amount;
    private long timestamp;
    private boolean isAutoBid;

    public Bid() {
        this. timestamp = System.currentTimeMillis();
        this.isAutoBid = false;
    }

    public Bid(UUID bidder, double amount) {
        this. bidder = bidder;
        this. amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.isAutoBid = false;
    }

    public Bid(UUID bidder, double amount, boolean isAutoBid) {
        this. bidder = bidder;
        this. amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.isAutoBid = isAutoBid;
    }

    public UUID getBidder() {
        return bidder;
    }

    public void setBidder(UUID bidder) {
        this.bidder = bidder;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isAutoBid() {
        return isAutoBid;
    }

    public void setAutoBid(boolean autoBid) {
        isAutoBid = autoBid;
    }

    public long getAge() {
        return System.currentTimeMillis() - timestamp;
    }

    @Override
    public String toString() {
        return String.format("Bid{bidder=%s, amount=%.2f, timestamp=%d, autoBid=%s}",
                bidder, amount, timestamp, isAutoBid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bid bid = (Bid) o;

        if (Double.compare(bid. amount, amount) != 0) return false;
        if (timestamp != bid.timestamp) return false;
        return bidder != null ? bidder.equals(bid.bidder) : bid.bidder == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = bidder != null ? bidder.hashCode() : 0;
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}