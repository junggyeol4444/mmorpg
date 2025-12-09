package com.multiverse.party.models;

import java.util.*;

public class PartyLevel {
    private int level;
    private long experience;
    private int skillPoints;
    private int usedSkillPoints;
    private List<String> learnedSkills; // 습득한 스킬 ID 문자열 리스트

    // Getters & Setters
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public long getExperience() { return experience; }
    public void setExperience(long experience) { this.experience = experience; }
    public int getSkillPoints() { return skillPoints; }
    public void setSkillPoints(int skillPoints) { this.skillPoints = skillPoints; }
    public int getUsedSkillPoints() { return usedSkillPoints; }
    public void setUsedSkillPoints(int usedSkillPoints) { this.usedSkillPoints = usedSkillPoints; }
    public List<String> getLearnedSkills() { return learnedSkills == null ? (learnedSkills = new ArrayList<>()) : learnedSkills; }
    public void setLearnedSkills(List<String> learnedSkills) { this.learnedSkills = learnedSkills; }
}