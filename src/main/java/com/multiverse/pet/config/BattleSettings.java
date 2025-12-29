package com.multiverse.pet.                 config;

import org.bukkit.                 configuration.file.FileConfiguration;

import java.util.                  List;

/**
 * 배틀 설정
 * 펫 배틀 관련 설정
 */
public class BattleSettings {

    private final FileConfiguration config;
    private final String basePath = "battle";

    // 기본 설정
    private boolean battleEnabled;
    private int minBattleLevel;
    private int maxTurns;
    private int turnTimeLimit;
    private int turnWarningTime;

    // AI 설정
    private int aiThinkingDelay;
    private boolean aiEnabled;
    private int maxAIDifficulty;

    // 매칭
    private int matchmakingTimeout;
    private int ratingRange;
    private int ratingRangeExpansion;
    private int maxRatingDifference;

    // 레이팅
    private int defaultRating;
    private int winRatingGain;
    private int loseRatingLoss;
    private int drawRatingChange;
    private int minRating;
    private int maxRating;

    // 보상
    private int winExpReward;
    private int loseExpReward;
    private double winGoldReward;
    private double loseGoldReward;
    private double challengeEntryFee;
    private double challengeWinReward;

    // 제한
    private int battleCooldown;
    private int maxBattlesPerDay;
    private double minHealthPercent;
    private double minHappiness;

    // 타임아웃
    private boolean autoDefendOnTimeout;
    private int maxConsecutiveTimeouts;

    // 효과
    private boolean battleEffects;
    private boolean battleSounds;
    private boolean showDamageNumbers;

    // 차단
    private List<String> blockedWorlds;
    private List<String> blockedSpecies;

    public BattleSettings(FileConfiguration config) {
        this.config = config;
        load();
    }

    /**
     * 설정 로드
     */
    private void load() {
        // 기본 설정
        battleEnabled = config.getBoolean(basePath + ". enabled", true);
        minBattleLevel = config.getInt(basePath + ".min-level", 5);
        maxTurns = config. getInt(basePath + ".max-turns", 30);
        turnTimeLimit = config.getInt(basePath + ".turn-time-limit", 30);
        turnWarningTime = config. getInt(basePath + ".turn-warning-time", 10);

        // AI 설정
        aiThinkingDelay = config.getInt(basePath + ".ai. thinking-delay", 20);
        aiEnabled = config.getBoolean(basePath + ".ai.enabled", true);
        maxAIDifficulty = config.getInt(basePath + ".ai.max-difficulty", 5);

        // 매칭
        matchmakingTimeout = config.getInt(basePath + ". matchmaking.timeout", 60);
        ratingRange = config.getInt(basePath + ".matchmaking.rating-range", 100);
        ratingRangeExpansion = config.getInt(basePath + ".matchmaking.range-expansion", 50);
        maxRatingDifference = config.getInt(basePath + ".matchmaking.max-difference", 500);

        // 레이팅
        defaultRating = config.getInt(basePath + ".rating. default", 1000);
        winRatingGain = config.getInt(basePath + ".rating. win-gain", 20);
        loseRatingLoss = config.getInt(basePath + ".rating.lose-loss", 15);
        drawRatingChange = config. getInt(basePath + ".rating.draw-change", 0);
        minRating = config.getInt(basePath + ".rating.min", 0);
        maxRating = config.getInt(basePath + ".rating.max", 9999);

        // 보상
        winExpReward = config.getInt(basePath + ".rewards.win-exp", 100);
        loseExpReward = config.getInt(basePath + ".rewards. lose-exp", 30);
        winGoldReward = config.getDouble(basePath + ". rewards.win-gold", 50.0);
        loseGoldReward = config.getDouble(basePath + ".rewards.lose-gold", 10.0);
        challengeEntryFee = config.getDouble(basePath + ".rewards.challenge-entry-fee", 100.0);
        challengeWinReward = config.getDouble(basePath + ".rewards.challenge-win-reward", 200.0);

        // 제한
        battleCooldown = config.getInt(basePath + ". limits.cooldown", 60);
        maxBattlesPerDay = config.getInt(basePath + ".limits.max-per-day", 20);
        minHealthPercent = config. getDouble(basePath + ".limits.min-health-percent", 50.0);
        minHappiness = config.getDouble(basePath + ".limits.min-happiness", 30.0);

        // 타임아웃
        autoDefendOnTimeout = config.getBoolean(basePath + ". timeout.auto-defend", true);
        maxConsecutiveTimeouts = config.getInt(basePath + ". timeout.max-consecutive", 3);

        // 효과
        battleEffects = config. getBoolean(basePath + ".effects. particles", true);
        battleSounds = config.getBoolean(basePath + ".effects.sounds", true);
        showDamageNumbers = config. getBoolean(basePath + ".effects. damage-numbers", true);

        // 차단
        blockedWorlds = config.getStringList(basePath + ".blocked-worlds");
        blockedSpecies = config.getStringList(basePath + ".blocked-species");
    }

    /**
     * 리로드
     */
    public void reload() {
        load();
    }

    // ===== Getter =====

    public boolean isBattleEnabled() {
        return battleEnabled;
    }

    public int getMinBattleLevel() {
        return minBattleLevel;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public int getTurnTimeLimit() {
        return turnTimeLimit;
    }

    public int getTurnWarningTime() {
        return turnWarningTime;
    }

    public int getAiThinkingDelay() {
        return aiThinkingDelay;
    }

    public boolean isAiEnabled() {
        return aiEnabled;
    }

    public int getMaxAIDifficulty() {
        return maxAIDifficulty;
    }

    public int getMatchmakingTimeout() {
        return matchmakingTimeout;
    }

    public int getRatingRange() {
        return ratingRange;
    }

    public int getRatingRangeExpansion() {
        return ratingRangeExpansion;
    }

    public int getMaxRatingDifference() {
        return maxRatingDifference;
    }

    public int getDefaultRating() {
        return defaultRating;
    }

    public int getWinRatingGain() {
        return winRatingGain;
    }

    public int getLoseRatingLoss() {
        return loseRatingLoss;
    }

    public int getDrawRatingChange() {
        return drawRatingChange;
    }

    public int getMinRating() {
        return minRating;
    }

    public int getMaxRating() {
        return maxRating;
    }

    public int getWinExpReward() {
        return winExpReward;
    }

    public int getLoseExpReward() {
        return loseExpReward;
    }

    public double getWinGoldReward() {
        return winGoldReward;
    }

    public double getLoseGoldReward() {
        return loseGoldReward;
    }

    public double getChallengeEntryFee() {
        return challengeEntryFee;
    }

    public double getChallengeWinReward() {
        return challengeWinReward;
    }

    public int getBattleCooldown() {
        return battleCooldown;
    }

    public int getMaxBattlesPerDay() {
        return maxBattlesPerDay;
    }

    public double getMinHealthPercent() {
        return minHealthPercent;
    }

    public double getMinHappiness() {
        return minHappiness;
    }

    public boolean isAutoDefendOnTimeout() {
        return autoDefendOnTimeout;
    }

    public int getMaxConsecutiveTimeouts() {
        return maxConsecutiveTimeouts;
    }

    public boolean isBattleEffects() {
        return battleEffects;
    }

    public boolean isBattleSounds() {
        return battleSounds;
    }

    public boolean isShowDamageNumbers() {
        return showDamageNumbers;
    }

    public List<String> getBlockedWorlds() {
        return blockedWorlds;
    }

    public List<String> getBlockedSpecies() {
        return blockedSpecies;
    }

    // ===== 계산 메서드 =====

    /**
     * 레이팅 변화 계산
     */
    public int calculateRatingChange(int myRating, int opponentRating, boolean won) {
        int diff = opponentRating - myRating;
        double multiplier = 1.0 + (diff / 400.0);
        multiplier = Math.max(0.5, Math.min(2.0, multiplier));

        if (won) {
            return (int) (winRatingGain * multiplier);
        } else {
            return (int) (-loseRatingLoss * (2. 0 - multiplier));
        }
    }

    /**
     * 레이팅 범위 내 확인
     */
    public int clampRating(int rating) {
        return Math. max(minRating, Math.min(maxRating, rating));
    }

    /**
     * 월드 차단 여부
     */
    public boolean isBlockedWorld(String worldName) {
        return blockedWorlds.contains(worldName);
    }

    /**
     * 종족 차단 여부
     */
    public boolean isBlockedSpecies(String speciesId) {
        return blockedSpecies.contains(speciesId);
    }

    /**
     * 배틀 가능 여부 확인
     */
    public boolean canBattle(int petLevel, double healthPercent, double happiness) {
        if (! battleEnabled) return false;
        if (petLevel < minBattleLevel) return false;
        if (healthPercent < minHealthPercent) return false;
        if (happiness < minHappiness) return false;
        return true;
    }
}