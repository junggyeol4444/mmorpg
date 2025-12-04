package com.multiverse.dungeon. data.model;

import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 던전 보상 데이터 클래스
 */
public class DungeonReward {

    // 경험치
    private int baseExperience; // 기본 경험치
    private int bossExperience; // 보스 처치 경험치

    // 화폐
    private Map<String, Double> money; // 화폐 맵 (화폐타입 -> 금액)
    private int dungeonPoints; // 던전 포인트

    // 아이템
    private List<ItemStack> guaranteedItems; // 확정 드롭 아이템
    private List<RewardItem> randomItems; // 랜덤 드롭 아이템

    // 칭호
    private String title; // 칭호

    // 첫 클리어 보너스
    private DungeonReward firstClearBonus; // 첫 클리어 시 추가 보상

    /**
     * 생성자
     */
    public DungeonReward() {
        this.baseExperience = 0;
        this.bossExperience = 0;
        this.money = new HashMap<>();
        this.dungeonPoints = 0;
        this. guaranteedItems = new ArrayList<>();
        this.randomItems = new ArrayList<>();
        this.title = null;
        this.firstClearBonus = null;
    }

    // ===== Getters & Setters =====

    public int getBaseExperience() {
        return baseExperience;
    }

    public void setBaseExperience(int baseExperience) {
        this.baseExperience = Math.max(0, baseExperience);
    }

    public int getBossExperience() {
        return bossExperience;
    }

    public void setBossExperience(int bossExperience) {
        this.bossExperience = Math.max(0, bossExperience);
    }

    public Map<String, Double> getMoney() {
        return money;
    }

    public void setMoney(Map<String, Double> money) {
        this.money = money != null ? money : new HashMap<>();
    }

    public void addMoney(String currencyType, double amount) {
        this.money.put(currencyType, this.money.getOrDefault(currencyType, 0. 0) + amount);
    }

    public double getMoney(String currencyType) {
        return money.getOrDefault(currencyType, 0.0);
    }

    public int getDungeonPoints() {
        return dungeonPoints;
    }

    public void setDungeonPoints(int dungeonPoints) {
        this.dungeonPoints = Math.max(0, dungeonPoints);
    }

    public List<ItemStack> getGuaranteedItems() {
        return guaranteedItems;
    }

    public void setGuaranteedItems(List<ItemStack> guaranteedItems) {
        this.guaranteedItems = guaranteedItems != null ? guaranteedItems : new ArrayList<>();
    }

    public void addGuaranteedItem(ItemStack item) {
        this. guaranteedItems.add(item);
    }

    public List<RewardItem> getRandomItems() {
        return randomItems;
    }

    public void setRandomItems(List<RewardItem> randomItems) {
        this. randomItems = randomItems != null ?  randomItems : new ArrayList<>();
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

    public DungeonReward getFirstClearBonus() {
        return firstClearBonus;
    }

    public void setFirstClearBonus(DungeonReward firstClearBonus) {
        this.firstClearBonus = firstClearBonus;
    }

    // ===== 비즈니스 로직 =====

    /**
     * 총 경험치
     *
     * @return 경험치 합계
     */
    public int getTotalExperience() {
        return baseExperience + bossExperience;
    }

    /**
     * 난이도 배율을 적용한 보상 계산
     *
     * @param multiplier 배율
     * @return 배율 적용된 보상 객체
     */
    public DungeonReward applyMultiplier(double multiplier) {
        DungeonReward scaled = new DungeonReward();
        
        scaled.baseExperience = (int) (this.baseExperience * multiplier);
        scaled.bossExperience = (int) (this.bossExperience * multiplier);
        
        // 화폐 적용
        for (Map.Entry<String, Double> entry : this.money.entrySet()) {
            scaled. addMoney(entry.getKey(), entry.getValue() * multiplier);
        }
        
        scaled.dungeonPoints = (int) (this.dungeonPoints * multiplier);
        scaled.guaranteedItems = new ArrayList<>(this. guaranteedItems);
        scaled.randomItems = new ArrayList<>(this.randomItems);
        scaled.title = this.title;
        
        return scaled;
    }

    /**
     * 첫 클리어 보너스 적용
     *
     * @return 첫 클리어 보너스가 적용된 보상
     */
    public DungeonReward withFirstClearBonus() {
        if (firstClearBonus == null) {
            return this;
        }

        DungeonReward combined = new DungeonReward();
        
        combined.baseExperience = this. baseExperience + firstClearBonus.baseExperience;
        combined.bossExperience = this.bossExperience + firstClearBonus.bossExperience;
        
        // 화폐 합산
        for (Map.Entry<String, Double> entry : this. money.entrySet()) {
            combined.addMoney(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Double> entry : firstClearBonus.money.entrySet()) {
            combined.addMoney(entry. getKey(), entry.getValue());
        }
        
        combined. dungeonPoints = this.dungeonPoints + firstClearBonus. dungeonPoints;
        combined. guaranteedItems. addAll(this.guaranteedItems);
        combined.guaranteedItems.addAll(firstClearBonus.guaranteedItems);
        combined.randomItems.addAll(this.randomItems);
        combined.randomItems.addAll(firstClearBonus.randomItems);
        
        return combined;
    }

    /**
     * 총 보상 아이템 개수 (확정 + 랜덤)
     *
     * @return 아이템 총 개수
     */
    public int getTotalItemCount() {
        int total = guaranteedItems.size();
        for (RewardItem item : randomItems) {
            if (item.shouldDrop()) {
                total++;
            }
        }
        return total;
    }

    /**
     * 랜덤 아이템 중 드롭될 아이템 생성
     *
     * @return 드롭될 아이템 목록
     */
    public List<ItemStack> generateRandomDrops() {
        List<ItemStack> drops = new ArrayList<>();
        for (RewardItem item : randomItems) {
            ItemStack drop = item.generateDropItem();
            if (drop != null) {
                drops.add(drop);
            }
        }
        return drops;
    }

    @Override
    public String toString() {
        return "DungeonReward{" +
                "baseExperience=" + baseExperience +
                ", bossExperience=" + bossExperience +
                ", money=" + money +
                ", dungeonPoints=" + dungeonPoints +
                ", guaranteedItems=" + guaranteedItems. size() +
                ", randomItems=" + randomItems.size() +
                ", title='" + title + '\'' +
                '}';
    }
}