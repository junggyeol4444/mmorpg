package com.multiverse.core.models;

import org.bukkit.Location;

import java.util.UUID;

public class Waypoint {
    private final int id;
    private final UUID owner;
    private String name;
    private String dimensionId;
    private Location location;
    private long createdAt;

    public Waypoint(int id, UUID owner, String name, String dimensionId, Location location, long createdAt) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.dimensionId = dimensionId;
        this.location = location;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) { this.name = name; }

    public String getDimensionId() {
        return dimensionId;
    }
    public void setDimensionId(String dimensionId) { this.dimensionId = dimensionId; }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) { this.location = location; }

    public long getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}