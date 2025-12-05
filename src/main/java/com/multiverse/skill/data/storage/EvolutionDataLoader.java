package com.multiverse.skill.data.storage;

import com.multiverse.skill. SkillCore;
import com.multiverse. skill.data.models.*;
import com.multiverse.skill.data.enums.EvolutionType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io. File;
import java.util.*;

/**
 * 스킬 진화 데이터 로더
 */
public class EvolutionDataLoader {

    private final SkillCore plugin;
    private final DataStorage storage;
    private final Map<String, SkillEvolution> evolutionCache;

    public EvolutionDataLoader(SkillCore plugin, DataStorage storage) {
        this.plugin = plugin;
        this.storage = storage;
        this.evolutionCache = new HashMap<>();
    }

    /**
     * 모든 진화 데이터 로드
     */
    public List<SkillEvolution> loadAllEvolutions() {
        List<SkillEvolution> evolutions = new ArrayList<>();
        File evolutionsFolder = new File(plugin.getDataFolder(), "evolutions");

        if (!evolutionsFolder.exists()) {
            plugin.getLogger().warning("⚠️ 진화 폴더가 없습니다: " + evolutionsFolder.getPath());
            return evolutions;
        }

        File[] evolutionFiles = evolutionsFolder.listFiles((d, name) -> name.endsWith(".yml"));
        if (evolutionFiles == null) {
            return evolutions;
        }

        for (File evolutionFile : evolutionFiles) {
            try {
                SkillEvolution evolution = loadEvolutionFromFile(evolutionFile);
                if (evolution != null) {
                    evolutions.add(evolution);
                    evolutionCache.put(evolution.getEvolutionId(), evolution);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("진화 데이터 로드 실패: " + evolutionFile. getName());
                e.printStackTrace();
            }
        }

        return evolutions;
    }

    /**
     * 파일에서 진화 데이터 로드
     */
    private SkillEvolution loadEvolutionFromFile(File file) {
        FileConfiguration config = YamlConfiguration. loadConfiguration(file);

        SkillEvolution evolution = new SkillEvolution();
        evolution.setEvolutionId(config.getString("id", file.getName(). replace(".yml", "")));
        evolution.setName(config.getString("name", "Unknown"));
        evolution.setDescription(config.getString("description", ""));

        // 진화 관계
        evolution.setFromSkillId(config.getString("from-skill-id", ""));
        evolution.setToSkillId(config.getString("to-skill-id", ""));

        // 타입
        String typeString = config.getString("type", "ENHANCE");
        try {
            evolution.setType(EvolutionType.valueOf(typeString));
        } catch (IllegalArgumentException e) {
            evolution.setType(EvolutionType. ENHANCE);
        }

        // 요구사항
        evolution.setRequiredSkillLevel(config.getInt("required-skill-level", 1));
        evolution.setRequiredPlayerLevel(config.getInt("required-player-level", 1));
        evolution.setRequiredUseCount(config.getInt("required-use-count", 0));
        evolution.setRequiredQuest(config.getString("required-quest", ""));

        // 비용
        evolution.setManaCost(config.getDouble("mana-cost", 0.0));
        evolution.setGoldCost(config.getDouble("gold-cost", 0.0));

        return evolution;
    }

    /**
     * 진화 데이터 조회
     */
    public SkillEvolution getEvolution(String evolutionId) {
        return evolutionCache.getOrDefault(evolutionId, null);
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        evolutionCache.clear();
    }
}