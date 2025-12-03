package com.multiverse.npcai.models;

import com.multiverse.npcai.models.enums.NPCType;
import com.multiverse.npcai.models.NPCAIBehavior;
import org.bukkit.Location;

import java.util.*;

/**
 * NPC 기본 데이터 (설정/상태/위치)
 */
public class NPCData {
    private int npcId;
    private String name;
    private String customId;
    private NPCType type;
    private Location location;
    private NPCAIBehavior behavior;

    public NPCData(int npcId, String name, NPCType type, Location location) {
        this.npcId = npcId;
        this.name = name;
        this.type = type;
        this.location = location;
        this.customId = "";
    }

    public int getNpcId() { return npcId; }
    public String getName() { return name; }
    public NPCType getType() { return type; }
    public Location getLocation() { return location; }
    public String getCustomId() { return customId; }
    public NPCAIBehavior getBehavior() { return behavior; }
    public void setBehavior(NPCAIBehavior behavior) { this.behavior = behavior; }
    public void setLocation(Location location) { this.location = location; }
    public void setCustomId(String customId) { this.customId = customId; }

    // === YAML 직렬화/역직렬화 ===
    public static NPCData fromYAML(org.bukkit.configuration.file.YamlConfiguration yml) {
        int id = yml.getInt("npcId");
        String name = yml.getString("name");
        NPCType type = NPCType.valueOf(yml.getString("type"));
        Location loc = (Location) yml.get("location");
        NPCData npc = new NPCData(id, name, type, loc);
        npc.setCustomId(yml.getString("customId", ""));
        if (yml.contains("behavior")) {
            npc.setBehavior(NPCAIBehavior.fromYAML(yml.getConfigurationSection("behavior")));
        }
        return npc;
    }

    public org.bukkit.configuration.file.YamlConfiguration toYAML() {
        org.bukkit.configuration.file.YamlConfiguration yml = new org.bukkit.configuration.file.YamlConfiguration();
        yml.set("npcId", npcId);
        yml.set("name", name);
        yml.set("type", type.name());
        yml.set("location", location);
        yml.set("customId", customId);
        if (behavior != null) yml.createSection("behavior");
        if (behavior != null) behavior.toYAML(yml.getConfigurationSection("behavior"));
        return yml;
    }
}