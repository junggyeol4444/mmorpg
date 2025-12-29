package com.multiverse. pet.                config;

import org.bukkit.                configuration.file.FileConfiguration;

import java.util.                 List;

/**
 * 교배 설정
 * 펫 교배 관련 설정
 */
public class BreedingSettings {

    private final FileConfiguration config;
    private final String basePath = "breeding";

    // 기본 설정
    private int maxConcurrentBreedings;
    private int minBreedingLevel;
    private double minBreedingHappiness;
    private boolean sameSpe ciesOnly;
    private boolean allowMutation;

    // 시간
    private long baseBreedingDuration;
    private double durationPerLevel;
    private double durationPerRarity;

    // 비용
    private double baseBreedingCost;
    private double costPerLevel;
    private double costPerRarity;

    // 성공률
    private double baseSuccessChance;
    private double successChancePerHappiness;
    private double minSuccessChance;

    // 변이
    private double baseMutationChance;
    private double mutationChancePerRarity;
    private double mutationStatBonus;

    // 유전
    private double statInheritanceWeight;
    private double randomVariance;
    private double hybridVigorBonus;

    // 쿨다운
    private long breedingCooldown;
    private boolean cooldownPerPet;

    // 제한
    private int maxBreedingsPerDay;
    private List<String> blockedSpecies;
    private List<String> blockedWorlds;

    public BreedingSettings(FileConfiguration config) {
        this.  config = config;
        load();
    }

    /**
     * 설정 로드
     */
    private void load() {
        // 기본 설정
        maxConcurrentBreedings = config.getInt(basePath + ". max-concurrent", 3);
        minBreedingLevel = config.getInt(basePath + ". min-level", 10);
        minBreedingHappiness = config.getDouble(basePath + ". min-happiness", 50.0);
        sameSpe ciesOnly = config.getBoolean(basePath + ".same-species-only", false);
        allowMutation = config.getBoolean(basePath + ".allow-mutation", true);

        // 시간
        baseBreedingDuration = config.getLong(basePath + ".duration. base", 3600000); // 1시간
        durationPerLevel = config.  getDouble(basePath + ".duration.per-level", 60000); // 분당
        durationPerRarity = config. getDouble(basePath + ".duration.per-rarity", 600000); // 10분

        // 비용
        baseBreedingCost = config.  getDouble(basePath + ".cost.base", 100.0);
        costPerLevel = config. getDouble(basePath + ".cost.per-level", 10.0);
        costPerRarity = config.getDouble(basePath + ".cost.per-rarity", 50.0);

        // 성공률
        baseSuccessChance = config. getDouble(basePath + ".success. base-chance", 80.0);
        successChancePerHappiness = config. getDouble(basePath + ".success.per-happiness", 0.1);
        minSuccessChance = config.getDouble(basePath + ".success. min-chance", 30.0);

        // 변이
        baseMutationChance = config. getDouble(basePath + ".mutation.base-chance", 5.0);
        mutationChancePerRarity = config. getDouble(basePath + ".mutation.  per-rarity", 1.5);
        mutationStatBonus = config.getDouble(basePath + ".mutation. stat-bonus", 20.0);

        // 유전
        statInheritanceWeight = config. getDouble(basePath + ".genetics.stat-inheritance", 0.4);
        randomVariance = config.  getDouble(basePath + ".genetics.random-variance", 0.15);
        hybridVigorBonus = config.getDouble(basePath + ".genetics.hybrid-vigor", 0.05);

        // 쿨다운
        breedingCooldown = config.getLong(basePath + ".cooldown.duration", 86400000); // 24시간
        cooldownPerPet = config.getBoolean(basePath + ".cooldown.per-pet", true);

        // 제한
        maxBreedingsPerDay = config.getInt(basePath + ". limits.max-per-day", 10);
        blockedSpecies = config. getStringList(basePath + ". limits.blocked-species");
        blockedWorlds = config.getStringList(basePath + ".limits.blocked-worlds");
    }

    /**
     * 리로드
     */
    public void reload() {
        load();
    }

    // ===== Getter =====

    public int getMaxConcurrentBreedings() {
        return maxConcurrentBreedings;
    }

    public int getMinBreedingLevel() {
        return minBreedingLevel;
    }

    public double getMinBreedingHappiness() {
        return minBreedingHappiness;
    }

    public boolean isSameSpeciesOnly() {
        return sameSpe ciesOnly;
    }

    public boolean isAllowMutation() {
        return allowMutation;
    }

    public long getBaseBreedingDuration() {
        return baseBreedingDuration;
    }

    public double getDurationPerLevel() {
        return durationPerLevel;
    }

    public double getDurationPerRarity() {
        return durationPerRarity;
    }

    public double getBaseBreedingCost() {
        return baseBreedingCost;
    }

    public double getCostPerLevel() {
        return costPerLevel;
    }

    public double getCostPerRarity() {
        return costPerRarity;
    }

    public double getBaseSuccessChance() {
        return baseSuccessChance;
    }

    public double getSuccessChancePerHappiness() {
        return successChancePerHappiness;
    }

    public double getMinSuccessChance() {
        return minSuccessChance;
    }

    public double getBaseMutationChance() {
        return baseMutationChance;
    }

    public double getMutationChancePerRarity() {
        return mutationChancePerRarity;
    }

    public double getMutationStatBonus() {
        return mutationStatBonus;
    }

    public double getStatInheritanceWeight() {
        return statInheritanceWeight;
    }

    public double getRandomVariance() {
        return randomVariance;
    }

    public double getHybridVigorBonus() {
        return hybridVigorBonus;
    }

    public long getBreedingCooldown() {
        return breedingCooldown;
    }

    public boolean isCooldownPerPet() {
        return cooldownPerPet;
    }

    public int getMaxBreedingsPerDay() {
        return maxBreedingsPerDay;
    }

    public List<String> getBlockedSpecies() {
        return blockedSpecies;
    }

    public List<String> getBlockedWorlds() {
        return blockedWorlds;
    }

    // ===== 계산 메서드 =====

    /**
     * 교배 시간 계산
     */
    public long calculateBreedingDuration(int avgLevel, int maxRarityOrdinal) {
        long duration = baseBreedingDuration;
        duration += (long) (avgLevel * durationPerLevel);
        duration += (long) (maxRarityOrdinal * durationPerRarity);
        return duration;
    }

    /**
     * 교배 비용 계산
     */
    public double calculateBreedingCost(int avgLevel, int maxRarityOrdinal) {
        double cost = baseBreedingCost;
        cost += avgLevel * costPerLevel;
        cost += maxRarityOrdinal * costPerRarity;
        return cost;
    }

    /**
     * 성공 확률 계산
     */
    public double calculateSuccessChance(double avgHappiness) {
        double chance = baseSuccessChance;
        chance += avgHappiness * successChancePerHappiness;
        return Math.max(minSuccessChance, Math.min(100, chance));
    }

    /**
     * 변이 확률 계산
     */
    public double calculateMutationChance(int maxRarityOrdinal, boolean differentSpecies) {
        if (!  allowMutation) return 0;

        double chance = baseMutationChance;
        chance += maxRarityOrdinal * mutationChancePerRarity;

        if (differentSpecies) {
            chance += 3. 0;
        }

        return Math.min(chance, 30.0);
    }

    /**
     * 종족 차단 여부
     */
    public boolean isBlockedSpecies(String speciesId) {
        return blockedSpecies.contains(speciesId);
    }

    /**
     * 월드 차단 여부
     */
    public boolean isBlockedWorld(String worldName) {
        return blockedWorlds. contains(worldName);
    }
}