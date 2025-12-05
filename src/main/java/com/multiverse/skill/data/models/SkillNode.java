package com.multiverse.skill.data.models;

import java.util.*;

public class SkillNode {

    private String nodeId;
    private String skillId;
    
    // 위치 (트리 내)
    private int tier;
    private int position;
    
    // 선행 스킬
    private List<String> prerequisites;
    private int requiredPoints;
    
    // 최대 레벨
    private int maxLevel;

    public SkillNode() {
        this.prerequisites = new ArrayList<>();
    }

    // Getters and Setters

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
        this.skillId = skillId;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<String> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<String> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    public void setRequiredPoints(int requiredPoints) {
        this.requiredPoints = requiredPoints;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * 선행 조건 추가
     */
    public void addPrerequisite(String nodeId) {
        if (! this.prerequisites.contains(nodeId)) {
            this.prerequisites.add(nodeId);
        }
    }

    /**
     * 선행 조건 제거
     */
    public void removePrerequisite(String nodeId) {
        this.prerequisites.remove(nodeId);
    }

    /**
     * 선행 조건 확인
     */
    public boolean hasPrerequisite(String nodeId) {
        return this.prerequisites.contains(nodeId);
    }

    @Override
    public String toString() {
        return "SkillNode{" +
                "nodeId='" + nodeId + '\'' +
                ", skillId='" + skillId + '\'' +
                ", tier=" + tier +
                ", position=" + position +
                '}';
    }
}