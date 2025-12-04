package com.multiverse.dungeon. data.model;

import org. bukkit.inventory.ItemStack;

/**
 * 랜덤 보상 아이템 데이터 클래스
 */
public class RewardItem {

    private ItemStack item; // 아이템
    private double dropChance; // 드롭 확률 (%)
    private int minAmount; // 최소 개수
    private int maxAmount; // 최대 개수

    /**
     * 생성자
     */
    public RewardItem(ItemStack item, double dropChance, int minAmount, int maxAmount) {
        this.item = item;
        this.dropChance = dropChance;
        this.minAmount = Math.max(1, minAmount);
        this. maxAmount = Math.max(this.minAmount, maxAmount);
    }

    /**
     * 기본 생성자
     */
    public RewardItem() {
        this.item = null;
        this.dropChance = 0;
        this.minAmount = 1;
        this.maxAmount = 1;
    }

    // ===== Getters & Setters =====

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public double getDropChance() {
        return dropChance;
    }

    public void setDropChance(double dropChance) {
        this. dropChance = Math.max(0, Math.min(100, dropChance));
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = Math.max(1, minAmount);
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = Math.max(this.minAmount, maxAmount);
    }

    /**
     * 드롭 확률에 따라 드롭 여부 결정
     *
     * @return 드롭되면 true
     */
    public boolean shouldDrop() {
        return Math.random() * 100 < dropChance;
    }

    /**
     * 랜덤 개수 생성
     *
     * @return 생성된 개수
     */
    public int generateRandomAmount() {
        if (minAmount == maxAmount) {
            return minAmount;
        }
        return minAmount + (int) (Math.random() * (maxAmount - minAmount + 1));
    }

    /**
     * 드롭될 아이템 복사본 생성
     *
     * @return 생성된 아이템 (개수 포함)
     */
    public ItemStack generateDropItem() {
        if (item == null || !shouldDrop()) {
            return null;
        }

        ItemStack drop = item.clone();
        int amount = generateRandomAmount();
        drop.setAmount(amount);
        return drop;
    }

    @Override
    public String toString() {
        return "RewardItem{" +
                "dropChance=" + dropChance +
                ", minAmount=" + minAmount +
                ", maxAmount=" + maxAmount +
                '}';
    }
}