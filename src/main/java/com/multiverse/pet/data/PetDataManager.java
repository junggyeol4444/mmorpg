package com. multiverse.pet.data;

import com. multiverse.pet.PetCore;
import com.multiverse. pet.model.Pet;
import com. multiverse.pet. model.PetRarity;
import com. multiverse.pet. model.PetStatus;
import com. multiverse.pet. model.PetType;
import com.multiverse. pet.model.skill.PetSkill;
import com.multiverse.pet.model. equipment.PetEquipSlot;
import com.multiverse.pet.model.equipment. PetEquipmentData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit. configuration.file.YamlConfiguration;
import org. bukkit.entity. EntityType;

import java.io. File;
import java.io.IOException;
import java. util.*;
import java. util.concurrent.ConcurrentHashMap;

/**
 * 펫 데이터 매니저
 * 펫 데이터의 저장 및 로드 담당
 */
public class PetDataManager {

    private final PetCore plugin;
    private final File dataFolder;

    // 플레이어 데이터 캐시
    private final Map<UUID, PlayerPetData> playerDataCache;

    // 저장 대기 큐
    private final Set<UUID> pendingSaves;

    public PetDataManager(PetCore plugin) {
        this. plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        this.playerDataCache = new ConcurrentHashMap<>();
        this.pendingSaves = ConcurrentHashMap. newKeySet();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    // ===== 플레이어 데이터 로드 =====

    /**
     * 플레이어 데이터 로드
     */
    public void loadPlayerData(UUID playerId) {
        File playerFile = getPlayerFile(playerId);

        if (!playerFile.exists()) {
            // 새 플레이어
            playerDataCache. put(playerId, new PlayerPetData(playerId));
            return;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            PlayerPetData data = loadFromConfig(playerId, config);
            playerDataCache. put(playerId, data);

            if (plugin.isDebugMode()) {
                plugin.getLogger().info("[DEBUG] 플레이어 데이터 로드: " + playerId + 
                        " (펫 " + data.getPets().size() + "마리)");
            }

        } catch (Exception e) {
            plugin.getLogger().warning("플레이어 데이터 로드 실패: " + playerId + " - " + e. getMessage());
            playerDataCache.put(playerId, new PlayerPetData(playerId));
        }
    }

    /**
     * 설정에서 플레이어 데이터 로드
     */
    private PlayerPetData loadFromConfig(UUID playerId, YamlConfiguration config) {
        PlayerPetData data = new PlayerPetData(playerId);

        // 펫 목록 로드
        ConfigurationSection petsSection = config. getConfigurationSection("pets");
        if (petsSection != null) {
            for (String petIdStr : petsSection.getKeys(false)) {
                try {
                    UUID petId = UUID. fromString(petIdStr);
                    ConfigurationSection petSection = petsSection.getConfigurationSection(petIdStr);
                    if (petSection != null) {
                        Pet pet = loadPetFromSection(petId, playerId, petSection);
                        data. addPet(pet);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("펫 로드 실패:  " + petIdStr + " - " + e.getMessage());
                }
            }
        }

        // 설정 로드
        ConfigurationSection settingsSection = config. getConfigurationSection("settings");
        if (settingsSection != null) {
            data.setAutoSummonEnabled(settingsSection. getBoolean("auto-summon", true));
            data.setSortType(settingsSection. getString("sort-type", "LEVEL"));
            data.setLastActivePetId(settingsSection.getString("last-active-pet"));
        }

        // 통계 로드
        ConfigurationSection statsSection = config.getConfigurationSection("stats");
        if (statsSection != null) {
            data. setTotalPetsOwned(statsSection. getInt("total-pets-owned", 0));
            data.setBattleWins(statsSection. getInt("battle-wins", 0));
            data.setBattleLosses(statsSection.getInt("battle-losses", 0));
            data.setRating(statsSection.getInt("rating", 1000));
        }

        return data;
    }

    /**
     * 섹션에서 펫 로드
     */
    private Pet loadPetFromSection(UUID petId, UUID ownerId, ConfigurationSection section) {
        Pet pet = new Pet();

        pet.setPetId(petId);
        pet.setOwnerId(ownerId);
        pet.setSpeciesId(section.getString("species-id", "unknown"));
        pet.setPetName(section.getString("name", "펫"));

        // 타입, 희귀도
        String typeStr = section.getString("type");
        if (typeStr != null) {
            try {
                pet.setType(PetType.valueOf(typeStr));
            } catch (IllegalArgumentException ignored) {}
        }

        String rarityStr = section. getString("rarity", "COMMON");
        try {
            pet. setRarity(PetRarity.valueOf(rarityStr));
        } catch (IllegalArgumentException e) {
            pet.setRarity(PetRarity. COMMON);
        }

        // 레벨, 경험치
        pet.setLevel(section.getInt("level", 1));
        pet.setExperience(section. getLong("experience", 0));
        pet.setSkillPoints(section.getInt("skill-points", 0));

        // 상태
        String statusStr = section. getString("status", "STORED");
        try {
            pet. setStatus(PetStatus.valueOf(statusStr));
        } catch (IllegalArgumentException e) {
            pet.setStatus(PetStatus. STORED);
        }

        // 체력, 배고픔, 행복도
        pet. setHealth(section. getDouble("health", 100));
        pet.setMaxHealth(section.getDouble("max-health", 100));
        pet.setHunger(section. getDouble("hunger", 100));
        pet.setHappiness(section.getDouble("happiness", 100));

        // 진화
        pet.setEvolutionStage(section.getInt("evolution-stage", 1));

        // 엔티티 타입
        String entityTypeStr = section.getString("entity-type");
        if (entityTypeStr != null) {
            try {
                pet.setEntityType(EntityType.valueOf(entityTypeStr));
            } catch (IllegalArgumentException ignored) {}
        }

        // 기본 스탯
        ConfigurationSection baseStatsSection = section. getConfigurationSection("base-stats");
        if (baseStatsSection != null) {
            Map<String, Double> baseStats = new HashMap<>();
            for (String key : baseStatsSection.getKeys(false)) {
                baseStats. put(key, baseStatsSection.getDouble(key));
            }
            pet.setBaseStats(baseStats);
        }

        // 보너스 스탯
        ConfigurationSection bonusStatsSection = section. getConfigurationSection("bonus-stats");
        if (bonusStatsSection != null) {
            Map<String, Double> bonusStats = new HashMap<>();
            for (String key : bonusStatsSection. getKeys(false)) {
                bonusStats.put(key, bonusStatsSection.getDouble(key));
            }
            pet.setBonusStats(bonusStats);
        }

        // 스킬
        ConfigurationSection skillsSection = section.getConfigurationSection("skills");
        if (skillsSection != null) {
            List<PetSkill> skills = new ArrayList<>();
            for (String skillId : skillsSection. getKeys(false)) {
                ConfigurationSection skillSection = skillsSection.getConfigurationSection(skillId);
                if (skillSection != null) {
                    PetSkill skill = loadSkillFromSection(skillId, skillSection);
                    skills.add(skill);
                }
            }
            pet.setSkills(skills);
        }

        // 장비
        ConfigurationSection equipmentSection = section.getConfigurationSection("equipment");
        if (equipmentSection != null) {
            Map<PetEquipSlot, PetEquipmentData> equipment = new EnumMap<>(PetEquipSlot.class);
            for (String slotStr : equipmentSection.getKeys(false)) {
                try {
                    PetEquipSlot slot = PetEquipSlot.valueOf(slotStr);
                    ConfigurationSection equipSection = equipmentSection. getConfigurationSection(slotStr);
                    if (equipSection != null) {
                        PetEquipmentData equipData = loadEquipmentFromSection(equipSection);
                        equipment.put(slot, equipData);
                    }
                } catch (IllegalArgumentException ignored) {}
            }
            pet.setEquipment(equipment);
        }

        // 전투 통계
        pet.setBattleWins(section.getInt("battle-wins", 0));
        pet.setBattleLosses(section.getInt("battle-losses", 0));
        pet.setKillCount(section.getInt("kill-count", 0));

        // 플래그
        pet.setFavorite(section. getBoolean("favorite", false));
        pet.setNameLocked(section.getBoolean("name-locked", false));
        pet.setMutation(section.getBoolean("mutation", false));
        pet.setLastActive(section.getBoolean("last-active", false));

        // 생성 시간
        pet.setCreatedAt(section. getLong("created-at", System.currentTimeMillis()));
        pet.setLastInteraction(section.getLong("last-interaction", System.currentTimeMillis()));

        return pet;
    }

    /**
     * 섹션에서 스킬 로드
     */
    private PetSkill loadSkillFromSection(String skillId, ConfigurationSection section) {
        PetSkill skill = new PetSkill();
        skill.setSkillId(skillId);
        skill.setCurrentLevel(section.getInt("level", 1));
        skill.setLastUsed(section.getLong("last-used", 0));
        return skill;
    }

    /**
     * 섹션에서 장비 로드
     */
    private PetEquipmentData loadEquipmentFromSection(ConfigurationSection section) {
        PetEquipmentData equipment = new PetEquipmentData();
        equipment. setItemId(section.getString("item-id"));
        equipment.setDurability(section.getInt("durability", 100));

        String rarityStr = section. getString("rarity", "COMMON");
        try {
            equipment.setRarity(PetRarity.valueOf(rarityStr));
        } catch (IllegalArgumentException e) {
            equipment.setRarity(PetRarity.COMMON);
        }

        ConfigurationSection statsSection = section.getConfigurationSection("stats");
        if (statsSection != null) {
            Map<String, Double> stats = new HashMap<>();
            for (String key : statsSection. getKeys(false)) {
                stats. put(key, statsSection.getDouble(key));
            }
            equipment.setStatBonuses(stats);
        }

        return equipment;
    }

    // ===== 플레이어 데이터 저장 =====

    /**
     * 플레이어 데이터 저장
     */
    public void savePlayerData(UUID playerId) {
        PlayerPetData data = playerDataCache.get(playerId);
        if (data == null) return;

        File playerFile = getPlayerFile(playerId);

        try {
            YamlConfiguration config = new YamlConfiguration();
            saveToConfig(data, config);
            config.save(playerFile);

            pendingSaves.remove(playerId);

            if (plugin. isDebugMode()) {
                plugin. getLogger().info("[DEBUG] 플레이어 데이터 저장:  " + playerId);
            }

        } catch (IOException e) {
            plugin.getLogger().warning("플레이어 데이터 저장 실패: " + playerId + " - " + e.getMessage());
        }
    }

    /**
     * 설정에 플레이어 데이터 저장
     */
    private void saveToConfig(PlayerPetData data, YamlConfiguration config) {
        // 펫 저장
        for (Pet pet : data.getPets()) {
            String path = "pets." + pet.getPetId().toString();
            savePetToSection(pet, config, path);
        }

        // 설정 저장
        config.set("settings. auto-summon", data.isAutoSummonEnabled());
        config.set("settings. sort-type", data.getSortType());
        if (data.getLastActivePetId() != null) {
            config.set("settings.last-active-pet", data. getLastActivePetId());
        }

        // 통계 저장
        config.set("stats.total-pets-owned", data. getTotalPetsOwned());
        config.set("stats. battle-wins", data.getBattleWins());
        config.set("stats. battle-losses", data.getBattleLosses());
        config.set("stats.rating", data. getRating());
    }

    /**
     * 펫을 섹션에 저장
     */
    private void savePetToSection(Pet pet, YamlConfiguration config, String path) {
        config.set(path + ".species-id", pet. getSpeciesId());
        config.set(path + ".name", pet.getPetName());

        if (pet.getType() != null) {
            config.set(path + ".type", pet.getType().name());
        }
        config.set(path + ".rarity", pet.getRarity().name());

        config.set(path + ".level", pet. getLevel());
        config.set(path + ".experience", pet.getExperience());
        config.set(path + ". skill-points", pet.getSkillPoints());

        config.set(path + ". status", pet.getStatus().name());

        config.set(path + ".health", pet. getHealth());
        config.set(path + ".max-health", pet.getMaxHealth());
        config.set(path + ".hunger", pet. getHunger());
        config.set(path + ".happiness", pet.getHappiness());

        config.set(path + ".evolution-stage", pet.getEvolutionStage());

        if (pet.getEntityType() != null) {
            config. set(path + ". entity-type", pet.getEntityType().name());
        }

        // 스탯
        for (Map.Entry<String, Double> stat : pet.getBaseStats().entrySet()) {
            config.set(path + ".base-stats." + stat.getKey(), stat.getValue());
        }
        for (Map.Entry<String, Double> stat : pet.getBonusStats().entrySet()) {
            config.set(path + ".bonus-stats." + stat.getKey(), stat.getValue());
        }

        // 스킬
        for (PetSkill skill : pet.getSkills()) {
            String skillPath = path + ".skills." + skill.getSkillId();
            config.set(skillPath + ".level", skill. getCurrentLevel());
            config.set(skillPath + ".last-used", skill.getLastUsed());
        }

        // 장비
        for (Map.Entry<PetEquipSlot, PetEquipmentData> entry : pet.getEquipment().entrySet()) {
            PetEquipmentData equip = entry.getValue();
            if (equip != null && equip.getItemId() != null) {
                String equipPath = path + ".equipment." + entry.getKey().name();
                config. set(equipPath + ".item-id", equip.getItemId());
                config.set(equipPath + ".durability", equip.getDurability());
                config.set(equipPath + ".rarity", equip.getRarity().name());

                for (Map.Entry<String, Double> stat : equip.getStatBonuses().entrySet()) {
                    config. set(equipPath + ".stats." + stat.getKey(), stat.getValue());
                }
            }
        }

        // 전투 통계
        config.set(path + ".battle-wins", pet.getBattleWins());
        config.set(path + ".battle-losses", pet. getBattleLosses());
        config.set(path + ".kill-count", pet.getKillCount());

        // 플래그
        config. set(path + ". favorite", pet.isFavorite());
        config.set(path + ".name-locked", pet.isNameLocked());
        config.set(path + ".mutation", pet.isMutation());
        config.set(path + ". last-active", pet.wasLastActive());

        // 시간
        config. set(path + ". created-at", pet.getCreatedAt());
        config.set(path + ". last-interaction", pet.getLastInteraction());
    }

    // ===== 유틸리티 =====

    /**
     * 플레이어 파일 경로
     */
    private File getPlayerFile(UUID playerId) {
        return new File(dataFolder, playerId. toString() + ". yml");
    }

    /**
     * 캐시된 플레이어 데이터 가져오기
     */
    public PlayerPetData getPlayerData(UUID playerId) {
        return playerDataCache.get(playerId);
    }

    /**
     * 저장 대기열에 추가
     */
    public void markForSave(UUID playerId) {
        pendingSaves.add(playerId);
    }

    /**
     * 대기 중인 모든 저장 실행
     */
    public void savePendingData() {
        for (UUID playerId :  new HashSet<>(pendingSaves)) {
            savePlayerData(playerId);
        }
    }

    /**
     * 모든 데이터 저장
     */
    public void saveAllData() {
        for (UUID playerId :  playerDataCache.keySet()) {
            savePlayerData(playerId);
        }
    }

    /**
     * 플레이어 캐시 제거
     */
    public void removeFromCache(UUID playerId) {
        playerDataCache.remove(playerId);
    }

    /**
     * 종료 처리
     */
    public void shutdown() {
        saveAllData();
        playerDataCache.clear();
    }
}