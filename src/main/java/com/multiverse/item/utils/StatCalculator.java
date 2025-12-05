package com.multiverse. item.utils;

import java. util.HashMap;
import java.util.Map;

public class StatCalculator {
    
    /**
     * 기본 스탯에 강화 레벨 적용
     */
    public static double calculateEnhancedStat(double baseStat, int enhanceLevel) {
        // 레벨당 5% 증가
        return baseStat * (1. 0 + (enhanceLevel * 0.05));
    }
    
    /**
     * 희귀도에 따른 스탯 배수
     */
    public static double getRarityMultiplier(String rarity) {
        switch (rarity.toLowerCase()) {
            case "common":
                return 1.0;
            case "uncommon":
                return 1.2;
            case "rare":
                return 1.5;
            case "epic":
                return 1.8;
            case "legendary":
                return 2.2;
            case "unique":
                return 2.5;
            default:
                return 1.0;
        }
    }
    
    /**
     * 희귀도 적용된 스탯 계산
     */
    public static double calculateRarityStat(double baseStat, String rarity) {
        return baseStat * getRarityMultiplier(rarity);
    }
    
    /**
     * 세트 보너스 계산
     */
    public static double calculateSetBonus(double baseStat, int setItemCount) {
        // 각 세트 아이템마다 10% 보너스
        return baseStat * (1.0 + (setItemCount * 0.1));
    }
    
    /**
     * 옵션 값 계산 (퍼센티지/절대값)
     */
    public static double calculateOptionValue(double baseValue, double optionValue, boolean isPercentage) {
        if (isPercentage) {
            return baseValue * (1.0 + (optionValue / 100.0));
        } else {
            return baseValue + optionValue;
        }
    }
    
    /**
     * 공격력 계산
     */
    public static double calculateDamage(double weaponDamage, double statDamage, int enhanceLevel, String rarity) {
        double enhanced = calculateEnhancedStat(weaponDamage, enhanceLevel);
        double withRarity = calculateRarityStat(enhanced, rarity);
        return withRarity + statDamage;
    }
    
    /**
     * 방어력 계산
     */
    public static double calculateDefense(double armorDefense, double statDefense, int enhanceLevel, String rarity) {
        double enhanced = calculateEnhancedStat(armorDefense, enhanceLevel);
        double withRarity = calculateRarityStat(enhanced, rarity);
        return withRarity + statDefense;
    }
    
    /**
     * 피해 감소 율 계산
     */
    public static double calculateDamageReduction(double defense) {
        // 방어력 10당 1% 피해 감소
        return Math.min(90, defense / 10.0);
    }
    
    /**
     * 최종 받는 피해 계산
     */
    public static double calculateIncomingDamage(double baseDamage, double defense) {
        double reductionRate = calculateDamageReduction(defense) / 100.0;
        return baseDamage * (1.0 - reductionRate);
    }
    
    /**
     * 생명력 흡수량 계산
     */
    public static double calculateLifesteal(double damage, double lifestealPercent) {
        return damage * (lifestealPercent / 100. 0);
    }
    
    /**
     * 치명타 피해 계산
     */
    public static double calculateCriticalDamage(double damage, double criticalDamagePercent) {
        return damage * (1.0 + (criticalDamagePercent / 100. 0));
    }
    
    /**
     * 다중 옵션 누적 계산
     */
    public static double calculateCumulativeStats(Map<String, Double> stats) {
        double total = 0;
        for (double value : stats.values()) {
            total += value;
        }
        return total;
    }
    
    /**
     * 강화 비용 계산
     */
    public static int calculateEnhanceCost(int enhanceLevel) {
        return 1000 + (enhanceLevel * 500);
    }
    
    /**
     * 강화 성공률 계산
     */
    public static double calculateEnhanceSuccessRate(int enhanceLevel) {
        double rate = 100.0 - (enhanceLevel * 5.0);
        return Math.max(10.0, rate);
    }
    
    /**
     * 최종 스탯 계산 (모든 보너스 적용)
     */
    public static double calculateFinalStat(
            double baseStat,
            String rarity,
            int enhanceLevel,
            int setItemCount,
            Map<String, Double> options) {
        
        // 1단계: 기본 스탯 적용
        double stat = baseStat;
        
        // 2단계: 희귀도 적용
        stat = calculateRarityStat(stat, rarity);
        
        // 3단계: 강화 적용
        stat = calculateEnhancedStat(stat, enhanceLevel);
        
        // 4단계: 세트 보너스 적용
        stat = calculateSetBonus(stat, setItemCount);
        
        // 5단계: 옵션 적용
        if (options != null) {
            for (double optionValue : options.values()) {
                stat += optionValue;
            }
        }
        
        return stat;
    }
}