package com.multiverse.playerdata.data.storage;

import com.multiverse.playerdata.models.PlayerStats;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class StatsStorage {

    private final File playerStatsFolder;

    public StatsStorage(File playerStatsFolder) {
        this.playerStatsFolder = playerStatsFolder;
        if (!playerStatsFolder.exists()) playerStatsFolder.mkdirs();
    }

    /**
     * 모든 플레이어 스탯을 로드
     */
    public Map<UUID, PlayerStats> loadAllPlayerStats() {
        Map<UUID, PlayerStats> map = new LinkedHashMap<>();
        File[] files = playerStatsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                PlayerStats stats = PlayerStats.fromYaml(uuid, yaml);
                map.put(uuid, stats);
            }
        }
        return map;
    }

    /**
     * 특정 플레이어 스탯 저장
     */
    public void savePlayerStats(UUID uuid, PlayerStats stats) {
        File file = new File(playerStatsFolder, uuid.toString() + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        stats.toYaml(yaml);
        try {
            yaml.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 플레이어 데이터 삭제
     */
    public void deletePlayerStats(UUID uuid) {
        File file = new File(playerStatsFolder, uuid.toString() + ".yml");
        if (file.exists()) file.delete();
    }
}