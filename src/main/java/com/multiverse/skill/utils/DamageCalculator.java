package com.multiverse.skill. utils;

import com.multiverse.skill.data.models.LearnedSkill;
import com.multiverse.skill.data. enums.DamageType;

/**
 * 데미지 계산 유틸리티
 */
public class DamageCalculator {

    /**
     * 기본 데미지 계산
     */
    public static double calculateBaseDamage(double baseDamage, LearnedSkill learnedSkill) {
        if (learnedSkill == null) {
            return baseDamage;
        }

        // 레벨에 따른 배수 적용
        double levelMultiplier = 1.0 + (learnedSkill. getLevel() * 0.1);
        return baseDamage * levelMultiplier;
    }

    /**
     * 데미지 타입별 계산
     */
    public static double calculateDamageByType(double baseDamage, DamageType damageType) {
        if (damageType == null) {
            return baseDamage;
        }

        switch (damageType) {
            case PHYSICAL:
                return baseDamage * 1.0;
            case MAGICAL:
                return baseDamage * 0.9;
            case TRUE:
                return baseDamage * 1.2;
            case FIXED:
                return baseDamage;
            default:
                return baseDamage;
        }
    }

    /**
     * 방어력 계산
     */
    public static double calculateWithDefense(double damage, double defense) {
        if (defense <= 0) {
            return damage;
        }

        // 방어력에 따른 감소율 (최대 80%)
        double defenseReduction = Math.min(0.8, defense / 100.0);
        return damage * (1.0 - defenseReduction);
    }

    /**
     * 크리티컬 데미지 계산
     */
    public static double calculateCriticalDamage(double baseDamage, double criticalChance, double criticalMultiplier) {
        if (Math.random() < criticalChance) {
            return baseDamage * criticalMultiplier;
        }

        return baseDamage;
    }

    /**
     * 스케일링 데미지 계산
     */
    public static double calculateScalingDamage(double baseDamage, double scalingValue, double scalingMultiplier) {
        return baseDamage + (scalingValue * scalingMultiplier);
    }

    /**
     * 최종 데미지 계산
     */
    public static double calculateFinalDamage(double baseDamage, LearnedSkill learnedSkill, 
                                             DamageType damageType, double defense) {
        double damage = calculateBaseDamage(baseDamage, learnedSkill);
        damage = calculateDamageByType(damage, damageType);
        damage = calculateWithDefense(damage, defense);

        return Math.max(1.0, damage); // 최소 1 데미지
    }

    /**
     * 범위 데미지 계산 (거리에 따른 감소)
     */
    public static double calculateRangeDamage(double baseDamage, double distance, double maxRange) {
        if (distance > maxRange) {
            return 0.0;
        }

        double damageReduction = distance / maxRange;
        return baseDamage * (1.0 - damageReduction * 0.5); // 최대 50% 감소
    }
}