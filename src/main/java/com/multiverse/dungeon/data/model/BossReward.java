package com.multiverse.dungeon.data.model;

import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

/**
 * 보스 처치 보상 데이터 클래스
 */
public class BossReward {

    private int baseExperience; // 기본 경험치
    private int minGold; // 최소 골드
    private int maxGold; // 최대 골드
    
    private List<ItemStack> guaranteedItems; // 확정 드롭 아이템
    private List<RewardItem> randomItems; // 랜덤 드롭 아이템
    
    private String title; // 칭호
    private int dungeonPoints; // 던전 포인트

    /**
     * 기본 생성자
     */
    public BossReward() {
        this.baseExperience = 0;
        this.minGold = 0;
        this.maxGold = 0;
        this.guaranteedItems = new ArrayList<>();
        this.randomItems = new ArrayList<>();
        this.title = null;
        this.dungeonPoints = 0;
    }

    /**
     * 모든 필드를 포함한 생성자
     */
    public BossReward(int baseExperience, int minGold, int maxGold,
                      List<ItemStack> guaranteedItems, List<RewardItem> randomItems,
                      String title, int dungeonPoints) {
        this.baseExperience = baseExperience;
        this.minGold = minGold;
        this.maxGold = maxGold;
        this.guaranteedItems = guaranteedItems != null ? guaranteedItems : new ArrayList<>();
        this.randomItems = randomItems != null ? randomItems : new ArrayList<>();
        this.title = title;
        this.dungeonPoints = dungeonPoints;
    }

    // ===== Getters & Setters =====

    public int getBaseExperience() {
        return baseExperience;
    }

    public void setBaseExperience(int baseExperience) {
        this.baseExperience = baseExperience;
    }

    public int getMinGold() {
        return minGold;
    }

    public void setMinGold(int minGold) {
        this.minGold = minGold;
    }

    public int getMaxGold() {
        return maxGold;
    }

    public void setMaxGold(int maxGold) {
        this.maxGold = maxGold;
    }

    public List<ItemStack> getGuaranteedItems() {
        return guaranteedItems;
    }

    public void setGuaranteedItems(List<ItemStack> guaranteedItems) {
        this.guaranteedItems = guaranteedItems != null ? guaranteedItems : new ArrayList<>();
    }

    public void addGuaranteedItem(ItemStack item) {
        this.guaranteedItems.add(item);
    }

    public List<RewardItem> getRandomItems() {
        return randomItems;
    }

    public void setRandomItems(List<RewardItem> randomItems) {
        this.randomItems = randomItems != null ? randomItems : new ArrayList<>();
    }

    public void addRandomItem(RewardItem item) {
        this.randomItems.add(item);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDungeonPoints() {
        return dungeonPoints;
    }

    public void setDungeonPoints(int dungeonPoints) {
        this.dungeonPoints = dungeonPoints;
    }

    /**
     * 랜덤 골드 생성
     *
     * @return 생성된 골드
     */
    public int generateRandomGold() {
        if (minGold == maxGold) {
            return minGold;
        }
        return minGold + (int) (Math.random() * (maxGold - minGold + 1));
    }

    @Override
    public String toString() {
        return "BossReward{" +
                "baseExperience=" + baseExperience +
                ", minGold=" + minGold +
                ", maxGold=" + maxGold +
                ", guaranteedItems=" + guaranteedItems. size() +
                ", randomItems=" + randomItems.size() +
                ", title='" + title + '\'' +
                ", dungeonPoints=" + dungeonPoints +
                '}';
    }
}