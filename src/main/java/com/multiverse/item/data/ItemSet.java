package com.multiverse.item.data;

import java.util.*;

public class ItemSet {
    
    private String setId;
    private String name;
    private String description;
    private List<String> itemIds; // 이 세트에 속한 아이템들
    private List<SetBonus> bonuses; // 세트 보너스 (개수별)
    private int requiredLevel;
    private String imageUrl;
    
    /**
     * 기본 생성자
     */
    public ItemSet() {
        this.itemIds = new ArrayList<>();
        this.bonuses = new ArrayList<>();
        this.requiredLevel = 0;
    }
    
    // Getters and Setters
    public String getSetId() {
        return setId;
    }
    
    public void setSetId(String setId) {
        this.setId = setId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getItemIds() {
        return itemIds;
    }
    
    public void setItemIds(List<String> itemIds) {
        this.itemIds = itemIds;
    }
    
    public void addItemId(String itemId) {
        itemIds.add(itemId);
    }
    
    public List<SetBonus> getBonuses() {
        return bonuses;
    }
    
    public void setBonuses(List<SetBonus> bonuses) {
        this.bonuses = bonuses;
    }
    
    public void addBonus(SetBonus bonus) {
        bonuses.add(bonus);
    }
    
    public int getRequiredLevel() {
        return requiredLevel;
    }
    
    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = Math.max(0, requiredLevel);
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    /**
     * 세트에 포함된 아이템 개수
     */
    public int getTotalItemCount() {
        return itemIds.size();
    }
    
    /**
     * 특정 개수에 해당하는 보너스 조회
     */
    public SetBonus getBonusByCount(int count) {
        for (SetBonus bonus : bonuses) {
            if (bonus. getRequiredCount() == count) {
                return bonus;
            }
        }
        return null;
    }
    
    /**
     * 활성화된 최대 보너스 조회
     */
    public SetBonus getMaxActivatedBonus(int equippedCount) {
        SetBonus maxBonus = null;
        
        for (SetBonus bonus : bonuses) {
            if (bonus.getRequiredCount() <= equippedCount) {
                if (maxBonus == null || bonus.getRequiredCount() > maxBonus.getRequiredCount()) {
                    maxBonus = bonus;
                }
            }
        }
        
        return maxBonus;
    }
    
    /**
     * 세트 완성도 (%)
     */
    public double getCompletionPercentage(int equippedCount) {
        return (double) equippedCount / getTotalItemCount() * 100;
    }
    
    /**
     * 세트 완성 여부
     */
    public boolean isCompleted(int equippedCount) {
        return equippedCount >= getTotalItemCount();
    }
    
    /**
     * 다음 보너스 조건
     */
    public int getNextBonusRequirement(int equippedCount) {
        int nextRequirement = getTotalItemCount() + 1;
        
        for (SetBonus bonus : bonuses) {
            if (bonus.getRequiredCount() > equippedCount && bonus.getRequiredCount() < nextRequirement) {
                nextRequirement = bonus.getRequiredCount();
            }
        }
        
        return nextRequirement;
    }
    
    /**
     * ItemSet 정보 출력
     */
    @Override
    public String toString() {
        return "ItemSet{" +
                "setId='" + setId + '\'' +
                ", name='" + name + '\'' +
                ", itemCount=" + getTotalItemCount() +
                ", bonusCount=" + bonuses.size() +
                ", requiredLevel=" + requiredLevel +
                '}';
    }
}