package com.multiverse.dungeon.managers;

import com.multiverse.dungeon.  data.enums.DungeonDifficulty;
import org.bukkit.configuration.Configuration;

/**
 * 난이도 및 파티 크기에 따른 스케일링 관리
 */
public class ScalingManager {

    private final Configuration config;
    
    // 난이도별 배율
    private final double easyHealthMultiplier;
    private final double easyDamageMultiplier;
    private final double easyRewardMultiplier;
    
    private final double normalHealthMultiplier;
    private final double normalDamageMultiplier;
    private final double normalRewardMultiplier;
    
    private final double hardHealthMultiplier;
    private final double hardDamageMultiplier;
    private final double hardRewardMultiplier;
    
    private final double extremeHealthMultiplier;
    private final double extremeDamageMultiplier;
    private final double extremeRewardMultiplier;
    
    // 파티 스케일링
    private final boolean enablePartyScaling;
    private final double partyScalingPerPlayer;

    /**
     * 생성자
     */
    public ScalingManager(Configuration config) {
        this. config = config;
        
        // 난이도별 배율 로드
        this.easyHealthMultiplier = config.getDouble("difficulty. EASY.mob-health", 0.7);
        this.easyDamageMultiplier = config.  getDouble("difficulty.EASY. mob-damage", 0.8);
        this.easyRewardMultiplier = config.getDouble("difficulty.EASY.reward", 1.0);
        
        this.normalHealthMultiplier = config.getDouble("difficulty.NORMAL.mob-health", 1.0);
        this.normalDamageMultiplier = config.getDouble("difficulty.NORMAL.mob-damage", 1. 0);
        this.normalRewardMultiplier = config.getDouble("difficulty.NORMAL.reward", 1.5);
        
        this.hardHealthMultiplier = config.getDouble("difficulty.HARD.mob-health", 1.5);
        this.hardDamageMultiplier = config.getDouble("difficulty.HARD.mob-damage", 1.2);
        this.hardRewardMultiplier = config.getDouble("difficulty.HARD.reward", 2.0);
        
        this.extremeHealthMultiplier = config.getDouble("difficulty. EXTREME.mob-health", 2.0);
        this.extremeDamageMultiplier = config.getDouble("difficulty.EXTREME.mob-damage", 1. 5);
        this.extremeRewardMultiplier = config. getDouble("difficulty.EXTREME. reward", 3.0);
        
        // 파티 스케일링
        this.enablePartyScaling = config.getBoolean("dungeons.scaling.enable-party-scaling", true);
        this.partyScalingPerPlayer = config.getDouble("dungeons.scaling.party-scaling-per-player", 0.15);
    }

    /**
     * 난이도에 따른 몬스터 체력 배율
     *
     * @param difficulty 난이도
     * @param partySize 파티 크기
     * @return 체력 배율
     */
    public double getMobHealthMultiplier(DungeonDifficulty difficulty, int partySize) {
        double baseMultiplier = switch (difficulty) {
            case EASY -> easyHealthMultiplier;
            case NORMAL -> normalHealthMultiplier;
            case HARD -> hardHealthMultiplier;
            case EXTREME -> extremeHealthMultiplier;
        };

        // 파티 크기에 따른 추가 배율
        if (enablePartyScaling && partySize > 1) {
            double partyMultiplier = 1.0 + ((partySize - 1) * partyScalingPerPlayer);
            return baseMultiplier * partyMultiplier;
        }

        return baseMultiplier;
    }

    /**
     * 난이도에 따른 몬스터 데미지 배율
     *
     * @param difficulty 난이도
     * @param partySize 파티 크기
     * @return 데미지 배율
     */
    public double getMobDamageMultiplier(DungeonDifficulty difficulty, int partySize) {
        double baseMultiplier = switch (difficulty) {
            case EASY -> easyDamageMultiplier;
            case NORMAL -> normalDamageMultiplier;
            case HARD -> hardDamageMultiplier;
            case EXTREME -> extremeDamageMultiplier;
        };

        // 파티 크기에 따른 추가 배율 (데미지는 작게)
        if (enablePartyScaling && partySize > 1) {
            double partyMultiplier = 1.0 + ((partySize - 1) * (partyScalingPerPlayer * 0.5));
            return baseMultiplier * partyMultiplier;
        }

        return baseMultiplier;
    }

    /**
     * 난이도에 따른 보상 배율
     *
     * @param difficulty 난이도
     * @return 보상 배율
     */
    public double getRewardMultiplier(DungeonDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> easyRewardMultiplier;
            case NORMAL -> normalRewardMultiplier;
            case HARD -> hardRewardMultiplier;
            case EXTREME -> extremeRewardMultiplier;
        };
    }

    /**
     * 파티 크기 스케일링 배율
     *
     * @param partySize 파티 크기
     * @return 스케일링 배율
     */
    public double getPartyScalingMultiplier(int partySize) {
        if (! enablePartyScaling || partySize <= 1) {
            return 1.0;
        }

        return 1.0 + ((partySize - 1) * partyScalingPerPlayer);
    }

    /**
     * 파티 스케일링 활성화 여부
     *
     * @return 활성화되었으면 true
     */
    public boolean isPartyScalingEnabled() {
        return enablePartyScaling;
    }

    /**
     * 파티 플레이어당 스케일링 배율
     *
     * @return 배율
     */
    public double getPartyScalingPerPlayer() {
        return partyScalingPerPlayer;
    }

    /**
     * 총 배율 계산 (난이도 + 파티)
     *
     * @param difficulty 난이도
     * @param partySize 파티 크기
     * @param baseValue 기본값
     * @return 최종 값
     */
    public double calculateScaledValue(DungeonDifficulty difficulty, int partySize, double baseValue) {
        double healthMultiplier = getMobHealthMultiplier(difficulty, partySize);
        return baseValue * healthMultiplier;
    }
}