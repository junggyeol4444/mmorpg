package com. multiverse.pvp.data;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java. util.HashMap;
import java. util.List;
import java.util. Map;

public class PvPReward {

    // 경험치
    private long experience;

    // 화폐 (화폐 종류 -> 금액)
    private Map<String, Double> money;

    // PvP 포인트
    private int pvpPoints;

    // 레이팅 변화
    private int ratingChange;

    // 아이템
    private List<ItemStack> items;

    // 칭호
    private String title;

    // 배율
    private double multiplier;

    // 보너스 정보
    private String bonusReason;
    private boolean isBonus;

    public PvPReward() {
        this.experience = 0;
        this. money = new HashMap<>();
        this.pvpPoints = 0;
        this. ratingChange = 0;
        this.items = new ArrayList<>();
        this.title = null;
        this. multiplier = 1.0;
        this.bonusReason = null;
        this.isBonus = false;
    }

    public PvPReward(long experience, double defaultMoney, int pvpPoints) {
        this();
        this.experience = experience;
        this.money.put("default", defaultMoney);
        this.pvpPoints = pvpPoints;
    }

    // ==================== Getters ====================

    public long getExperience() {
        return experience;
    }

    public Map<String, Double> getMoney() {
        return money;
    }

    public int getPvpPoints() {
        return pvpPoints;
    }

    public int getRatingChange() {
        return ratingChange;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public String getTitle() {
        return title;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getBonusReason() {
        return bonusReason;
    }

    public boolean isBonus() {
        return isBonus;
    }

    // ==================== Setters ====================

    public void setExperience(long experience) {
        this. experience = experience;
    }

    public void setMoney(Map<String, Double> money) {
        this.money = money;
    }

    public void setPvpPoints(int pvpPoints) {
        this.pvpPoints = pvpPoints;
    }

    public void setRatingChange(int ratingChange) {
        this.ratingChange = ratingChange;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public void setBonusReason(String bonusReason) {
        this.bonusReason = bonusReason;
    }

    public void setBonus(boolean bonus) {
        isBonus = bonus;
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 경험치 추가
     */
    public void addExperience(long amount) {
        this.experience += amount;
    }

    /**
     * 화폐 추가
     */
    public void addMoney(String currency, double amount) {
        this.money. put(currency, this.money.getOrDefault(currency, 0.0) + amount);
    }

    /**
     * 기본 화폐 추가
     */
    public void addDefaultMoney(double amount) {
        addMoney("default", amount);
    }

    /**
     * 기본 화폐 조회
     */
    public double getDefaultMoney() {
        return money.getOrDefault("default", 0.0);
    }

    /**
     * 특정 화폐 조회
     */
    public double getMoney(String currency) {
        return money.getOrDefault(currency, 0.0);
    }

    /**
     * PvP 포인트 추가
     */
    public void addPvpPoints(int amount) {
        this.pvpPoints += amount;
    }

    /**
     * 레이팅 변화 설정
     */
    public void addRatingChange(int amount) {
        this.ratingChange += amount;
    }

    /**
     * 아이템 추가
     */
    public void addItem(ItemStack item) {
        if (item != null) {
            this. items.add(item. clone());
        }
    }

    /**
     * 아이템 목록 추가
     */
    public void addItems(List<ItemStack> items) {
        if (items != null) {
            for (ItemStack item :  items) {
                addItem(item);
            }
        }
    }

    /**
     * 배율 적용
     */
    public void applyMultiplier(double multiplier) {
        this.multiplier *= multiplier;
        this.experience = (long) (this.experience * multiplier);
        
        for (Map.Entry<String, Double> entry : money.entrySet()) {
            money.put(entry. getKey(), entry.getValue() * multiplier);
        }
        
        this.pvpPoints = (int) (this.pvpPoints * multiplier);
    }

    /**
     * 다른 보상과 합치기
     */
    public void merge(PvPReward other) {
        if (other == null) {
            return;
        }

        this.experience += other.experience;
        
        for (Map. Entry<String, Double> entry : other. money.entrySet()) {
            addMoney(entry. getKey(), entry.getValue());
        }
        
        this.pvpPoints += other.pvpPoints;
        this. ratingChange += other.ratingChange;
        
        addItems(other.items);
        
        if (other.title != null && this.title == null) {
            this.title = other.title;
        }
    }

    /**
     * 보상이 비어있는지 확인
     */
    public boolean isEmpty() {
        if (experience > 0) return false;
        if (pvpPoints > 0) return false;
        if (ratingChange != 0) return false;
        if (! items.isEmpty()) return false;
        if (title != null) return false;
        
        for (double amount : money.values()) {
            if (amount > 0) return false;
        }
        
        return true;
    }

    /**
     * 보상 초기화
     */
    public void clear() {
        this.experience = 0;
        this. money.clear();
        this.pvpPoints = 0;
        this.ratingChange = 0;
        this. items.clear();
        this.title = null;
        this.multiplier = 1.0;
        this.bonusReason = null;
        this. isBonus = false;
    }

    /**
     * 보상 복사본 생성
     */
    public PvPReward clone() {
        PvPReward clone = new PvPReward();
        clone.experience = this.experience;
        clone.money = new HashMap<>(this.money);
        clone.pvpPoints = this.pvpPoints;
        clone.ratingChange = this.ratingChange;
        
        for (ItemStack item :  this.items) {
            clone.items.add(item. clone());
        }
        
        clone.title = this. title;
        clone.multiplier = this.multiplier;
        clone.bonusReason = this. bonusReason;
        clone.isBonus = this. isBonus;
        
        return clone;
    }

    /**
     * 보상 요약 문자열
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        
        if (experience > 0) {
            sb.append("&e경험치: &f+").append(experience).append("\n");
        }
        
        for (Map.Entry<String, Double> entry : money.entrySet()) {
            if (entry.getValue() > 0) {
                sb.append("&e").append(entry.getKey()).append(": &f+")
                  .append(String.format("%.1f", entry.getValue())).append("\n");
            }
        }
        
        if (pvpPoints > 0) {
            sb. append("&ePvP 포인트: &f+").append(pvpPoints).append("\n");
        }
        
        if (ratingChange != 0) {
            String prefix = ratingChange > 0 ? "+" :  "";
            sb. append("&e레이팅:  &f").append(prefix).append(ratingChange).append("\n");
        }
        
        if (!items.isEmpty()) {
            sb. append("&e아이템: &f").append(items.size()).append("개\n");
        }
        
        if (title != null) {
            sb.append("&e칭호:  &f").append(title).append("\n");
        }
        
        if (isBonus && bonusReason != null) {
            sb.append("&a보너스: &f").append(bonusReason).append("\n");
        }
        
        return sb.toString();
    }

    /**
     * 빌더 패턴
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PvPReward reward;

        public Builder() {
            this. reward = new PvPReward();
        }

        public Builder experience(long experience) {
            reward.setExperience(experience);
            return this;
        }

        public Builder money(String currency, double amount) {
            reward.addMoney(currency, amount);
            return this;
        }

        public Builder defaultMoney(double amount) {
            reward.addDefaultMoney(amount);
            return this;
        }

        public Builder pvpPoints(int points) {
            reward.setPvpPoints(points);
            return this;
        }

        public Builder ratingChange(int change) {
            reward. setRatingChange(change);
            return this;
        }

        public Builder item(ItemStack item) {
            reward.addItem(item);
            return this;
        }

        public Builder items(List<ItemStack> items) {
            reward. addItems(items);
            return this;
        }

        public Builder title(String title) {
            reward.setTitle(title);
            return this;
        }

        public Builder multiplier(double multiplier) {
            reward.setMultiplier(multiplier);
            return this;
        }

        public Builder bonus(String reason) {
            reward.setBonus(true);
            reward.setBonusReason(reason);
            return this;
        }

        public PvPReward build() {
            return reward;
        }
    }

    @Override
    public String toString() {
        return "PvPReward{" +
                "experience=" + experience +
                ", money=" + money +
                ", pvpPoints=" + pvpPoints +
                ", ratingChange=" + ratingChange +
                ", itemCount=" + items. size() +
                ", title='" + title + '\'' +
                ", multiplier=" + multiplier +
                '}';
    }
}