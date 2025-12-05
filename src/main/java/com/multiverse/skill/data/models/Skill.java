package com. multiverse.skill. data.models;

import com.multiverse.skill.data.enums.SkillType;
import java.util.*;

public class Skill {

    private String skillId;
    private String name;
    private String description;
    private List<String> lore;
    private SkillType type;
    private String category;

    // 레벨
    private int maxLevel;

    // 요구사항
    private int requiredLevel;
    private String requiredClass;
    private List<String> prerequisites;

    // 비용
    private double baseCost;
    private double costPerLevel;
    private String costType;

    // 쿨다운
    private long baseCooldown;
    private long cooldownReductionPerLevel;

    // 캐스팅
    private long castTime;
    private boolean channeling;
    private boolean cancelOnMove;
    private boolean cancelOnDamage;

    // 효과
    private List<SkillEffect> effects;
    private SkillEffect defaultEffect;

    // 스킬 트리
    private String skillTreeId;

    // 기본값
    private double baseValue;
    private double perLevelValue;

    // Getters and Setters

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
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
        this. description = description;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public SkillType getType() {
        return type;
    }

    public void setType(SkillType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
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

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(double baseCost) {
        this.baseCost = baseCost;
    }

    public double getCostPerLevel() {
        return costPerLevel;
    }

    public void setCostPerLevel(double costPerLevel) {
        this.costPerLevel = costPerLevel;
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public long getBaseCooldown() {
        return baseCooldown;
    }

    public void setBaseCooldown(long baseCooldown) {
        this.baseCooldown = baseCooldown;
    }

    public long getCooldownReductionPerLevel() {
        return cooldownReductionPerLevel;
    }

    public void setCooldownReductionPerLevel(long cooldownReductionPerLevel) {
        this.cooldownReductionPerLevel = cooldownReductionPerLevel;
    }

    public long getCastTime() {
        return castTime;
    }

    public void setCastTime(long castTime) {
        this.castTime = castTime;
    }

    public boolean isChanneling() {
        return channeling;
    }

    public void setChanneling(boolean channeling) {
        this.channeling = channeling;
    }

    public boolean isCancelOnMove() {
        return cancelOnMove;
    }

    public void setCancelOnMove(boolean cancelOnMove) {
        this.cancelOnMove = cancelOnMove;
    }

    public boolean isCancelOnDamage() {
        return cancelOnDamage;
    }

    public void setCancelOnDamage(boolean cancelOnDamage) {
        this.cancelOnDamage = cancelOnDamage;
    }

    public List<SkillEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<SkillEffect> effects) {
        this.effects = effects;
    }

    public SkillEffect getDefaultEffect() {
        return defaultEffect;
    }

    public void setDefaultEffect(SkillEffect defaultEffect) {
        this.defaultEffect = defaultEffect;
    }

    public String getSkillTreeId() {
        return skillTreeId;
    }

    public void setSkillTreeId(String skillTreeId) {
        this.skillTreeId = skillTreeId;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(double baseValue) {
        this.baseValue = baseValue;
    }

    public double getPerLevelValue() {
        return perLevelValue;
    }

    public void setPerLevelValue(double perLevelValue) {
        this.perLevelValue = perLevelValue;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "skillId='" + skillId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", maxLevel=" + maxLevel +
                '}';
    }
}