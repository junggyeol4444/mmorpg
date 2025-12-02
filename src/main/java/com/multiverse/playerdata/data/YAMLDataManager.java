package com.multiverse.playerdata.data;

import com.multiverse.playerdata.PlayerDataCore;
import com.multiverse.playerdata.models.*;
import com.multiverse.playerdata.models.enums.StatType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class YAMLDataManager implements DataManager {

    private final PlayerDataCore plugin;
    private final File dataFolder;
    private final File playersFolder;
    private final File racesFile;
    private final File evolutionsFile;

    public YAMLDataManager(PlayerDataCore plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        this.playersFolder = new File(dataFolder, "players");
        if (!playersFolder.exists()) playersFolder.mkdirs();
        this.racesFile = new File(dataFolder, "data/races.yml");
        this.evolutionsFile = new File(dataFolder, "data/evolutions.yml");
        // transcendent_powers.yml, stat_formulas.yml 등도 필요 시 추가
    }

    // ==== 종족 데이터 ====
    @Override
    public Map<String, Object> loadRaceData() {
        Map<String, Object> map = new LinkedHashMap<>();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(racesFile);
        for (String raceId : yaml.getConfigurationSection("races").getKeys(false)) {
            map.put(raceId, yaml.getConfigurationSection("races." + raceId).getValues(true));
        }
        return map;
    }

    // ==== 진화 데이터 ====
    @Override
    public Map<String, Object> loadEvolutionData() {
        Map<String, Object> map = new LinkedHashMap<>();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(evolutionsFile);
        for (String evoId : yaml.getConfigurationSection("evolutions").getKeys(false)) {
            map.put(evoId, yaml.getConfigurationSection("evolutions." + evoId).getValues(true));
        }
        return map;
    }

    // ==== 플레이어 데이터 ====
    @Override
    public boolean playerDataExists(UUID uuid) {
        File playerFile = getPlayerFile(uuid);
        return playerFile.exists();
    }

    @Override
    public PlayerStats loadPlayerStats(UUID uuid) {
        File playerFile = getPlayerFile(uuid);
        if (!playerFile.exists()) return PlayerStats.createNew(uuid, null);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(playerFile);
        return PlayerStats.fromYaml(uuid, yaml);
    }

    @Override
    public void savePlayerStats(UUID uuid, PlayerStats stats) {
        File playerFile = getPlayerFile(uuid);
        YamlConfiguration yaml = new YamlConfiguration();
        stats.toYaml(yaml);
        try {
            yaml.save(playerFile);
        } catch (Exception e) {
            plugin.getLogger().warning("플레이어 데이터 저장 실패: " + uuid + ", " + e.getMessage());
        }
    }

    @Override
    public PlayerStats restorePlayerStats(UUID uuid, String backupFileName) {
        File backupFile = new File(dataFolder, "backups/" + backupFileName);
        if (!backupFile.exists()) return PlayerStats.createNew(uuid, null);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(backupFile);
        return PlayerStats.fromYaml(uuid, yaml);
    }

    // ==== 플레이어 종족 ====
    @Override
    public String getPlayerRaceId(UUID uuid) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(getPlayerFile(uuid));
        return yaml.getString("race.current", "human");
    }

    @Override
    public void setPlayerRaceId(UUID uuid, String raceId) {
        File playerFile = getPlayerFile(uuid);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(playerFile);
        yaml.set("race.current", raceId);
        try {
            yaml.save(playerFile);
        } catch (Exception e) {
            plugin.getLogger().warning("종족 데이터 저장 실패: " + uuid + ", " + e.getMessage());
        }
    }

    // ==== 플레이어 진화 이력 ====
    @Override
    public void addPlayerEvolutionHistory(UUID uuid, String fromRace, String toRace) {
        File playerFile = getPlayerFile(uuid);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(playerFile);
        List<Map<String, Object>> history;
        if (yaml.isList("race.evolution-history")) {
            history = (List<Map<String, Object>>) yaml.getList("race.evolution-history");
        } else {
            history = new ArrayList<>();
        }
        Map<String, Object> entry = new HashMap<>();
        entry.put("from", fromRace);
        entry.put("to", toRace);
        entry.put("date", System.currentTimeMillis());
        history.add(entry);
        yaml.set("race.evolution-history", history);
        try {
            yaml.save(playerFile);
        } catch (Exception e) {
            plugin.getLogger().warning("진화 이력 저장 실패: " + uuid + ", " + e.getMessage());
        }
    }

    // ==== 초월 데이터 ====
    @Override
    public Transcendence loadPlayerTranscendence(UUID uuid) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(getPlayerFile(uuid));
        return Transcendence.fromYaml(uuid, yaml);
    }

    @Override
    public void savePlayerTranscendence(UUID uuid, Transcendence transcendence) {
        File playerFile = getPlayerFile(uuid);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(playerFile);
        transcendence.toYaml(yaml);
        try {
            yaml.save(playerFile);
        } catch (Exception e) {
            plugin.getLogger().warning("초월 데이터 저장 실패: " + uuid + ", " + e.getMessage());
        }
    }

    // ==== 내부 유틸 ====
    private File getPlayerFile(UUID uuid) {
        return new File(playersFolder, uuid.toString() + ".yml");
    }
}