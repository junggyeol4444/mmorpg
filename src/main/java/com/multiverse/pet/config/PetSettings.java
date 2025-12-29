package com.multiverse.pet.           config;

import org.bukkit.           configuration.file.FileConfiguration;
import org.bukkit.           entity.  EntityType;

import java.util.            HashMap;
import java.util.            List;
import java.  util.            Map;

/**
 * 펫 기본 설정
 * 펫의 기본 동작 및 속성 설정
 */
public class PetSettings {

    private final FileConfiguration config;
    private final String basePath = "pet";

    // 기본 설정
    private int maxActivePets;
    private int defaultStorageCapacity;
    private int maxStorageCapacity;
    private boolean autoSummonOnJoin;
    private boolean teleportOnWorldChange;
    private double teleportDistance;
    private double followDistance;
    private double attackRange;

    // 이름 설정
    private int minNameLength;
    private int maxNameLength;
    private boolean allowColorCodes;
    private List<String> bannedWords;

    // 소환 설정
    private int summonCooldown;
    private boolean summonEffects;
    private boolean summonSound;

    // 사망 설정
    private boolean permanentDeath;
    private double deathExpLoss;
    private int reviveCooldown;

    // 허용 엔티티
    private List<String> allowedEntityTypes;
    private Map<String, Double> entityScales;

    public PetSettings(FileConfiguration config) {
        this.config = config;
        load();
    }

    /**
     * 설정 로드
     */
    private void load() {
        // 기본 설정
        maxActivePets = config.getInt(basePath + ".max-active-pets", 1);
        defaultStorageCapacity = config.getInt(basePath + ".default-storage-capacity", 20);
        maxStorageCapacity = config.getInt(basePath + ".max-storage-capacity", 100);
        autoSummonOnJoin = config.getBoolean(basePath + ".auto-summon-on-join", true);
        teleportOnWorldChange = config.getBoolean(basePath + ".teleport-on-world-change", true);
        teleportDistance = config.getDouble(basePath + ". teleport-distance", 30.0);
        followDistance = config. getDouble(basePath + ".follow-distance", 3.0);
        attackRange = config. getDouble(basePath + ".attack-range", 2.0);

        // 이름 설정
        minNameLength = config.getInt(basePath + ". name. min-length", 2);
        maxNameLength = config.getInt(basePath + ".name.max-length", 20);
        allowColorCodes = config.getBoolean(basePath + ".name.allow-color-codes", true);
        bannedWords = config. getStringList(basePath + ".name.banned-words");

        // 소환 설정
        summonCooldown = config.getInt(basePath + ".summon.cooldown", 5);
        summonEffects = config. getBoolean(basePath + ".summon.effects", true);
        summonSound = config.getBoolean(basePath + ".summon.sound", true);

        // 사망 설정
        permanentDeath = config.getBoolean(basePath + ".death.permanent", false);
        deathExpLoss = config. getDouble(basePath + ".death.exp-loss-percent", 10.0);
        reviveCooldown = config.getInt(basePath + ". death.revive-cooldown", 60);

        // 허용 엔티티
        allowedEntityTypes = config. getStringList(basePath + ".allowed-entity-types");

        // 엔티티 크기
        entityScales = new HashMap<>();
        if (config.isConfigurationSection(basePath + ".entity-scales")) {
            for (String key : config.getConfigurationSection(basePath + ".entity-scales").getKeys(false)) {
                entityScales.put(key, config. getDouble(basePath + ".entity-scales." + key, 1.0));
            }
        }
    }

    /**
     * 리로드
     */
    public void reload() {
        load();
    }

    // ===== Getter =====

    public int getMaxActivePets() {
        return maxActivePets;
    }

    public int getDefaultStorageCapacity() {
        return defaultStorageCapacity;
    }

    public int getMaxStorageCapacity() {
        return maxStorageCapacity;
    }

    public boolean isAutoSummonOnJoin() {
        return autoSummonOnJoin;
    }

    public boolean isTeleportOnWorldChange() {
        return teleportOnWorldChange;
    }

    public double getTeleportDistance() {
        return teleportDistance;
    }

    public double getFollowDistance() {
        return followDistance;
    }

    public double getAttackRange() {
        return attackRange;
    }

    public int getMinNameLength() {
        return minNameLength;
    }

    public int getMaxNameLength() {
        return maxNameLength;
    }

    public boolean isAllowColorCodes() {
        return allowColorCodes;
    }

    public List<String> getBannedWords() {
        return bannedWords;
    }

    public int getSummonCooldown() {
        return summonCooldown;
    }

    public boolean isSummonEffects() {
        return summonEffects;
    }

    public boolean isSummonSound() {
        return summonSound;
    }

    public boolean isPermanentDeath() {
        return permanentDeath;
    }

    public double getDeathExpLoss() {
        return deathExpLoss;
    }

    public int getReviveCooldown() {
        return reviveCooldown;
    }

    public List<String> getAllowedEntityTypes() {
        return allowedEntityTypes;
    }

    public double getEntityScale(EntityType entityType) {
        return entityScales.getOrDefault(entityType.name(), 1.0);
    }

    public double getEntityScale(String entityType) {
        return entityScales.getOrDefault(entityType. toUpperCase(), 1.0);
    }

    // ===== 유효성 검사 =====

    /**
     * 이름 유효성 검사
     */
    public boolean isValidName(String name) {
        if (name == null) return false;

        // 길이 체크
        int length = name.replaceAll("§.", "").length();
        if (length < minNameLength || length > maxNameLength) {
            return false;
        }

        // 금지어 체크
        String lowerName = name. toLowerCase();
        for (String banned : bannedWords) {
            if (lowerName.contains(banned.toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    /**
     * 엔티티 타입 허용 여부
     */
    public boolean isAllowedEntityType(EntityType entityType) {
        if (allowedEntityTypes. isEmpty()) {
            return true; // 비어있으면 모두 허용
        }
        return allowedEntityTypes.contains(entityType.name());
    }

    /**
     * 엔티티 타입 허용 여부 (문자열)
     */
    public boolean isAllowedEntityType(String entityType) {
        if (allowedEntityTypes.isEmpty()) {
            return true;
        }
        return allowedEntityTypes.contains(entityType.toUpperCase());
    }
}