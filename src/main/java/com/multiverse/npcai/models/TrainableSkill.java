package com.multiverse.npcai.models;

import java.util.*;

/**
 * NPC가 가르칠 수 있는 훈련 가능 스킬 항목
 */
public class TrainableSkill {
    private String skillId;
    private String displayName;
    private int requiredLevel;
    private double trainingCost;

    public TrainableSkill(String skillId, String displayName, int requiredLevel, double trainingCost) {
        this.skillId = skillId;
        this.displayName = displayName;
        this.requiredLevel = requiredLevel;
        this.trainingCost = trainingCost;
    }

    public String getSkillId() { return skillId; }
    public String getDisplayName() { return displayName; }
    public int getRequiredLevel() { return requiredLevel; }
    public double getTrainingCost() { return trainingCost; }

    // Map 직렬화/역직렬화
    public static TrainableSkill fromMap(Map<?, ?> map) {
        String skillId = (String) map.get("skillId");
        String displayName = (String) map.get("displayName");
        int requiredLevel = (int) map.getOrDefault("requiredLevel", 1);
        double trainingCost = (double) map.getOrDefault("trainingCost", 0.0);
        return new TrainableSkill(skillId, displayName, requiredLevel, trainingCost);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("skillId", skillId);
        map.put("displayName", displayName);
        map.put("requiredLevel", requiredLevel);
        map.put("trainingCost", trainingCost);
        return map;
    }
}