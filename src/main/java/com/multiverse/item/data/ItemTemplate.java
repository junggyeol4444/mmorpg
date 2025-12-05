package com. multiverse.item. data;

import java.util.*;

public class ItemTemplate {
    
    private String templateId;
    private String name;
    private String description;
    private org.bukkit.Material material;
    private ItemType type;
    
    // 스탯 정보
    private Map<String, StatRange> statRanges;
    
    // 강화 정보
    private int maxEnhance;
    
    // 소켓 정보
    private int sockets;
    private List<SocketColor> socketColors;
    
    // 요구사항
    private int requiredLevel;
    private String requiredClass;
    private String requiredRace;
    
    // 내구도
    private int maxDurability;
    private boolean unbreakable;
    
    // 옵션 정보
    private List<String> optionIds;
    private int optionCount;
    
    // 세트 정보
    private String setId;
    
    /**
     * 기본 생성자
     */
    public ItemTemplate() {
        this.statRanges = new HashMap<>();
        this.socketColors = new ArrayList<>();
        this.optionIds = new ArrayList<>();
        this.maxEnhance = 15;
        this.sockets = 0;
        this.maxDurability = 100;
        this.unbreakable = false;
        this.optionCount = 0;
    }
    
    // Getters and Setters
    public String getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
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
    
    public org.bukkit. Material getMaterial() {
        return material;
    }
    
    public void setMaterial(org.bukkit.Material material) {
        this.material = material;
    }
    
    public ItemType getType() {
        return type;
    }
    
    public void setType(ItemType type) {
        this. type = type;
    }
    
    public Map<String, StatRange> getStatRanges() {
        return statRanges;
    }
    
    public void setStatRanges(Map<String, StatRange> statRanges) {
        this.statRanges = statRanges;
    }
    
    public void addStatRange(String statName, StatRange range) {
        statRanges.put(statName, range);
    }
    
    public int getMaxEnhance() {
        return maxEnhance;
    }
    
    public void setMaxEnhance(int maxEnhance) {
        this.maxEnhance = Math.max(0, maxEnhance);
    }
    
    public int getSockets() {
        return sockets;
    }
    
    public void setSockets(int sockets) {
        this.sockets = Math.max(0, sockets);
    }
    
    public List<SocketColor> getSocketColors() {
        return socketColors;
    }
    
    public void setSocketColors(List<SocketColor> socketColors) {
        this.socketColors = socketColors;
    }
    
    public void addSocketColor(SocketColor color) {
        socketColors.add(color);
    }
    
    public int getRequiredLevel() {
        return requiredLevel;
    }
    
    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = Math.max(0, requiredLevel);
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
    
    public int getMaxDurability() {
        return maxDurability;
    }
    
    public void setMaxDurability(int maxDurability) {
        this.maxDurability = Math.max(0, maxDurability);
    }
    
    public boolean isUnbreakable() {
        return unbreakable;
    }
    
    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }
    
    public List<String> getOptionIds() {
        return optionIds;
    }
    
    public void setOptionIds(List<String> optionIds) {
        this.optionIds = optionIds;
    }
    
    public void addOptionId(String optionId) {
        optionIds. add(optionId);
    }
    
    public int getOptionCount() {
        return optionCount;
    }
    
    public void setOptionCount(int optionCount) {
        this.optionCount = Math. max(0, optionCount);
    }
    
    public String getSetId() {
        return setId;
    }
    
    public void setSetId(String setId) {
        this.setId = setId;
    }
    
    /**
     * 템플릿 정보 출력
     */
    @Override
    public String toString() {
        return "ItemTemplate{" +
                "templateId='" + templateId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", maxEnhance=" + maxEnhance +
                ", sockets=" + sockets +
                ", requiredLevel=" + requiredLevel +
                '}';
    }
}