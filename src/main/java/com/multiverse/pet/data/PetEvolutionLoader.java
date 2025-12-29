package com.multiverse. pet.data;

import com.multiverse.pet.PetCore;
import com.multiverse.pet.model.evolution. EvolutionType;
import com. multiverse.pet. model.evolution.PetEvolution;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java. io.File;
import java.util.*;

/**
 * 펫 진화 로더
 * YAML 파일에서 진화 정보 로드
 */
public class PetEvolutionLoader {

    private final PetCore plugin;
    private final File evolutionsFile;

    public PetEvolutionLoader(PetCore plugin) {
        this. plugin = plugin;
        this.evolutionsFile = new File(plugin.getDataFolder(), "pets/evolutions/evolutions.yml");

        File parent = evolutionsFile. getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (!evolutionsFile.exists()) {
            try {
                plugin.saveResource("pets/evolutions/evolutions.yml", false);
            } catch (Exception e) {
                plugin.getLogger().warning("기본 진화 파일 생성 실패");
            }
        }
    }

    /**
     * 모든 진화 로드
     */
    public Map<String, PetEvolution> loadAllEvolutions() {
        Map<String, PetEvolution> evolutions = new HashMap<>();

        if (!evolutionsFile. exists()) {
            return evolutions;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(evolutionsFile);

            for (String evolutionId :  config.getKeys(false)) {
                ConfigurationSection section = config.getConfigurationSection(evolutionId);
                if (section == null) continue;

                try {
                    PetEvolution evolution = loadEvolutionFromSection(evolutionId, section);
                    evolutions.put(evolutionId, evolution);
                } catch (Exception e) {
                    plugin.getLogger().warning("진화 로드 실패:  " + evolutionId + " - " + e.getMessage());
                }
            }

            plugin.getLogger().info("총 " + evolutions. size() + "개의 펫 진화 로드됨");

        } catch (Exception e) {
            plugin.getLogger().warning("진화 파일 로드 실패: " + e.getMessage());
        }

        return evolutions;
    }

    /**
     * 섹션에서 진화 로드
     */
    private PetEvolution loadEvolutionFromSection(String evolutionId, ConfigurationSection section) {
        PetEvolution evolution = new PetEvolution();

        evolution.setEvolutionId(evolutionId);
        evolution. setFromSpeciesId(section.getString("from-species"));
        evolution.setToSpeciesId(section.getString("to-species"));

        // 진화 단계
        evolution.setFromStage(section.getInt("from-stage", 1));
        evolution.setToStage(section. getInt("to-stage", 2));

        // 진화 타입
        String typeStr = section.getString("type", "NORMAL");
        try {
            evolution.setEvolutionType(EvolutionType. valueOf(typeStr. toUpperCase()));
        } catch (IllegalArgumentException e) {
            evolution.setEvolutionType(EvolutionType.NORMAL);
        }

        // 요구 조건
        evolution. setRequiredLevel(section.getInt("required-level", 1));
        evolution.setRequiredHappiness(section.getInt("required-happiness", 0));
        evolution.setRequiredBattleWins(section.getInt("required-battle-wins", 0));

        // 시간 조건
        evolution. setRequiresDaytime(section.getBoolean("requires-daytime", false));
        evolution.setRequiresNighttime(section.getBoolean("requires-nighttime", false));

        // 바이옴 조건
        evolution.setRequiredBiome(section.getString("required-biome"));

        // 날씨 조건
        evolution.setRequiresRain(section.getBoolean("requires-rain", false));
        evolution.setRequiresThunder(section.getBoolean("requires-thunder", false));

        // 필요 아이템
        ConfigurationSection itemsSection = section.getConfigurationSection("required-items");
        if (itemsSection != null) {
            List<PetEvolution.ItemRequirement> items = new ArrayList<>();
            for (String itemKey : itemsSection. getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemKey);
                if (itemSection != null) {
                    PetEvolution. ItemRequirement item = new PetEvolution. ItemRequirement();
                    item.setItemId(itemSection.getString("id", itemKey));
                    item.setAmount(itemSection.getInt("amount", 1));
                    item.setConsumed(itemSection.getBoolean("consumed", true));
                    items.add(item);
                }
            }
            evolution. setRequiredItems(items);
        }

        // 골드 비용
        evolution. setGoldCost(section.getDouble("gold-cost", 0));

        // 성공 확률
        evolution. setSuccessChance(section.getDouble("success-chance", 100));

        // 스탯 보너스
        ConfigurationSection statBonusSection = section.getConfigurationSection("stat-bonuses");
        if (statBonusSection != null) {
            Map<String, Double> statBonuses = new HashMap<>();
            for (String stat : statBonusSection.getKeys(false)) {
                statBonuses. put(stat, statBonusSection. getDouble(stat));
            }
            evolution.setStatBonuses(statBonuses);
        }

        // 스탯 배율
        ConfigurationSection statMultiplierSection = section.getConfigurationSection("stat-multipliers");
        if (statMultiplierSection != null) {
            Map<String, Double> statMultipliers = new HashMap<>();
            for (String stat : statMultiplierSection.getKeys(false)) {
                statMultipliers.put(stat, statMultiplierSection.getDouble(stat));
            }
            evolution.setStatMultipliers(statMultipliers);
        }

        // 새 스킬
        List<String> newSkills = section. getStringList("new-skills");
        evolution.setNewSkills(newSkills);

        // 제거 스킬
        List<String> removedSkills = section.getStringList("removed-skills");
        evolution.setRemovedSkills(removedSkills);

        // 새 능력
        List<String> newAbilities = section.getStringList("new-abilities");
        evolution. setNewAbilities(newAbilities);

        // 외형 변경
        evolution.setNewEntityType(section.getString("new-entity-type"));
        evolution.setNewScale(section.getDouble("new-scale", 1.0));
        evolution.setNewCustomModelData(section.getInt("new-custom-model-data", 0));

        // 이펙트
        evolution.setEvolutionParticle(section.getString("particle"));
        evolution. setEvolutionSound(section.getString("sound"));

        // 실패 시 페널티
        evolution.setFailPenaltyExp(section.getDouble("fail-penalty-exp", 0));
        evolution.setFailPenaltyHappiness(section. getDouble("fail-penalty-happiness", 0));

        // 설명
        evolution.setDescription(section.getString("description", ""));

        return evolution;
    }

    /**
     * 진화 파일 리로드
     */
    public Map<String, PetEvolution> reload() {
        return loadAllEvolutions();
    }
}