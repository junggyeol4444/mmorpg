package com.multiverse.playerdata.models;

import org.bukkit.World;

import java.util.*;

public class PlayerDimensionData {

    private final UUID playerUuid;
    private final Map<String, Integer> visits;
    private String lastWorldName;

    public PlayerDimensionData(UUID playerUuid) {
        this(playerUuid, new HashMap<>(), null);
    }

    public PlayerDimensionData(UUID playerUuid, Map<String, Integer> visits, String lastWorldName) {
        this.playerUuid = playerUuid;
        this.visits = visits != null ? visits : new HashMap<>();
        this.lastWorldName = lastWorldName;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Map<String, Integer> getVisits() {
        return visits;
    }

    public String getLastWorldName() {
        return lastWorldName;
    }

    public void setLastWorldName(String lastWorldName) {
        this.lastWorldName = lastWorldName;
    }

    public void addVisit(World world) {
        addVisit(world.getName());
    }

    public void addVisit(String worldName) {
        visits.put(worldName, visits.getOrDefault(worldName, 0) + 1);
        lastWorldName = worldName;
    }
}