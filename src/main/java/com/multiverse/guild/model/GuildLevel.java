package com.multiverse.guild.model;

import java.util.HashMap;
import java.util.Map;

public class GuildLevel {
    private int level;
    private long experience;
    private long expToNext;

    // 스킬 포인트
    private int skillPoints;
    private int usedSkillPoints;

    // 보너스
    private Map<String, Double> bonuses = new HashMap<>();

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public long getExperience() { return experience; }
    public void setExperience(long experience) { this.experience = experience; }
    public long getExpToNext() { return expToNext; }
    public void setExpToNext(long expToNext) { this.expToNext = expToNext; }
    public int getSkillPoints() { return skillPoints; }
    public void setSkillPoints(int skillPoints) { this.skillPoints = skillPoints; }
    public int getUsedSkillPoints() { return usedSkillPoints; }
    public void setUsedSkillPoints(int usedSkillPoints) { this.usedSkillPoints = usedSkillPoints; }
    public Map<String, Double> getBonuses() { return bonuses; }
    public void setBonuses(Map<String, Double> bonuses) { this.bonuses = bonuses; }
}