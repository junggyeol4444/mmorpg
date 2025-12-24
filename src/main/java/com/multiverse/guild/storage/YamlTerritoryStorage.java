package com.multiverse.guild.storage;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.*;
import com.multiverse.guild.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class YamlTerritoryStorage {

    private final GuildCore plugin;
    private final File dir;

    public YamlTerritoryStorage(GuildCore plugin) {
        this.plugin = plugin;
        this.dir = new File(plugin.getDataFolder(), "territories");
        if (!dir.exists()) dir.mkdirs();
    }

    public Map<UUID, GuildTerritory> loadAll() {
        Map<UUID, GuildTerritory> map = new ConcurrentHashMap<>();
        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return map;
        for (File f : files) {
            YamlConfiguration yc = YamlConfiguration.loadConfiguration(f);
            GuildTerritory t = fromConfig(yc);
            if (t != null) map.put(t.getTerritoryId(), t);
        }
        return map;
    }

    public void save(GuildTerritory territory) {
        File f = new File(dir, territory.getTerritoryId().toString() + ".yml");
        YamlConfiguration yc = new YamlConfiguration();
        toConfig(territory, yc);
        try { yc.save(f); } catch (IOException e) { e.printStackTrace(); }
    }

    public void delete(UUID territoryId) {
        File f = new File(dir, territoryId.toString() + ".yml");
        if (f.exists()) f.delete();
    }

    /* ==== MAPPING ==== */
    private GuildTerritory fromConfig(YamlConfiguration c) {
        String tid = c.getString("territory.territory-id");
        if (tid == null) return null;
        UUID territoryId = UUID.fromString(tid);
        UUID guildId = UUID.fromString(c.getString("territory.guild-id", territoryId.toString()));
        String world = c.getString("territory.world", "world");
        Location c1 = LocationUtil.loc(world, c.getDouble("territory.corner1.x", 0), c.getDouble("territory.corner1.y", 0), c.getDouble("territory.corner1.z", 0));
        Location c2 = LocationUtil.loc(world, c.getDouble("territory.corner2.x", 0), c.getDouble("territory.corner2.y", 255), c.getDouble("territory.corner2.z", 0));
        Location spawn = LocationUtil.loc(world, c.getDouble("territory.spawn-point.x", 0), c.getDouble("territory.spawn-point.y", 64), c.getDouble("territory.spawn-point.z", 0),
                (float) c.getDouble("territory.spawn-point.yaw", 0), (float) c.getDouble("territory.spawn-point.pitch", 0));

        Map<BuildingType, GuildBuilding> buildings = new ConcurrentHashMap<>();
        if (c.isConfigurationSection("territory.buildings")) {
            for (String key : c.getConfigurationSection("territory.buildings").getKeys(false)) {
                BuildingType bt;
                try { bt = BuildingType.valueOf(key); } catch (Exception e) { continue; }
                String p = "territory.buildings." + key;
                int lv = c.getInt(p + ".level", 1);
                Location loc = LocationUtil.loc(world, c.getDouble(p + ".location.x", 0), c.getDouble(p + ".location.y", 0), c.getDouble(p + ".location.z", 0));
                Map<String, Double> eff = new ConcurrentHashMap<>();
                if (c.isConfigurationSection(p + ".effects")) {
                    for (String ek : c.getConfigurationSection(p + ".effects").getKeys(false)) {
                        eff.put(ek, c.getDouble(p + ".effects." + ek, 0.0));
                    }
                }
                buildings.put(bt, new GuildBuilding(bt, lv, loc, eff));
            }
        }

        TerritorySettings settings = new TerritorySettings(
                c.getBoolean("territory.settings.pvp-enabled", false),
                c.getBoolean("territory.settings.mob-spawn", false),
                c.getBoolean("territory.settings.block-break", false),
                c.getBoolean("territory.settings.public-access", false)
        );

        return new GuildTerritory(territoryId, guildId, world, c1, c2, spawn, buildings, settings);
    }

    private void toConfig(GuildTerritory t, YamlConfiguration c) {
        c.set("territory.territory-id", t.getTerritoryId().toString());
        c.set("territory.guild-id", t.getGuildId().toString());
        c.set("territory.world", t.getWorldName());
        c.set("territory.corner1.x", t.getCorner1().getX());
        c.set("territory.corner1.y", t.getCorner1().getY());
        c.set("territory.corner1.z", t.getCorner1().getZ());
        c.set("territory.corner2.x", t.getCorner2().getX());
        c.set("territory.corner2.y", t.getCorner2().getY());
        c.set("territory.corner2.z", t.getCorner2().getZ());
        c.set("territory.spawn-point.x", t.getSpawnPoint().getX());
        c.set("territory.spawn-point.y", t.getSpawnPoint().getY());
        c.set("territory.spawn-point.z", t.getSpawnPoint().getZ());
        c.set("territory.spawn-point.yaw", t.getSpawnPoint().getYaw());
        c.set("territory.spawn-point.pitch", t.getSpawnPoint().getPitch());

        for (Map.Entry<BuildingType, GuildBuilding> e : t.getBuildings().entrySet()) {
            String p = "territory.buildings." + e.getKey().name();
            GuildBuilding b = e.getValue();
            c.set(p + ".type", b.getType().name());
            c.set(p + ".level", b.getLevel());
            c.set(p + ".location.x", b.getLocation().getX());
            c.set(p + ".location.y", b.getLocation().getY());
            c.set(p + ".location.z", b.getLocation().getZ());
            for (Map.Entry<String, Double> eff : b.getEffects().entrySet()) {
                c.set(p + ".effects." + eff.getKey(), eff.getValue());
            }
        }

        TerritorySettings s = t.getSettings();
        c.set("territory.settings.pvp-enabled", s.isPvpEnabled());
        c.set("territory.settings.mob-spawn", s.isMobSpawn());
        c.set("territory.settings.block-break", s.isBlockBreak());
        c.set("territory.settings.public-access", s.isPublicAccess());
    }
}