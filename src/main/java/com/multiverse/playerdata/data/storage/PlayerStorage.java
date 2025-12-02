package com.multiverse.playerdata.data.storage;

import com.multiverse.playerdata.models.PlayerStats;
import com.multiverse.playerdata.models.Transcendence;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class PlayerStorage {

    private final File playersFolder;

    public PlayerStorage(File playersFolder) {
        this.playersFolder = playersFolder;
        if (!playersFolder.exists()) playersFolder.mkdirs();
    }

    /**
     * 개별 플레이어 데이터 로드
     */
    public PlayerStats loadPlayerStats(UUID uuid) {
        File file = new File(playersFolder, uuid.toString() + ".yml");
        if (!file.exists()) return PlayerStats.createNew(uuid, null);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        return PlayerStats.fromYaml(uuid, yaml);
    }

    public void savePlayerStats(UUID uuid, PlayerStats stats) {
        File file = new File(playersFolder, uuid.toString() + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        stats.toYaml(yaml);
        try {
            yaml.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Transcendence loadPlayerTranscendence(UUID uuid) {
        File file = new File(playersFolder, uuid.toString() + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        return Transcendence.fromYaml(uuid, yaml);
    }

    public void savePlayerTranscendence(UUID uuid, Transcendence transc) {
        File file = new File(playersFolder, uuid.toString() + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        transc.toYaml(yaml);
        try {
            yaml.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean exists(UUID uuid) {
        File file = new File(playersFolder, uuid.toString() + ".yml");
        return file.exists();
    }

    public void delete(UUID uuid) {
        File file = new File(playersFolder, uuid.toString() + ".yml");
        if (file.exists()) file.delete();
    }
}