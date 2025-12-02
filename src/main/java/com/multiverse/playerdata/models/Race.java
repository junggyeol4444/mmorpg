package com.multiverse.playerdata.models;

import com.multiverse.playerdata.models.enums.RaceType;
import com.multiverse.playerdata.models.enums.StatType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class Race {

    private final String id;
    private final String name;
    private final String description;
    private final RaceType type;
    private final List<String> abilities;
    private final Map<StatType, Integer> statBonus;

    public Race(String id, String name, String description, RaceType type,
                List<String> abilities, Map<StatType, Integer> statBonus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.abilities = abilities != null ? abilities : new ArrayList<>();
        this.statBonus = statBonus != null ? statBonus : new EnumMap<>(StatType.class);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public RaceType getType() {
        return type;
    }

    public List<String> getAbilities() {
        return new ArrayList<>(abilities);
    }

    public Map<StatType, Integer> getStatBonus() {
        return new EnumMap<>(statBonus);
    }

    // YAML 로드
    public static Race fromYaml(String id, Map<String, Object> data) {
        String name = (String) data.getOrDefault("name", id);
        String description = (String) data.getOrDefault("description", "");
        RaceType type = RaceType.valueOf(String.valueOf(data.getOrDefault("type", "NORMAL")).toUpperCase());
        List<String> abilities = (List<String>) data.getOrDefault("abilities", new ArrayList<>());
        Map<StatType, Integer> statBonus = new EnumMap<>(StatType.class);
        Map<String, Object> statMap = (Map<String, Object>) data.getOrDefault("statBonus", new HashMap<>());
        for (String key : statMap.keySet()) {
            try {
                StatType st = StatType.valueOf(key.toUpperCase());
                statBonus.put(st, ((Number) statMap.get(key)).intValue());
            } catch (Exception e) {
                // ignore unknown stat
            }
        }
        return new Race(id, name, description, type, abilities, statBonus);
    }

    // YAML 저장
    public void toYaml(YamlConfiguration yaml, String prefix) {
        yaml.set(prefix + ".name", name);
        yaml.set(prefix + ".description", description);
        yaml.set(prefix + ".type", type.name());
        yaml.set(prefix + ".abilities", abilities);
        Map<String, Integer> stats = new HashMap<>();
        for (Map.Entry<StatType, Integer> entry : statBonus.entrySet()) {
            stats.put(entry.getKey().name(), entry.getValue());
        }
        yaml.set(prefix + ".statBonus", stats);
    }
}