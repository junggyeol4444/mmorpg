package com.multiverse.skill. utils;

import com.multiverse.skill.data.models.LearnedSkill;

/**
 * 스케일링 계산 유틸리티
 */
public class ScalingCalculator {

    /**
     * 공격력 스케일링 계산
     */
    public static double calculateAttackScaling(double baseScaling, double attackPower) {
        if (baseScaling <= 0 || attackPower <= 0) {
            return 0. 0;
        }

        return attackPower * baseScaling;
    }

    /**
     * 능력치 스케일링 계산
     */
    public static double calculateStatScaling(double baseScaling, double statValue) {
        if (baseScaling <= 0 || statValue <= 0) {
            return 0.0;
        }

        return statValue * baseScaling;
    }

    /**
     * 레벨 스케일링 계산
     */
    public static double calculateLevelScaling(double baseValue, LearnedSkill learnedSkill) {
        if (learnedSkill == null) {
            return baseValue;
        }

        int level = learnedSkill. getLevel();
        return baseValue * (1.0 + (level * 0.05)); // 레벨당 5% 증가
    }

    /**
     * 지수 스케일링 계산
     */
    public static double calculateExponentialScaling(double baseValue, double exponent) {
        return Math.pow(baseValue, exponent);
    }

    /**
     * 로그 스케일링 계산
     */
    public static double calculateLogarithmicScaling(double baseValue) {
        if (baseValue <= 0) {
            return 0.0;
        }

        return Math.log(baseValue + 1);
    }

    /**
     * 비율 스케일링 계산
     */
    public static double calculatePercentageScaling(double baseValue, double percentage) {
        return baseValue * (percentage / 100.0);
    }

    /**
     * 누적 스케일링 계산
     */
    public static double calculateCumulativeScaling(double baseValue, int count, double scalingPerCount) {
        return baseValue + (count * scalingPerCount);
    }

    /**
     * 감소 스케일링 계산 (거리에 따른 감소)
     */
    public static double calculateDecayScaling(double baseValue, double distance, double decayRate) {
        if (distance <= 0) {
            return baseValue;
        }

        return baseValue / (1.0 + (distance * decayRate));
    }
}