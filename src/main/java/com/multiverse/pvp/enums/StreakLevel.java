package com.multiverse.pvp. enums;

public enum StreakLevel {

    DOUBLE_KILL(2, "&eDouble Kill!", "더블 킬", 1.5, 50),
    TRIPLE_KILL(3, "&6Triple Kill!", "트리플 킬", 2.0, 100),
    KILLING_SPREE(5, "&cKilling Spree!", "킬링 스프리", 3.0, 200),
    RAMPAGE(10, "&4Rampage!", "램페이지", 5.0, 500),
    UNSTOPPABLE(15, "&5Unstoppable!", "언스탑퍼블", 7.0, 1000),
    GODLIKE(20, "&d&lGodlike!", "갓라이크", 10.0, 2000);

    private final int killsRequired;
    private final String announcement;
    private final String displayName;
    private final double rewardMultiplier;
    private final int bonusPoints;

    StreakLevel(int killsRequired, String announcement, String displayName, double rewardMultiplier, int bonusPoints) {
        this.killsRequired = killsRequired;
        this.announcement = announcement;
        this.displayName = displayName;
        this.rewardMultiplier = rewardMultiplier;
        this. bonusPoints = bonusPoints;
    }

    public int getKillsRequired() {
        return killsRequired;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    /**
     * 킬 수로 스트릭 레벨 조회
     */
    public static StreakLevel getLevel(int kills) {
        StreakLevel result = null;
        
        for (StreakLevel level : values()) {
            if (kills >= level.killsRequired) {
                result = level;
            }
        }
        
        return result;
    }

    /**
     * 정확히 해당 킬 수에 도달했는지 확인
     */
    public static StreakLevel getExactLevel(int kills) {
        for (StreakLevel level : values()) {
            if (kills == level.killsRequired) {
                return level;
            }
        }
        return null;
    }

    /**
     * 다음 스트릭 레벨 반환
     */
    public StreakLevel getNextLevel() {
        switch (this) {
            case DOUBLE_KILL:
                return TRIPLE_KILL;
            case TRIPLE_KILL: 
                return KILLING_SPREE;
            case KILLING_SPREE: 
                return RAMPAGE;
            case RAMPAGE:
                return UNSTOPPABLE;
            case UNSTOPPABLE: 
                return GODLIKE;
            case GODLIKE: 
                return null; // 최고 레벨
            default:
                return null;
        }
    }

    /**
     * 이전 스트릭 레벨 반환
     */
    public StreakLevel getPreviousLevel() {
        switch (this) {
            case DOUBLE_KILL:
                return null; // 최저 레벨
            case TRIPLE_KILL: 
                return DOUBLE_KILL;
            case KILLING_SPREE: 
                return TRIPLE_KILL;
            case RAMPAGE: 
                return KILLING_SPREE;
            case UNSTOPPABLE:
                return RAMPAGE;
            case GODLIKE:
                return UNSTOPPABLE;
            default: 
                return null;
        }
    }

    /**
     * 다음 레벨까지 필요한 킬 수
     */
    public static int getKillsToNextLevel(int currentKills) {
        for (StreakLevel level : values()) {
            if (currentKills < level.killsRequired) {
                return level.killsRequired - currentKills;
            }
        }
        return 0; // 최고 레벨 도달
    }

    /**
     * 현재 킬 수에서 다음 레벨 반환
     */
    public static StreakLevel getNextLevel(int currentKills) {
        for (StreakLevel level : values()) {
            if (currentKills < level. killsRequired) {
                return level;
            }
        }
        return null; // 최고 레벨 도달
    }

    /**
     * 셧다운 보상 포인트 (스트릭을 끊을 때 받는 보상)
     */
    public int getShutdownBonus() {
        return bonusPoints / 2;
    }

    /**
     * 최고 레벨인지 확인
     */
    public boolean isMaxLevel() {
        return this == GODLIKE;
    }

    /**
     * 최저 레벨인지 확인
     */
    public boolean isMinLevel() {
        return this == DOUBLE_KILL;
    }

    /**
     * 브로드캐스트 여부 (서버 전체 공지)
     */
    public boolean shouldBroadcast() {
        return this == RAMPAGE || this == UNSTOPPABLE || this == GODLIKE;
    }

    /**
     * 효과음 타입
     */
    public org.bukkit.Sound getSound() {
        switch (this) {
            case DOUBLE_KILL: 
                return org.bukkit.Sound. ENTITY_EXPERIENCE_ORB_PICKUP;
            case TRIPLE_KILL: 
                return org. bukkit.Sound. ENTITY_PLAYER_LEVELUP;
            case KILLING_SPREE:
                return org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL;
            case RAMPAGE: 
                return org.bukkit.Sound. ENTITY_WITHER_SPAWN;
            case UNSTOPPABLE: 
                return org.bukkit.Sound. ENTITY_ENDER_DRAGON_DEATH;
            case GODLIKE:
                return org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE;
            default:
                return org.bukkit. Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }
    }

    /**
     * 파티클 효과 타입
     */
    public org.bukkit. Particle getParticle() {
        switch (this) {
            case DOUBLE_KILL:
                return org.bukkit.Particle. VILLAGER_HAPPY;
            case TRIPLE_KILL: 
                return org. bukkit.Particle.FLAME;
            case KILLING_SPREE:
                return org.bukkit. Particle.LAVA;
            case RAMPAGE: 
                return org.bukkit.Particle. EXPLOSION_LARGE;
            case UNSTOPPABLE:
                return org.bukkit. Particle.DRAGON_BREATH;
            case GODLIKE:
                return org.bukkit.Particle.END_ROD;
            default:
                return org.bukkit. Particle.VILLAGER_HAPPY;
        }
    }

    /**
     * 문자열로부터 StreakLevel 반환
     */
    public static StreakLevel fromString(String str) {
        if (str == null) {
            return null;
        }
        
        try {
            return valueOf(str.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            for (StreakLevel level : values()) {
                if (level.displayName.equalsIgnoreCase(str)) {
                    return level;
                }
            }
            return null;
        }
    }
}