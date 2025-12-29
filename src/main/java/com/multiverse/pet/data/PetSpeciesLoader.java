package com.multiverse.pet.data;

import com.multiverse. pet.PetCore;
import com. multiverse.pet. model.PetRarity;
import com.multiverse.pet.model.PetSpecies;
import com.multiverse.pet.model.PetType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org. bukkit.entity. EntityType;

import java.io. File;
import java.util.*;

/**
 * 펫 종족 로더
 * YAML 파일에서 펫 종족 정보 로드
 */
public class PetSpeciesLoader {

    private final PetCore plugin;
    private final File speciesFolder;

    public PetSpeciesLoader(PetCore plugin) {
        this. plugin = plugin;
        this.speciesFolder = new File(plugin.getDataFolder(), "pets/species");

        if (!speciesFolder.exists()) {
            speciesFolder.mkdirs();
            saveDefaultSpeciesFiles();
        }
    }

    /**
     * 기본 종족 파일 저장
     */
    private void saveDefaultSpeciesFiles() {
        String[] defaultFiles = {"wolf. yml", "dragon.yml", "phoenix.yml", "golem.yml"};

        for (String fileName : defaultFiles) {
            File file = new File(speciesFolder, fileName);
            if (! file.exists()) {
                plugin.saveResource("pets/species/" + fileName, false);
            }
        }
    }

    /**
     * 모든 종족 로드
     */
    public Map<String, PetSpecies> loadAllSpecies() {
        Map<String, PetSpecies> species = new HashMap<>();

        File[] files = speciesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return species;

        for (File file : files) {
            try {
                Map<String, PetSpecies> loaded = loadSpeciesFromFile(file);
                species.putAll(loaded);

                if (plugin.isDebugMode()) {
                    plugin.getLogger().info("[DEBUG] 종족 파일 로드:  " + file.getName() + 
                            " (" + loaded.size() + "개)");
                }

            } catch (Exception e) {
                plugin.getLogger().warning("종족 파일 로드 실패: " + file.getName() + " - " + e.getMessage());
            }
        }

        plugin.getLogger().info("총 " + species.size() + "개의 펫 종족 로드됨");
        return species;
    }

    /**
     * 파일에서 종족 로드
     */
    private Map<String, PetSpecies> loadSpeciesFromFile(File file) {
        Map<String, PetSpecies> species = new HashMap<>();
        YamlConfiguration config = YamlConfiguration. loadConfiguration(file);

        for (String speciesId : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(speciesId);
            if (section == null) continue;

            try {
                PetSpecies sp = loadSpeciesFromSection(speciesId, section);
                species. put(speciesId, sp);
            } catch (Exception e) {
                plugin.getLogger().warning("종족 로드 실패:  " + speciesId + " - " + e.getMessage());
            }
        }

        return species;
    }

    /**
     * 섹션에서 종족 로드
     */
    private PetSpecies loadSpeciesFromSection(String speciesId, ConfigurationSection section) {
        PetSpecies species = new PetSpecies();

        species.setSpeciesId(speciesId);
        species.setName(section.getString("name", speciesId));
        species.setDescription(section.getString("description", ""));

        // 타입
        String typeStr = section.getString("type", "COMBAT");
        try {
            species.setType(PetType.valueOf(typeStr. toUpperCase()));
        } catch (IllegalArgumentException e) {
            species.setType(PetType.COMBAT);
        }

        // 엔티티 타입
        String entityTypeStr = section. getString("entity-type", "WOLF");
        try {
            species.setEntityType(EntityType.valueOf(entityTypeStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            species.setEntityType(EntityType.WOLF);
        }

        // 기본 희귀도
        String rarityStr = section. getString("base-rarity", "COMMON");
        try {
            species.setBaseRarity(PetRarity.valueOf(rarityStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            species.setBaseRarity(PetRarity.COMMON);
        }

        // 최대 레벨
        species.setMaxLevel(section.getInt("max-level", 100));

        // 기본 스탯
        ConfigurationSection baseStatsSection = section.getConfigurationSection("base-stats");
        if (baseStatsSection != null) {
            Map<String, Double> baseStats = new HashMap<>();
            for (String key : baseStatsSection.getKeys(false)) {
                baseStats.put(key, baseStatsSection.getDouble(key));
            }
            species.setBaseStats(baseStats);
        }

        // 레벨당 스탯 증가
        ConfigurationSection statsPerLevelSection = section.getConfigurationSection("stats-per-level");
        if (statsPerLevelSection != null) {
            Map<String, Double> statsPerLevel = new HashMap<>();
            for (String key : statsPerLevelSection.getKeys(false)) {
                statsPerLevel. put(key, statsPerLevelSection. getDouble(key));
            }
            species.setStatsPerLevel(statsPerLevel);
        }

        // 배울 수 있는 스킬
        List<String> learnableSkills = section.getStringList("learnable-skills");
        species.setLearnableSkills(learnableSkills);

        // 스킬 해금 레벨
        ConfigurationSection skillUnlockSection = section.getConfigurationSection("skill-unlock-levels");
        if (skillUnlockSection != null) {
            Map<String, Integer> skillUnlockLevels = new HashMap<>();
            for (String skillId : skillUnlockSection.getKeys(false)) {
                skillUnlockLevels.put(skillId, skillUnlockSection. getInt(skillId));
            }
            species.setSkillUnlockLevels(skillUnlockLevels);
        }

        // 진화 경로
        List<String> evolutionPaths = section.getStringList("evolution-paths");
        species.setEvolutionPaths(evolutionPaths);

        // 음식 선호도
        List<String> preferredFoods = section.getStringList("preferred-foods");
        species.setPreferredFoods(preferredFoods);

        // 특수 능력
        List<String> abilities = section.getStringList("abilities");
        species.setAbilities(abilities);

        // 획득 방법
        List<String> obtainMethods = section.getStringList("obtain-methods");
        species.setObtainMethods(obtainMethods);

        // 포획 설정
        ConfigurationSection captureSection = section.getConfigurationSection("capture");
        if (captureSection != null) {
            species.setCapturable(captureSection. getBoolean("enabled", false));
            species.setBaseCaptureChance(captureSection.getDouble("base-chance", 10.0));
            species.setMinCaptureLevel(captureSection. getInt("min-level", 1));
        }

        // 커스텀 모델 데이터
        species.setCustomModelData(section.getInt("custom-model-data", 0));

        // 사운드
        species.setAmbientSound(section.getString("sounds. ambient"));
        species.setHurtSound(section.getString("sounds.hurt"));
        species.setDeathSound(section. getString("sounds.death"));

        // 크기
        species. setScale(section.getDouble("scale", 1.0));

        // 이동 속도
        species.setBaseSpeed(section.getDouble("base-speed", 0.3));

        // 공격 범위
        species. setAttackRange(section.getDouble("attack-range", 2.0));

        return species;
    }

    /**
     * 종족 파일 리로드
     */
    public Map<String, PetSpecies> reload() {
        return loadAllSpecies();
    }
}