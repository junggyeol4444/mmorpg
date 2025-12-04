package com.multiverse.dungeon.data.enums;

/**
 * 던전 난이도 열거형
 * 각 난이도별 몬스터 체력, 데미지, 보상 배율 정의
 */
public enum DungeonDifficulty {

    /**
     * 쉬움
     * - 몬스터 체력: 70%
     * - 몬스터 데미지: 80%
     * - 보상 배율: 1. 0배
     */
    EASY("쉬움", 0. 7, 0.8, 1. 0, "&a"),

    /**
     * 보통
     * - 몬스터 체력: 100%
     * - 몬스터 데미지: 100%
     * - 보상 배율: 1.5배
     */
    NORMAL("보통", 1.0, 1.0, 1.5, "&e"),

    /**
     * 어려움
     * - 몬스터 체력: 150%
     * - 몬스터 데미지: 120%
     * - 보상 배율: 2. 0배
     */
    HARD("어려움", 1.5, 1.2, 2.0, "&c"),

    /**
     * 극악
     * - 몬스터 체력: 200%
     * - 몬스터 데미지: 150%
     * - 보상 배율: 3.0배
     */
    EXTREME("극악", 2.0, 1.5, 3.0, "&4");

    private final String displayName;
    private final double mobHealthMultiplier;
    private final double mobDamageMultiplier;
    private final double rewardMultiplier;
    private final String colorCode;

    DungeonDifficulty(String displayName, double health, double damage, double reward, String colorCode) {
        this. displayName = displayName;
        this.mobHealthMultiplier = health;
        this.mobDamageMultiplier = damage;
        this.rewardMultiplier = reward;
        this. colorCode = colorCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMobHealthMultiplier() {
        return mobHealthMultiplier;
    }

    public double getMobDamageMultiplier() {
        return mobDamageMultiplier;
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public String getColorCode() {
        return colorCode;
    }

    /**
     * 문자열로부터 DungeonDifficulty 조회
     *
     * @param name 난이도 이름
     * @return DungeonDifficulty, 없으면 NORMAL
     */
    public static DungeonDifficulty fromString(String name) {
        try {
            return DungeonDifficulty.valueOf(name. toUpperCase());
        } catch (IllegalArgumentException e) {
            return NORMAL;
        }
    }

    /**
     * 모든 난이도 반환
     *
     * @return 난이도 배열
     */
    public static DungeonDifficulty[] getAll() {
        return values();
    }
}