package com.multiverse.guild.model;

import org.bukkit.Location;

import java.util.Map;

public class GuildBuilding {
    private BuildingType type;
    private int level;
    private Location location;
    private Map<String, Double> effects;

    public GuildBuilding(BuildingType type, int level, Location location, Map<String, Double> effects) {
        this.type = type;
        this.level = level;
        this.location = location;
        this.effects = effects;
    }

    public BuildingType getType() { return type; }
    public void setType(BuildingType type) { this.type = type; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public Map<String, Double> getEffects() { return effects; }
    public void setEffects(Map<String, Double> effects) { this.effects = effects; }
}