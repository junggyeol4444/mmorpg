package com.multiverse.core.models;

import com.multiverse.core.models.enums.DimensionType;

import java.util.List;

public class Dimension {
    private final String id;
    private String name;
    private String worldName;
    private DimensionType type;
    private int balanceValue;
    private double timeMultiplier;
    private boolean active;
    private int levelRequirement;
    private String questRequirement;
    private List<String> connectedDimensions;

    public Dimension(String id, String name, String worldName, DimensionType type,
                     int balanceValue, double timeMultiplier, boolean active,
                     int levelRequirement, String questRequirement, List<String> connectedDimensions) {
        this.id = id;
        this.name = name;
        this.worldName = worldName;
        this.type = type;
        this.balanceValue = balanceValue;
        this.timeMultiplier = timeMultiplier;
        this.active = active;
        this.levelRequirement = levelRequirement;
        this.questRequirement = questRequirement;
        this.connectedDimensions = connectedDimensions;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) { this.name = name; }

    public String getWorldName() {
        return worldName;
    }
    public void setWorldName(String worldName) { this.worldName = worldName; }

    public DimensionType getType() {
        return type;
    }
    public void setType(DimensionType type) { this.type = type; }

    public int getBalanceValue() {
        return balanceValue;
    }
    public void setBalanceValue(int balanceValue) { this.balanceValue = balanceValue; }

    public double getTimeMultiplier() {
        return timeMultiplier;
    }
    public void setTimeMultiplier(double timeMultiplier) { this.timeMultiplier = timeMultiplier; }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) { this.active = active; }

    public int getLevelRequirement() {
        return levelRequirement;
    }
    public void setLevelRequirement(int levelRequirement) { this.levelRequirement = levelRequirement; }

    public String getQuestRequirement() {
        return questRequirement;
    }
    public void setQuestRequirement(String questRequirement) { this.questRequirement = questRequirement; }

    public List<String> getConnectedDimensions() {
        return connectedDimensions;
    }
    public void setConnectedDimensions(List<String> connectedDimensions) { this.connectedDimensions = connectedDimensions; }
}