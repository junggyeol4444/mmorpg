package com.multiverse.pet.      util;

import com.multiverse.pet.model.      Pet;
import com.multiverse.pet.model.      PetRarity;

/**
 * 경험치 계산기
 * 펫 레벨업에 필요한 경험치 및 획득 경험치 계산
 */
public class ExpCalculator {

    // 기본 상수
    private static final long BASE_EXP = 100;            // 레벨 1 -> 2 기본 경험치
    private static final double EXP_GROWTH_RATE = 1.15;  // 레벨당 경험치 증가율
    private static final int MAX_LEVEL = 100;            // 최대 레벨

    // 경험치 획득 배율
    private static final double KILL_EXP_BASE = 10;
    private static final double BOSS_KILL_MULTIPLIER = 5.0;
    private static final double PLAYER_KILL_MULTIPLIER = 3.0;
    private static final double BATTLE_WIN_MULTIPLIER = 2.0;

    /**
     * 특정 레벨에 필요한 총 경험치
     */
    public static long getTotalExpForLevel(int level) {
        if (level <= 1) return 0;
        if (level > MAX_LEVEL) level = MAX_LEVEL;

        long totalExp = 0;
        for (int i = 1; i < level; i++) {
            totalExp += getExpToNextLevel(i);
        }
        return totalExp;
    }

    /**
     * 현재 레벨에서 다음 레벨까지 필요한 경험치
     */
    public static long getExpToNextLevel(int currentLevel) {
        if (currentLevel >= MAX_LEVEL) return Long.MAX_VALUE;

        return (long) (BASE_EXP * Math.pow(EXP_GROWTH_RATE, currentLevel - 1));
    }

    /**
     * 펫의 다음 레벨까지 필요한 경험치
     */
    public static long getExpToNextLevel(Pet pet) {
        return getExpToNextLevel(pet.getLevel());
    }

    /**
     * 경험치로부터 레벨 계산
     */
    public static int getLevelFromExp(long totalExp) {
        int level = 1;
        long expRemaining = totalExp;

        while (level < MAX_LEVEL) {
            long needed = getExpToNextLevel(level);
            if (expRemaining < needed) break;
            expRemaining -= needed;
            level++;
        }

        return level;
    }

    /**
     * 경험치 진행률 (%)
     */
    public static double getExpPercentage(Pet pet) {
        if (pet. getLevel() >= MAX_LEVEL) return 100.0;

        long currentExp = pet.getExperience();
        long totalForCurrent = getTotalExpForLevel(pet. getLevel());
        long expInCurrentLevel = currentExp - totalForCurrent;
        long expNeeded = getExpToNextLevel(pet. getLevel());

        if (expNeeded <= 0) return 100.0;

        return (double) expInCurrentLevel / expNeeded * 100;
    }

    /**
     * 레벨업 가능 여부
     */
    public static boolean canLevelUp(Pet pet) {
        if (pet.getLevel() >= MAX_LEVEL) return false;

        long totalForNext = getTotalExpForLevel(pet.getLevel() + 1);
        return pet.getExperience() >= totalForNext;
    }

    /**
     * 레벨업 시 증가하는 레벨 수 계산
     */
    public static int calculateLevelsGained(Pet pet, long newTotalExp) {
        int currentLevel = pet.getLevel();
        int newLevel = getLevelFromExp(newTotalExp);
        return Math.max(0, newLevel - currentLevel);
    }

    // ===== 경험치 획득 계산 =====

    /**
     * 몬스터 처치 경험치
     */
    public static int calculateKillExp(Pet pet, org.bukkit.entity.EntityType entityType) {
        double baseExp = getEntityBaseExp(entityType);

        // 펫 레벨 보정 (높은 레벨 펫은 낮은 몹에서 경험치 감소)
        int petLevel = pet.getLevel();
        int mobLevel = estimateMobLevel(entityType);

        double levelDiff = mobLevel - petLevel;
        double levelModifier = 1.0;

        if (levelDiff < -10) {
            levelModifier = 0.1; // 너무 낮은 몹
        } else if (levelDiff < -5) {
            levelModifier = 0.5;
        } else if (levelDiff > 10) {
            levelModifier = 1.5; // 높은 몹 보너스
        }

        // 희귀도 보너스
        double rarityBonus = 1.0 + (pet.getRarity().ordinal() * 0.05);

        // 행복도 보너스
        double happinessBonus = 1.0 + (pet.getHappiness() / 200);

        return (int) (baseExp * levelModifier * rarityBonus * happinessBonus);
    }

    /**
     * 엔티티 기본 경험치
     */
    private static double getEntityBaseExp(org.bukkit.entity.EntityType type) {
        switch (type) {
            // 약한 몹
            case ZOMBIE: 
            case SKELETON:
            case SPIDER:
            case CREEPER:
                return KILL_EXP_BASE;

            // 중간 몹
            case ENDERMAN:
            case BLAZE:
            case WITCH: 
            case PIGLIN:
                return KILL_EXP_BASE * 2;

            // 강한 몹
            case WITHER_SKELETON: 
            case PIGLIN_BRUTE:
            case RAVAGER:
            case EVOKER:
                return KILL_EXP_BASE * 3;

            // 보스
            case ENDER_DRAGON: 
                return KILL_EXP_BASE * BOSS_KILL_MULTIPLIER * 10;
            case WITHER: 
                return KILL_EXP_BASE * BOSS_KILL_MULTIPLIER * 8;
            case ELDER_GUARDIAN: 
                return KILL_EXP_BASE * BOSS_KILL_MULTIPLIER * 5;
            case WARDEN:
                return KILL_EXP_BASE * BOSS_KILL_MULTIPLIER * 6;

            default:
                return KILL_EXP_BASE * 0.5;
        }
    }

    /**
     * 몹 레벨 추정
     */
    private static int estimateMobLevel(org.bukkit.entity.EntityType type) {
        switch (type) {
            case ZOMBIE: 
            case SKELETON: 
            case SPIDER:
                return 5;
            case CREEPER:
            case WITCH:
                return 10;
            case ENDERMAN:
            case BLAZE: 
                return 20;
            case WITHER_SKELETON:
            case PIGLIN_BRUTE:
                return 35;
            case EVOKER: 
            case RAVAGER: 
                return 50;
            case WARDEN:
                return 80;
            case ENDER_DRAGON:
            case WITHER: 
                return 100;
            default: 
                return 1;
        }
    }

    /**
     * 배틀 경험치 계산
     */
    public static int calculateBattleExp(Pet pet, boolean won, int opponentLevel) {
        double baseExp = 50;

        // 승패 보정
        double winModifier = won ?  BATTLE_WIN_MULTIPLIER : 0.5;

        // 레벨 차이 보정
        int levelDiff = opponentLevel - pet. getLevel();
        double levelModifier = 1.0;

        if (levelDiff > 0) {
            levelModifier = 1.0 + (levelDiff * 0.1); // 높은 상대 보너스
        } else if (levelDiff < -10) {
            levelModifier = 0.3; // 낮은 상대 패널티
        }

        // 희귀도 보너스
        double rarityBonus = 1.0 + (pet.getRarity().ordinal() * 0.05);

        return (int) (baseExp * winModifier * levelModifier * rarityBonus);
    }

    /**
     * 훈련 경험치 계산
     */
    public static int calculateTrainingExp(Pet pet, int trainingMinutes) {
        double baseExp = 5 * trainingMinutes;

        // 레벨이 높을수록 훈련 효율 감소
        double levelModifier = 1.0 - (pet.getLevel() * 0.005);
        levelModifier = Math. max(0.3, levelModifier);

        // 행복도 보너스
        double happinessBonus = pet.getHappiness() / 100;

        return (int) (baseExp * levelModifier * happinessBonus);
    }

    /**
     * 채집 경험치 계산
     */
    public static int calculateGatheringExp(Pet pet, String resourceType) {
        double baseExp = 3;

        switch (resourceType. toLowerCase()) {
            case "diamond":
            case "emerald":
            case "ancient_debris":
                baseExp = 20;
                break;
            case "gold": 
            case "iron":
            case "lapis": 
            case "redstone":
                baseExp = 10;
                break;
            case "coal":
            case "copper":
                baseExp = 5;
                break;
            default:
                baseExp = 3;
                break;
        }

        // 희귀도 보너스
        double rarityBonus = 1.0 + (pet.getRarity().ordinal() * 0.1);

        return (int) (baseExp * rarityBonus);
    }

    /**
     * 보너스 경험치 적용
     */
    public static long applyExpBonus(long baseExp, double bonusPercent) {
        return (long) (baseExp * (1 + bonusPercent / 100));
    }

    /**
     * 레벨 범위 확인
     */
    public static boolean isValidLevel(int level) {
        return level >= 1 && level <= MAX_LEVEL;
    }

    /**
     * 최대 레벨
     */
    public static int getMaxLevel() {
        return MAX_LEVEL;
    }

    /**
     * 경험치 표시 문자열
     */
    public static String formatExp(long exp) {
        if (exp >= 1000000) {
            return String. format("%.1fM", exp / 1000000.0);
        } else if (exp >= 1000) {
            return String.format("%.1fK", exp / 1000.0);
        } else {
            return String.valueOf(exp);
        }
    }
}