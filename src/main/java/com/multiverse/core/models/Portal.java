package com.multiverse.core.models;

import com.multiverse.core.models.enums.PortalType;
import org.bukkit.Location;

public class Portal {
    private final int id;
    private String name;
    private String fromDimension;
    private String toDimension;
    private String worldName;
    private Location location;
    private PortalType type;
    private int cost;
    private boolean active;

    public Portal(int id, String name, String fromDimension, String toDimension, String worldName,
                  Location location, PortalType type, int cost, boolean active) {
        this.id = id;
        this.name = name;
        this.fromDimension = fromDimension;
        this.toDimension = toDimension;
        this.worldName = worldName;
        this.location = location;
        this.type = type;
        this.cost = cost;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) { this.name = name; }

    public String getFromDimension() {
        return fromDimension;
    }
    public void setFromDimension(String fromDimension) { this.fromDimension = fromDimension; }

    public String getToDimension() {
        return toDimension;
    }
    public void setToDimension(String toDimension) { this.toDimension = toDimension; }

    public String getWorldName() {
        return worldName;
    }
    public void setWorldName(String worldName) { this.worldName = worldName; }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) { this.location = location; }

    public PortalType getType() {
        return type;
    }
    public void setType(PortalType type) { this.type = type; }

    public int getCost() {
        return cost;
    }
    public void setCost(int cost) { this.cost = cost; }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) { this.active = active; }
}