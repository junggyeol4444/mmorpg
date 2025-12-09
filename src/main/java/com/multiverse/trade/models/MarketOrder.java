package com.multiverse.trade.models;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MarketOrder {

    private UUID orderId;
    private UUID player;
    private OrderType type;
    
    private ItemStack item;
    private int amount;
    private int remainingAmount;
    private double pricePerUnit;
    
    private long createTime;
    private long expiryTime;
    
    private OrderStatus status;

    public MarketOrder() {
        this. amount = 0;
        this. remainingAmount = 0;
        this.pricePerUnit = 0;
        this.createTime = System.currentTimeMillis();
        this.expiryTime = 0;
        this.status = OrderStatus.ACTIVE;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(int remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this. pricePerUnit = pricePerUnit;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this. expiryTime = expiryTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double getTotalValue() {
        return pricePerUnit * amount;
    }

    public double getRemainingValue() {
        return pricePerUnit * remainingAmount;
    }

    public double getFilledValue() {
        return pricePerUnit * (amount - remainingAmount);
    }

    public int getFilledAmount() {
        return amount - remainingAmount;
    }

    public double getFillPercentage() {
        if (amount == 0) {
            return 0;
        }
        return ((double) getFilledAmount() / amount) * 100.0;
    }

    public boolean isFilled() {
        return remainingAmount <= 0;
    }

    public boolean isPartiallyFilled() {
        return remainingAmount > 0 && remainingAmount < amount;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expiryTime;
    }

    public boolean isActive() {
        return status == OrderStatus.ACTIVE || status == OrderStatus. PARTIAL;
    }

    public long getTimeRemaining() {
        long remaining = expiryTime - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    public void fill(int amount) {
        this.remainingAmount = Math.max(0, this. remainingAmount - amount);
        if (this.remainingAmount <= 0) {
            this. status = OrderStatus. FILLED;
        } else {
            this.status = OrderStatus.PARTIAL;
        }
    }

    public boolean isSellOrder() {
        return type == OrderType. SELL;
    }

    public boolean isBuyOrder() {
        return type == OrderType.BUY;
    }

    public String getShortId() {
        return orderId.toString().substring(0, 8);
    }

    public boolean canMatch(MarketOrder other) {
        if (this.type == other.type) {
            return false;
        }

        if (this. type == OrderType. SELL) {
            return this.pricePerUnit <= other.pricePerUnit;
        } else {
            return this.pricePerUnit >= other.pricePerUnit;
        }
    }
}