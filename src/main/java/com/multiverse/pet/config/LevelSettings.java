package com. multiverse.pet.             config;

import org.bukkit.            configuration.  file.FileConfiguration;

/**
 * 레벨 설정
 * 펫 레벨 및 경험치 관련 설정
 */
public class LevelSettings {

    private final FileConfiguration config;
    private final String basePath = "level";

    // 레벨 설정
    private int maxLevel;
    private long baseExp;
    private double expGrowthRate;
    private int skillPointsPerLevel;

    // 경험치 획득
    private double mobKillExpMultiplier;
    private double playerKillExpMultiplier;
    private double bossKillExpMultiplier;
    private double battleExpMultiplier;
    private double trainingExpMultiplier;
    private double gatheringExpMultiplier;

    // 경험치 보너스
    private double happinessExpBonus;
    private double rarityExpBonus;
    private double partyExpBonus;

    // 레벨업 효과
    private boolean levelUpEffects;
    private boolean levelUpSound;
    private boolean levelUpTitle;
    private boolean levelUpBroadcast;
    private int broadcastMinLevel;

    // 레벨 제한
    private int minCaptureLevel;
    private int minBreedingLevel;
    private int minBattleLevel;

    public LevelSettings(FileConfiguration config) {
        this.config = config;
        load();
    }

    /**
     * 설정 로드
     */
    private void load() {
        // 레벨 설정
        maxLevel = config.getInt(basePath + ". max-level", 100);
        baseExp = config.getLong(basePath + ".base-exp", 100);
        expGrowthRate = config. getDouble(basePath + ".exp-growth-rate", 1.15);
        skillPointsPerLevel = config.getInt(basePath + ".skill-points-per-level", 1);

        // 경험치 획득
        mobKillExpMultiplier = config.getDouble(basePath + ". exp-multipliers.mob-kill", 1.0);
        playerKillExpMultiplier = config.getDouble(basePath + ".exp-multipliers.player-kill", 3.0);
        bossKillExpMultiplier = config.getDouble(basePath + ".exp-multipliers.boss-kill", 5.0);
        battleExpMultiplier = config.getDouble(basePath + ". exp-multipliers. battle", 2.0);
        trainingExpMultiplier = config.getDouble(basePath + ". exp-multipliers. training", 0.5);
        gatheringExpMultiplier = config. getDouble(basePath + ".exp-multipliers.gathering", 0.3);

        // 경험치 보너스
        happinessExpBonus = config.getDouble(basePath + ". exp-bonuses.happiness", 0.1);
        rarityExpBonus = config.getDouble(basePath + ".exp-bonuses.rarity", 0.05);
        partyExpBonus = config.getDouble(basePath + ".exp-bonuses.party", 0.2);

        // 레벨업 효과
        levelUpEffects = config.getBoolean(basePath + ".level-up.effects", true);
        levelUpSound = config. getBoolean(basePath + ".level-up.sound", true);
        levelUpTitle = config.getBoolean(basePath + ".level-up.title", true);
        levelUpBroadcast = config.getBoolean(basePath + ".level-up.broadcast", true);
        broadcastMinLevel = config.getInt(basePath + ".level-up.broadcast-min-level", 50);

        // 레벨 제한
        minCaptureLevel = config.getInt(basePath + ".requirements.min-capture-level", 1);
        minBreedingLevel = config.getInt(basePath + ".requirements. min-breeding-level", 10);
        minBattleLevel = config.getInt(basePath + ".requirements. min-battle-level", 5);
    }

    /**
     * 리로드
     */
    public void reload() {
        load();
    }

    // ===== Getter =====

    public int getMaxLevel() {
        return maxLevel;
    }

    public long getBaseExp() {
        return baseExp;
    }

    public double getExpGrowthRate() {
        return expGrowthRate;
    }

    public int getSkillPointsPerLevel() {
        return skillPointsPerLevel;
    }

    public double getMobKillExpMultiplier() {
        return mobKillExpMultiplier;
    }

    public double getPlayerKillExpMultiplier() {
        return playerKillExpMultiplier;
    }

    public double getBossKillExpMultiplier() {
        return bossKillExpMultiplier;
    }

    public double getBattleExpMultiplier() {
        return battleExpMultiplier;
    }

    public double getTrainingExpMultiplier() {
        return trainingExpMultiplier;
    }

    public double getGatheringExpMultiplier() {
        return gatheringExpMultiplier;
    }

    public double getHappinessExpBonus() {
        return happinessExpBonus;
    }

    public double getRarityExpBonus() {
        return rarityExpBonus;
    }

    public double getPartyExpBonus() {
        return partyExpBonus;
    }

    public boolean isLevelUpEffects() {
        return levelUpEffects;
    }

    public boolean isLevelUpSound() {
        return levelUpSound;
    }

    public boolean isLevelUpTitle() {
        return levelUpTitle;
    }

    public boolean isLevelUpBroadcast() {
        return levelUpBroadcast;
    }

    public int getBroadcastMinLevel() {
        return broadcastMinLevel;
    }

    public int getMinCaptureLevel() {
        return minCaptureLevel;
    }

    public int getMinBreedingLevel() {
        return minBreedingLevel;
    }

    public int getMinBattleLevel() {
        return minBattleLevel;
    }

    // ===== 계산 메서드 =====

    /**
     * 특정 레벨까지 필요한 총 경험치
     */
    public long getTotalExpForLevel(int level) {
        if (level <= 1) return 0;
        if (level > maxLevel) level = maxLevel;

        long total = 0;
        for (int i = 1; i < level; i++) {
            total += getExpToNextLevel(i);
        }
        return total;
    }

    /**
     * 다음 레벨까지 필요한 경험치
     */
    public long getExpToNextLevel(int currentLevel) {
        if (currentLevel >= maxLevel) return Long.MAX_VALUE;
        return (long) (baseExp * Math.pow(expGrowthRate, currentLevel - 1));
    }

    /**
     * 레벨업 시 획득 스킬 포인트
     */
    public int getSkillPointsForLevelUp(int fromLevel, int toLevel) {
        return (toLevel - fromLevel) * skillPointsPerLevel;
    }

    /**
     * 레벨 브로드캐스트 여부
     */
    public boolean shouldBroadcastLevelUp(int level) {
        return levelUpBroadcast && level >= broadcastMinLevel;
    }
}