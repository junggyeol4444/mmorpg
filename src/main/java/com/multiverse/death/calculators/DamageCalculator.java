package com.multiverse.playerdata.calculators;

import com.multiverse.playerdata.models.PlayerStats;
import com.multiverse.playerdata.models.enums.StatType;

public class DamageCalculator {

    /**
     * 물리 피해량 계산
     */
    public static double calculatePhysicalDamage(PlayerStats stats, int baseDamage) {
        int strength = stats.getBaseStats().getOrDefault(StatType.STR, 0);
        int dex = stats.getBaseStats().getOrDefault(StatType.DEX, 0);
        double result = baseDamage + strength * 1.5 + dex * 0.5;
        return result;
    }

    /**
     * 마법 피해량 계산
     */
    public static double calculateMagicDamage(PlayerStats stats, int baseDamage) {
        int intelligence = stats.getBaseStats().getOrDefault(StatType.INT, 0);
        int wisdom = stats.getBaseStats().getOrDefault(StatType.WIS, 0);
        double result = baseDamage + intelligence * 2.0 + wisdom * 0.7;
        return result;
    }

    /**
     * 치명타 여부
     */
    public static boolean isCriticalHit(PlayerStats stats) {
        int luck = stats.getBaseStats().getOrDefault(StatType.LUK, 0);
        double chance = luck * 0.005;
        return Math.random() < chance;
    }
}