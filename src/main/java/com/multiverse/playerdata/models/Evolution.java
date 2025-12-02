package com.multiverse.playerdata.models;

import com.multiverse.playerdata.models.enums.EvolutionType;
import com.multiverse.playerdata.models.enums.RaceType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class Evolution {

    private final String id;
    private final String name;
    private final String description;
    private final EvolutionType evolutionType;
    private final String fromRaceId;
    private final String toRaceId;
    private final List<String> conditions;

    public Evolution(String id, String name, String description, EvolutionType evolutionType,
                     String fromRaceId, String toRaceId, List<String> conditions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.evolutionType = evolutionType;
        this.fromRaceId = fromRaceId;
        this.toRaceId = toRaceId;
        this.conditions = conditions != null ? conditions : new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public EvolutionType getEvolutionType() { return evolutionType; }
    public String getFromRaceId() { return fromRaceId; }
    public String getToRaceId() { return toRaceId; }
    public List<String> getConditions() { return new ArrayList<>(conditions); }

    // YAML 로드
    public static Evolution fromYaml(String id, Map<String, Object> data) {
        String name = (String)data.getOrDefault("name", id);
        String description = (String)data.getOrDefault("description", "");
        EvolutionType evolutionType = EvolutionType.valueOf(
                String.valueOf(data.getOrDefault("type", "NORMAL")).toUpperCase());
        String fromRaceId = (String)data.getOrDefault("fromRace", "");
        String toRaceId = (String)data.getOrDefault("toRace", "");
        List<String> condList = (List<String>)data.getOrDefault("conditions", new ArrayList<>());
        return new Evolution(id, name, description, evolutionType, fromRaceId, toRaceId, condList);
    }

    // YAML 저장
    public void toYaml(YamlConfiguration yaml, String prefix) {
        yaml.set(prefix + ".name", name);
        yaml.set(prefix + ".description", description);
        yaml.set(prefix + ".type", evolutionType.name());
        yaml.set(prefix + ".fromRace", fromRaceId);
        yaml.set(prefix + ".toRace", toRaceId);
        yaml.set(prefix + ".conditions", conditions);
    }
}