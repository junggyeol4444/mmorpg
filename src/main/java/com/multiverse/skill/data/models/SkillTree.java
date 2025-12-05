package com.multiverse.skill.data. models;

import com.multiverse.skill.data.enums. SkillTreeType;
import java.util.*;

public class SkillTree {

    private String treeId;
    private String name;
    private SkillTreeType type;
    
    private String description;
    private List<String> lore;
    
    // 스킬 노드
    private Map<String, SkillNode> nodes;
    
    // 최대 포인트
    private int maxPoints;
    
    // 조건
    private int requiredLevel;
    private String requiredClass;

    public SkillTree() {
        this.nodes = new HashMap<>();
    }

    // Getters and Setters

    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SkillTreeType getType() {
        return type;
    }

    public void setType(SkillTreeType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this. lore = lore;
    }

    public Map<String, SkillNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, SkillNode> nodes) {
        this.nodes = nodes;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
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

    /**
     * 노드 추가
     */
    public void addNode(SkillNode node) {
        this.nodes.put(node.getNodeId(), node);
    }

    /**
     * 노드 조회
     */
    public SkillNode getNode(String nodeId) {
        return this.nodes.get(nodeId);
    }

    /**
     * 노드 제거
     */
    public void removeNode(String nodeId) {
        this.nodes.remove(nodeId);
    }

    /**
     * 트리의 모든 스킬 조회
     */
    public List<String> getAllSkillIds() {
        List<String> skillIds = new ArrayList<>();
        for (SkillNode node : nodes.values()) {
            if (node.getSkillId() != null && ! node.getSkillId().isEmpty()) {
                skillIds.add(node.getSkillId());
            }
        }
        return skillIds;
    }

    @Override
    public String toString() {
        return "SkillTree{" +
                "treeId='" + treeId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", maxPoints=" + maxPoints +
                ", nodeCount=" + nodes.size() +
                '}';
    }
}