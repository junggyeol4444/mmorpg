package com.multiverse.playerdata.calculators;

import com.multiverse.playerdata.models.PlayerStats;
import com.multiverse.playerdata.models.StatValue;
import com.multiverse.playerdata.models.enums.StatType;

public class StatCalculator {

    /**
     * 최종 스탯 계산 (버프, 디버프 등 포함)
     */
    public static int calculateStat(PlayerStats stats, StatType type) {
        int base = stats.getBaseStats().getOrDefault(type, 0);
        // StatValue 확장 시 delta 등 포함
        return base;
    }

    /**
     * 두 스탯 값을 조합
     */
    public static int addStat(int statA, int statB) {
        return statA + statB;
    }

    /**
     * 배율 계산 (예: 성장 보너스)
     */
    public static int multiplyStat(int stat, double multiplier) {
        return (int) Math.round(stat * multiplier);
    }
}