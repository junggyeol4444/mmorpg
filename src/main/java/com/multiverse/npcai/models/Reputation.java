package com.multiverse.npcai.models;

import com.multiverse.npcai.models.enums.ReputationLevel;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * 플레이어-NPC 호감도 데이터
 */
public class Reputation {

    private UUID playerUUID;
    private int npcId;
    private int points;
    private ReputationLevel level;
    private long lastChanged;

    public Reputation(UUID playerUUID, int npcId, int points, ReputationLevel level, long lastChanged) {
        this.playerUUID = playerUUID;
        this.npcId = npcId;
        this.points = points;
        this.level = level;
        this.lastChanged = lastChanged;
    }

    public UUID getPlayerUUID() { return playerUUID; }
    public int getNpcId() { return npcId; }
    public int getPoints() { return points; }
    public ReputationLevel getLevel() { return level; }
    public long getLastChanged() { return lastChanged; }

    public void setPoints(int points) { this.points = points; }
    public void setLevel(ReputationLevel level) { this.level = level; }
    public void setLastChanged(long lastChanged) { this.lastChanged = lastChanged; }

    // === YAML 직렬화/역직렬화 ===
    public static Reputation fromYAML(ConfigurationSection yml) {
        UUID playerUUID = UUID.fromString(yml.getString("playerUUID"));
        int npcId = yml.getInt("npcId");
        int points = yml.getInt("points");
        ReputationLevel level = ReputationLevel.valueOf(yml.getString("level", "NEUTRAL"));
        long lastChanged = yml.getLong("lastChanged", System.currentTimeMillis());
        return new Reputation(playerUUID, npcId, points, level, lastChanged);
    }

    public ConfigurationSection toYAML(ConfigurationSection yml) {
        yml.set("playerUUID", playerUUID.toString());
        yml.set("npcId", npcId);
        yml.set("points", points);
        yml.set("level", level.name());
        yml.set("lastChanged", lastChanged);
        return yml;
    }
}