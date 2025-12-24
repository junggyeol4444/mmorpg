package com.multiverse.guild.model;

import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

public class GuildTerritory {
    private UUID territoryId;
    private UUID guildId;

    // 영역
    private String worldName;
    private Location corner1;
    private Location corner2;

    // 스폰
    private Location spawnPoint;

    // 건물
    private Map<BuildingType, GuildBuilding> buildings;

    // 설정
    private TerritorySettings settings;

    public GuildTerritory(UUID territoryId, UUID guildId, String worldName, Location corner1, Location corner2, Location spawnPoint, Map<BuildingType, GuildBuilding> buildings, TerritorySettings settings) {
        this.territoryId = territoryId;
        this.guildId = guildId;
        this.worldName = worldName;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.spawnPoint = spawnPoint;
        this.buildings = buildings;
        this.settings = settings;
    }

    public UUID getTerritoryId() { return territoryId; }
    public UUID getGuildId() { return guildId; }
    public String getWorldName() { return worldName; }
    public Location getCorner1() { return corner1; }
    public Location getCorner2() { return corner2; }
    public Location getSpawnPoint() { return spawnPoint; }
    public Map<BuildingType, GuildBuilding> getBuildings() { return buildings; }
    public TerritorySettings getSettings() { return settings; }
    public void setSettings(TerritorySettings settings) { this.settings = settings; }
}