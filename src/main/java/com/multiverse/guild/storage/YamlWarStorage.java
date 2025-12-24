package com.multiverse.guild.storage;

import com.multiverse.guild.GuildCore;
import com.multiverse.guild.model.GuildWar;
import com.multiverse.guild.model.WarStatus;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class YamlWarStorage {

    private final GuildCore plugin;
    private final File dirActive;
    private final File dirPending;
    private final File dirHistory;

    public YamlWarStorage(GuildCore plugin) {
        this.plugin = plugin;
        this.dirActive = new File(plugin.getDataFolder(), "wars/active");
        this.dirPending = new File(plugin.getDataFolder(), "wars/pending");
        this.dirHistory = new File(plugin.getDataFolder(), "wars/history");
        dirActive.mkdirs(); dirPending.mkdirs(); dirHistory.mkdirs();
    }

    public Map<UUID, GuildWar> loadActive() {
        return loadDir(dirActive);
    }

    public void saveActive(GuildWar war) { saveToDir(dirActive, war); }

    public void deleteActive(UUID warId) {
        File f = new File(dirActive, warId.toString() + ".yml");
        if (f.exists()) f.delete();
    }

    public void savePending(GuildWar war) { saveToDir(dirPending, war); }

    public void deletePending(UUID warId) {
        File f = new File(dirPending, warId.toString() + ".yml");
        if (f.exists()) f.delete();
    }

    public void appendHistory(GuildWar war) { saveToDir(dirHistory, war); }

    /* ==== internal ==== */
    private Map<UUID, GuildWar> loadDir(File dir) {
        Map<UUID, GuildWar> map = new ConcurrentHashMap<>();
        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return map;
        for (File f : files) {
            YamlConfiguration yc = YamlConfiguration.loadConfiguration(f);
            GuildWar w = fromConfig(yc);
            if (w != null) map.put(w.getWarId(), w);
        }
        return map;
    }

    private void saveToDir(File dir, GuildWar war) {
        File f = new File(dir, war.getWarId().toString() + ".yml");
        YamlConfiguration yc = new YamlConfiguration();
        toConfig(war, yc);
        try { yc.save(f); } catch (IOException e) { e.printStackTrace(); }
    }

    private GuildWar fromConfig(YamlConfiguration c) {
        String wid = c.getString("war.war-id");
        if (wid == null) return null;
        UUID warId = UUID.fromString(wid);
        UUID attacker = UUID.fromString(c.getString("war.attacker", warId.toString()));
        UUID defender = UUID.fromString(c.getString("war.defender", warId.toString()));
        int aScore = c.getInt("war.attacker-score", 0);
        int dScore = c.getInt("war.defender-score", 0);
        int target = c.getInt("war.target-score", 100);
        long start = c.getLong("war.start-time", 0);
        long end = c.getLong("war.end-time", 0);
        int duration = c.getInt("war.duration", 3600);
        WarStatus status = WarStatus.valueOf(c.getString("war.status", "DECLARED"));
        Map<UUID, Integer> kills = new ConcurrentHashMap<>();
        Map<UUID, Integer> deaths = new ConcurrentHashMap<>();
        if (c.isConfigurationSection("war.kills")) {
            for (String k : c.getConfigurationSection("war.kills").getKeys(false)) {
                kills.put(UUID.fromString(k), c.getInt("war.kills." + k, 0));
            }
        }
        if (c.isConfigurationSection("war.deaths")) {
            for (String k : c.getConfigurationSection("war.deaths").getKeys(false)) {
                deaths.put(UUID.fromString(k), c.getInt("war.deaths." + k, 0));
            }
        }
        return new GuildWar(warId, attacker, defender, aScore, dScore, target, start, end, duration, status, kills, deaths);
    }

    private void toConfig(GuildWar w, YamlConfiguration c) {
        c.set("war.war-id", w.getWarId().toString());
        c.set("war.attacker", w.getAttackerGuildId().toString());
        c.set("war.defender", w.getDefenderGuildId().toString());
        c.set("war.attacker-score", w.getAttackerScore());
        c.set("war.defender-score", w.getDefenderScore());
        c.set("war.target-score", w.getTargetScore());
        c.set("war.start-time", w.getStartTime());
        c.set("war.end-time", w.getEndTime());
        c.set("war.duration", w.getDuration());
        c.set("war.status", w.getStatus().name());
        w.getKills().forEach((k, v) -> c.set("war.kills." + k, v));
        w.getDeaths().forEach((k, v) -> c.set("war.deaths." + k, v));
    }
}