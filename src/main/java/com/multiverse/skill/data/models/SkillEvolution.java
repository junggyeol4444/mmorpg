package com.multiverse.skill.data. models;

import com.multiverse.skill.data.enums.EvolutionType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SkillEvolution {

    private String evolutionId;
    private String name;
    private String description;
    
    // 진화 관계
    private String fromSkillId;
    private String toSkillId;
    private EvolutionType type;
    
    // 요구사항
    private int requiredSkillLevel;
    private int requiredPlayerLevel;
    private int requiredUseCount;
    private List<ItemStack> requiredItems;
    private String requiredQuest;
    
    // 비용
    private double manaCost;
    private double goldCost;

    public SkillEvolution() {
        this.requiredItems = new ArrayList<>();
    }

    // Getters and Setters

    public String getEvolutionId() {
        return evolutionId;
    }

    public void setEvolutionId(String evolutionId) {
        this. evolutionId = evolutionId;
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

    public String getFromSkillId() {
        return fromSkillId;
    }

    public void setFromSkillId(String fromSkillId) {
        this.fromSkillId = fromSkillId;
    }

    public String getToSkillId() {
        return toSkillId;
    }

    public void setToSkillId(String toSkillId) {
        this.toSkillId = toSkillId;
    }

    public EvolutionType getType() {
        return type;
    }

    public void setType(EvolutionType type) {
        this.type = type;
    }

    public int getRequiredSkillLevel() {
        return requiredSkillLevel;
    }

    public void setRequiredSkillLevel(int requiredSkillLevel) {
        this.requiredSkillLevel = requiredSkillLevel;
    }

    public int getRequiredPlayerLevel() {
        return requiredPlayerLevel;
    }

    public void setRequiredPlayerLevel(int requiredPlayerLevel) {
        this.requiredPlayerLevel = requiredPlayerLevel;
    }

    public int getRequiredUseCount() {
        return requiredUseCount;
    }

    public void setRequiredUseCount(int requiredUseCount) {
        this.requiredUseCount = requiredUseCount;
    }

    public List<ItemStack> getRequiredItems() {
        return requiredItems;
    }

    public void setRequiredItems(List<ItemStack> requiredItems) {
        this.requiredItems = requiredItems;
    }

    public String getRequiredQuest() {
        return requiredQuest;
    }

    public void setRequiredQuest(String requiredQuest) {
        this.requiredQuest = requiredQuest;
    }

    public double getManaCost() {
        return manaCost;
    }

    public void setManaCost(double manaCost) {
        this.manaCost = manaCost;
    }

    public double getGoldCost() {
        return goldCost;
    }

    public void setGoldCost(double goldCost) {
        this.goldCost = goldCost;
    }

    /**
     * 필수 아이템 추가
     */
    public void addRequiredItem(ItemStack item) {
        requiredItems. add(item);
    }

    /**
     * 진화 조건 확인 (텍스트)
     */
    public String getRequirementsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("스킬 레벨: %d | ", requiredSkillLevel));
        sb.append(String.format("플레이어 레벨: %d | ", requiredPlayerLevel));
        sb.append(String.format("사용 횟수: %d회", requiredUseCount));
        
        if (!requiredItems. isEmpty()) {
            sb.append(String.format(" | 아이템: %d개", requiredItems.size()));
        }
        
        if (manaCost > 0) {
            sb. append(String.format(" | 마나: %. 0f", manaCost));
        }
        
        if (goldCost > 0) {
            sb.append(String.format(" | 골드: %.0f", goldCost));
        }
        
        return sb. toString();
    }

    /**
     * 진화 체인 문자열
     */
    public String getChainString() {
        return String.format("%s %s→ %s",
                fromSkillId,
                type.getDisplayName(),
                toSkillId);
    }

    /**
     * 진화 정보 문자열
     */
    public String getInfoString() {
        return String. format("%s: %s (%s)",
                name,
                getChainString(),
                type.getDisplayName());
    }

    @Override
    public String toString() {
        return "SkillEvolution{" +
                "evolutionId='" + evolutionId + '\'' +
                ", name='" + name + '\'' +
                ", from='" + fromSkillId + '\'' +
                ", to='" + toSkillId + '\'' +
                ", type=" + type +
                '}';
    }
}