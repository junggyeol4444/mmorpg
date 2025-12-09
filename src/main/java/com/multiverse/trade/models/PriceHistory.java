package com.multiverse.trade.models;

public class PriceHistory {

    private long timestamp;
    private double price;
    private int volume;

    public PriceHistory() {
        this.timestamp = System.currentTimeMillis();
        this.price = 0;
        this.volume = 0;
    }

    public PriceHistory(long timestamp, double price, int volume) {
        this.timestamp = timestamp;
        this.price = price;
        this.volume = volume;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public double getTotalValue() {
        return price * volume;
    }

    public long getAge() {
        return System.currentTimeMillis() - timestamp;
    }

    public boolean isWithinHours(int hours) {
        return getAge() <= (hours * 60L * 60L * 1000L);
    }

    public boolean isWithinDays(int days) {
        return getAge() <= (days * 24L * 60L * 60L * 1000L);
    }

    @Override
    public String toString() {
        return String.format("PriceHistory{timestamp=%d, price=%.2f, volume=%d}",
                timestamp, price, volume);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriceHistory that = (PriceHistory) o;

        if (timestamp != that. timestamp) return false;
        if (Double.compare(that. price, price) != 0) return false;
        return volume == that.volume;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (timestamp ^ (timestamp >>> 32));
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + volume;
        return result;
    }
}