package com.multiverse.playerdata.data.storage;

import com.multiverse.playerdata.models.Race;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class RaceStorage {

    private final File raceFile;

    public RaceStorage(File raceFile) {
        this.raceFile = raceFile;
    }

    /**
     * 모든 종족 데이터 로드
     */
    public Map<String, Race> loadAllRaces() {
        Map<String, Race> raceMap = new LinkedHashMap<>();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(raceFile);

        if (yaml.isConfigurationSection("races")) {
            for (String id : yaml.getConfigurationSection("races").getKeys(false)) {
                Map<String, Object> data = yaml.getConfigurationSection("races." + id).getValues(true);
                Race race = Race.fromYaml(id, data);
                raceMap.put(id, race);
            }
        }
        return raceMap;
    }

    /**
     * 특정 종족 저장 또는 추가
     */
    public void saveRace(Race race) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(raceFile);
        race.toYaml(yaml, "races." + race.getId());
        try {
            yaml.save(raceFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 종족 삭제
     */
    public void deleteRace(String id) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(raceFile);
        yaml.set("races." + id, null);
        try {
            yaml.save(raceFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}