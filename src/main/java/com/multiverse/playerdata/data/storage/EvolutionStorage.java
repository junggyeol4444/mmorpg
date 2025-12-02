package com.multiverse.playerdata.data.storage;

import com.multiverse.playerdata.models.Evolution;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class EvolutionStorage {

    private final File evolutionFile;

    public EvolutionStorage(File evolutionFile) {
        this.evolutionFile = evolutionFile;
    }

    /**
     * 모든 진화 데이터 로드
     */
    public Map<String, Evolution> loadAllEvolutions() {
        Map<String, Evolution> map = new LinkedHashMap<>();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(evolutionFile);

        if (yaml.isConfigurationSection("evolutions")) {
            for (String id : yaml.getConfigurationSection("evolutions").getKeys(false)) {
                Map<String, Object> data = yaml.getConfigurationSection("evolutions." + id).getValues(true);
                Evolution evo = Evolution.fromYaml(id, data);
                map.put(id, evo);
            }
        }
        return map;
    }

    /**
     * 진화 정보 저장 또는 추가
     */
    public void saveEvolution(Evolution evo) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(evolutionFile);
        evo.toYaml(yaml, "evolutions." + evo.getId());
        try {
            yaml.save(evolutionFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 진화 정보 삭제
     */
    public void deleteEvolution(String id) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(evolutionFile);
        yaml.set("evolutions." + id, null);
        try {
            yaml.save(evolutionFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}