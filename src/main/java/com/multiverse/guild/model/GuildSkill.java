package com.multiverse.guild.model;

import java.util.Map;

public class GuildSkill {
    private String skillId;
    private String name;
    private SkillCategory category;

    // 레벨
    private int currentLevel;
    private int maxLevel;

    // 비용
    private int requiredSkillPoints;
    private long requiredGold;

    // 효과
    private Map<String, Double> effects;

    // 선행 스킬
    private String prerequisiteSkill;
    private int prerequisiteLevel;

    public GuildSkill(String skillId, String name, SkillCategory category, int currentLevel, int maxLevel, int requiredSkillPoints, long requiredGold, Map<String, Double> effects, String prerequisiteSkill, int prerequisiteLevel) {
        this.skillId = skillId;
        this.name = name;
        this.category = category;
        this.currentLevel = currentLevel;
        this.maxLevel = maxLevel;
        this.requiredSkillPoints = requiredSkillPoints;
        this.requiredGold = requiredGold;
        this.effects = effects;
        this.prerequisiteSkill = prerequisiteSkill;
        this.prerequisiteLevel = prerequisiteLevel;
    }

    public String getSkillId() { return skillId; }
    public String getName() { return name; }
    public SkillCategory getCategory() { return category; }
    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }
    public int getMaxLevel() { return maxLevel; }
    public int getRequiredSkillPoints() { return requiredSkillPoints; }
    public long getRequiredGold() { return requiredGold; }
    public Map<String, Double> getEffects() { return effects; }
    public String getPrerequisiteSkill() { return prerequisiteSkill; }
    public int getPrerequisiteLevel() { return prerequisiteLevel; }
}