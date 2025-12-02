package com.multiverse.playerdata.models;

import com.multiverse.playerdata.models.enums.StatType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

public class PlayerStats {

    private final UUID uuid;
    private int level;
    private int exp;
    private int maxHp;
    private int currentHp;
    private Map<StatType, Integer> baseStats;
    private Map<String, Double> specialStats;
    private Set<String> completedQuests;

    public PlayerStats(UUID uuid, int level, int exp, int maxHp, int currentHp,
                      Map<StatType, Integer> baseStats, Map<String, Double> specialStats,
                      Set<String> completedQuests) {
        this.uuid = uuid;
        this.level = level;
        this.exp = exp;
        this.maxHp = maxHp;
        this.currentHp = currentHp;
        this.baseStats = baseStats != null ? baseStats : new EnumMap<>(StatType.class);
        this.specialStats = specialStats != null ? specialStats : new HashMap<>();
        this.completedQuests = completedQuests != null ? completedQuests : new HashSet<>();
    }

    public UUID getUuid() { return uuid; }
    public int getLevel() { return level; }
    public int getExp() { return exp; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public Map<StatType, Integer> getBaseStats() { return baseStats; }
    public Map<String, Double> getSpecialStats() { return specialStats; }
    public Set<String> getCompletedQuests() { return completedQuests; }

    // 신규 생성
    public static PlayerStats createNew(UUID uuid, Race race) {
        Map<StatType, Integer> stats = new EnumMap<>(StatType.class);
        for (StatType type : StatType.values()) {
            stats.put(type, 10); // 기본값
        }
        if (race != null) {
            for (Map.Entry<StatType, Integer> e : race.getStatBonus().entrySet()) {
                stats.put(e.getKey(), stats.getOrDefault(e.getKey(), 0) + e.getValue());
            }
        }
        return new PlayerStats(uuid, 1, 0, 100, 100, stats, new HashMap<>(), new HashSet<>());
    }

    // YAML 로드
    public static PlayerStats fromYaml(UUID uuid, YamlConfiguration yaml) {
        int level = yaml.getInt("stats.level", 1);
        int exp = yaml.getInt("stats.exp", 0);
        int maxHp = yaml.getInt("stats.maxHp", 100);
        int currentHp = yaml.getInt("stats.currentHp", maxHp);
        Map<StatType, Integer> baseStats = new EnumMap<>(StatType.class);

        Map<String, Object> statsMap = yaml.getConfigurationSection("stats.base") != null ?
            yaml.getConfigurationSection("stats.base").getValues(false) : new HashMap<>();
        for (String key : statsMap.keySet()) {
            try {
                StatType type = StatType.valueOf(key.toUpperCase());
                baseStats.put(type, ((Number) statsMap.get(key)).intValue());
            } catch (Exception e) { }
        }

        Map<String, Double> specialStats = new HashMap<>();
        if (yaml.isConfigurationSection("stats.special")) {
            Map<String, Object> specialMap = yaml.getConfigurationSection("stats.special").getValues(false);
            for (String sk : specialMap.keySet()) {
                specialStats.put(sk, ((Number)specialMap.get(sk)).doubleValue());
            }
        }
        Set<String> completedQuests = new HashSet<>(yaml.getStringList("stats.quests.completed"));

        return new PlayerStats(uuid, level, exp, maxHp, currentHp, baseStats, specialStats, completedQuests);
    }

    // YAML 저장
    public void toYaml(YamlConfiguration yaml) {
        yaml.set("stats.level", level);
        yaml.set("stats.exp", exp);
        yaml.set("stats.maxHp", maxHp);
        yaml.set("stats.currentHp", currentHp);

        Map<String, Integer> base = new HashMap<>();
        for (Map.Entry<StatType, Integer> e : baseStats.entrySet()) {
            base.put(e.getKey().name(), e.getValue());
        }
        yaml.set("stats.base", base);

        yaml.set("stats.special", specialStats);
        yaml.set("stats.quests.completed", new ArrayList<>(completedQuests));
    }
}