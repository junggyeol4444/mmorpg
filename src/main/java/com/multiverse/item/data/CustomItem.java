package com.multiverse.item. data;

import java.util.*;

public class CustomItem implements Cloneable {
    
    private String itemId;
    private String name;
    private String description;
    private org.bukkit.Material material;
    private ItemType type;
    private ItemRarity rarity;
    
    // 강화 정보
    private int enhanceLevel;
    private int maxEnhance;
    
    // 스탯 정보
    private Map<String, Double> baseStats;
    private List<ItemOption> options;
    
    // 소켓 정보
    private int sockets;
    private List<Gem> gems;
    
    // 세트 정보
    private String setId;
    
    // 요구사항
    private int requiredLevel;
    private String requiredClass;
    private String requiredRace;
    
    // 내구도
    private int durability;
    private int maxDurability;
    private boolean unbreakable;
    
    // 거래 정보
    private int tradeCount;
    private int maxTrades;
    private boolean soulbound;
    
    // 생성자
    public CustomItem() {
        this.enhanceLevel = 0;
        this.maxEnhance = 15;
        this.baseStats = new HashMap<>();
        this.options = new ArrayList<>();
        this. sockets = 0;
        this.gems = new ArrayList<>();
        this.tradeCount = 0;
        this.maxTrades = 3;
        this.soulbound = false;
        this.unbreakable = false;
    }
    
    // Getters and Setters
    public String getItemId() {
        return itemId;
    }
    
    public void setItemId(String itemId) {
        this.itemId = itemId;
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
    
    public org.bukkit.Material getMaterial() {
        return material;
    }
    
    public void setMaterial(org.bukkit.Material material) {
        this.material = material;
    }
    
    public ItemType getType() {
        return type;
    }
    
    public void setType(ItemType type) {
        this.type = type;
    }
    
    public ItemRarity getRarity() {
        return rarity;
    }
    
    public void setRarity(ItemRarity rarity) {
        this.rarity = rarity;
    }
    
    public int getEnhanceLevel() {
        return enhanceLevel;
    }
    
    public void setEnhanceLevel(int enhanceLevel) {
        this.enhanceLevel = Math.max(0, Math.min(enhanceLevel, maxEnhance));
    }
    
    public int getMaxEnhance() {
        return maxEnhance;
    }
    
    public void setMaxEnhance(int maxEnhance) {
        this.maxEnhance = maxEnhance;
    }
    
    public Map<String, Double> getBaseStats() {
        return baseStats;
    }
    
    public void setBaseStats(Map<String, Double> baseStats) {
        this. baseStats = baseStats;
    }
    
    public List<ItemOption> getOptions() {
        return options;
    }
    
    public void setOptions(List<ItemOption> options) {
        this.options = options;
    }
    
    public int getSockets() {
        return sockets;
    }
    
    public void setSockets(int sockets) {
        this. sockets = Math.max(0, sockets);
    }
    
    public List<Gem> getGems() {
        return gems;
    }
    
    public void setGems(List<Gem> gems) {
        this.gems = gems;
    }
    
    public String getSetId() {
        return setId;
    }
    
    public void setSetId(String setId) {
        this.setId = setId;
    }
    
    public int getRequiredLevel() {
        return requiredLevel;
    }
    
    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }
    
    public String getRequiredClass() {
        return requiredClass;
    }
    
    public void setRequiredClass(String requiredClass) {
        this.requiredClass = requiredClass;
    }
    
    public String getRequiredRace() {
        return requiredRace;
    }
    
    public void setRequiredRace(String requiredRace) {
        this.requiredRace = requiredRace;
    }
    
    public int getDurability() {
        return durability;
    }
    
    public void setDurability(int durability) {
        this. durability = Math.max(0, Math.min(durability, maxDurability));
    }
    
    public int getMaxDurability() {
        return maxDurability;
    }
    
    public void setMaxDurability(int maxDurability) {
        this.maxDurability = maxDurability;
        if (this.durability == 0) {
            this.durability = maxDurability;
        }
    }
    
    public boolean isUnbreakable() {
        return unbreakable;
    }
    
    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }
    
    public int getTradeCount() {
        return tradeCount;
    }
    
    public void setTradeCount(int tradeCount) {
        this.tradeCount = Math.max(0, tradeCount);
    }
    
    public int getMaxTrades() {
        return maxTrades;
    }
    
    public void setMaxTrades(int maxTrades) {
        this.maxTrades = maxTrades;
    }
    
    public boolean isSoulbound() {
        return soulbound;
    }
    
    public void setSoulbound(boolean soulbound) {
        this. soulbound = soulbound;
    }
    
    /**
     * CustomItem 복제
     */
    @Override
    public CustomItem clone() {
        try {
            CustomItem cloned = (CustomItem) super. clone();
            cloned.baseStats = new HashMap<>(this.baseStats);
            cloned.options = new ArrayList<>(this.options);
            cloned. gems = new ArrayList<>(this. gems);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("복제 실패", e);
        }
    }
    
    /**
     * CustomItem 정보 출력
     */
    @Override
    public String toString() {
        return "CustomItem{" +
                "itemId='" + itemId + '\'' +
                ", name='" + name + '\'' +
                ", rarity=" + rarity +
                ", enhanceLevel=" + enhanceLevel +
                ", sockets=" + sockets +
                ", options=" + (options != null ? options.size() : 0) +
                '}';
    }
}