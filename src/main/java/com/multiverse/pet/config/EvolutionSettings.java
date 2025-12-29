package com.multiverse.pet.              config;

import org.bukkit.              configuration.file.FileConfiguration;

import java.util.               List;

/**
 * 진화 설정
 * 펫 진화 관련 설정
 */
public class EvolutionSettings {

    private final FileConfiguration config;
    private final String basePath = "evolution";

    // 기본 설정
    private int maxEvolutionStage;
    private boolean requireItems;
    private boolean consumeItems;
    private boolean allowDowngrade;

    // 성공률
    private double baseSuccessChance;
    private double successChancePerLevel;
    private double successChancePerHappiness;
    private double minSuccessChance;
    private double maxSuccessChance;

    // 실패 페널티
    private boolean failPenaltyEnabled;
    private double failExpLossPercent;
    private double failHappinessLoss;
    private boolean failConsumeItems;

    // 보너스
    private double statBonusPerStage;
    private int skillSlotsPerStage;
    private double rarityUpgradeChance;

    // 효과
    private boolean evolutionEffects;
    private boolean evolutionSound;
    private boolean evolutionBroadcast;
    private int broadcastMinStage;

    // 제한
    private int evolutionCooldown;
    private List<String> blockedWorlds;

    public EvolutionSettings(FileConfiguration config) {
        this.config = config;
        load();
    }

    /**
     * 설정 로드
     */
    private void load() {
        // 기본 설정
        maxEvolutionStage = config.getInt(basePath + ".max-stage", 5);
        requireItems = config.getBoolean(basePath + ".require-items", true);
        consumeItems = config. getBoolean(basePath + ".consume-items", true);
        allowDowngrade = config.getBoolean(basePath + ". allow-downgrade", false);

        // 성공률
        baseSuccessChance = config.getDouble(basePath + ".success-chance. base", 70.0);
        successChancePerLevel = config.getDouble(basePath + ". success-chance.per-level", 0.5);
        successChancePerHappiness = config. getDouble(basePath + ".success-chance.  per-happiness", 0.1);
        minSuccessChance = config.getDouble(basePath + ".success-chance.  min", 10.0);
        maxSuccessChance = config.getDouble(basePath + ". success-chance. max", 95.0);

        // 실패 페널티
        failPenaltyEnabled = config.getBoolean(basePath + ".  fail-penalty.enabled", true);
        failExpLossPercent = config.getDouble(basePath + ". fail-penalty.exp-loss-percent", 10.0);
        failHappinessLoss = config.getDouble(basePath + ".fail-penalty. happiness-loss", 20.0);
        failConsumeItems = config.getBoolean(basePath + ".fail-penalty. consume-items", true);

        // 보너스
        statBonusPerStage = config. getDouble(basePath + ". bonuses.stat-bonus-per-stage", 10.0);
        skillSlotsPerStage = config. getInt(basePath + ". bonuses.skill-slots-per-stage", 1);
        rarityUpgradeChance = config.getDouble(basePath + ". bonuses.rarity-upgrade-chance", 10.0);

        // 효과
        evolutionEffects = config.  getBoolean(basePath + ". effects.particles", true);
        evolutionSound = config.getBoolean(basePath + ".effects.sound", true);
        evolutionBroadcast = config.getBoolean(basePath + ".effects.broadcast", true);
        broadcastMinStage = config.getInt(basePath + ". effects.broadcast-min-stage", 3);

        // 제한
        evolutionCooldown = config.getInt(basePath + ". cooldown", 3600);
        blockedWorlds = config.getStringList(basePath + ". blocked-worlds");
    }

    /**
     * 리로드
     */
    public void reload() {
        load();
    }

    // ===== Getter =====

    public int getMaxEvolutionStage() {
        return maxEvolutionStage;
    }

    public boolean isRequireItems() {
        return requireItems;
    }

    public boolean isConsumeItems() {
        return consumeItems;
    }

    public boolean isAllowDowngrade() {
        return allowDowngrade;
    }

    public double getBaseSuccessChance() {
        return baseSuccessChance;
    }

    public double getSuccessChancePerLevel() {
        return successChancePerLevel;
    }

    public double getSuccessChancePerHappiness() {
        return successChancePerHappiness;
    }

    public double getMinSuccessChance() {
        return minSuccessChance;
    }

    public double getMaxSuccessChance() {
        return maxSuccessChance;
    }

    public boolean isFailPenaltyEnabled() {
        return failPenaltyEnabled;
    }

    public double getFailExpLossPercent() {
        return failExpLossPercent;
    }

    public double getFailHappinessLoss() {
        return failHappinessLoss;
    }

    public boolean isFailConsumeItems() {
        return failConsumeItems;
    }

    public double getStatBonusPerStage() {
        return statBonusPerStage;
    }

    public int getSkillSlotsPerStage() {
        return skillSlotsPerStage;
    }

    public double getRarityUpgradeChance() {
        return rarityUpgradeChance;
    }

    public boolean isEvolutionEffects() {
        return evolutionEffects;
    }

    public boolean isEvolutionSound() {
        return evolutionSound;
    }

    public boolean isEvolutionBroadcast() {
        return evolutionBroadcast;
    }

    public int getBroadcastMinStage() {
        return broadcastMinStage;
    }

    public int getEvolutionCooldown() {
        return evolutionCooldown;
    }

    public List<String> getBlockedWorlds() {
        return blockedWorlds;
    }

    // ===== 계산 메서드 =====

    /**
     * 성공 확률 계산
     */
    public double calculateSuccessChance(int petLevel, double happiness, int targetStage) {
        double chance = baseSuccessChance;

        // 레벨 보너스
        chance += petLevel * successChancePerLevel;

        // 행복도 보너스
        chance += happiness * successChancePerHappiness;

        // 단계별 페널티 (높은 단계일수록 어려움)
        chance -= (targetStage - 1) * 5;

        // 범위 제한
        return Math.max(minSuccessChance, Math.min(maxSuccessChance, chance));
    }

    /**
     * 단계별 스탯 보너스
     */
    public double getStatMultiplier(int evolutionStage) {
        return 1.0 + ((evolutionStage - 1) * statBonusPerStage / 100);
    }

    /**
     * 단계별 총 스킬 슬롯 보너스
     */
    public int getTotalSkillSlotBonus(int evolutionStage) {
        return (evolutionStage - 1) * skillSlotsPerStage;
    }

    /**
     * 월드 차단 여부
     */
    public boolean isBlockedWorld(String worldName) {
        return blockedWorlds.contains(worldName);
    }

    /**
     * 브로드캐스트 여부
     */
    public boolean shouldBroadcast(int evolutionStage) {
        return evolutionBroadcast && evolutionStage >= broadcastMinStage;
    }
}