package com. multiverse.pet. model;

/**
 * 펫 희귀도 열거형
 * 펫의 등급과 스탯 배율을 정의
 */
public enum PetRarity {

    /**
     * 일반 등급
     */
    COMMON("일반", "&f", "§f", 1.0, 50, 0.0),

    /**
     * 고급 등급
     */
    UNCOMMON("고급", "&a", "§a", 1.2, 75, 5.0),

    /**
     * 희귀 등급
     */
    RARE("희귀", "&9", "§9", 1.5, 100, 10.0),

    /**
     * 영웅 등급
     */
    EPIC("영웅", "&5", "§5", 2.0, 125, 15.0),

    /**
     * 전설 등급
     */
    LEGENDARY("전설", "&6", "§6", 3.0, 150, 25.0),

    /**
     * 신화 등급
     */
    MYTHIC("신화", "&c", "§c", 5.0, 200, 50.0);

    private final String displayName;
    private final String colorCode;
    private final String minecraftColor;
    private final double statMultiplier;
    private final int maxLevel;
    private final double expBonus;

    /**
     * PetRarity 생성자
     *
     * @param displayName 표시 이름
     * @param colorCode 색상 코드 (&)
     * @param minecraftColor 마인크래프트 색상 코드 (§)
     * @param statMultiplier 스탯 배율
     * @param maxLevel 최대 레벨
     * @param expBonus 경험치 보너스 (%)
     */
    PetRarity(String displayName, String colorCode, String minecraftColor,
              double statMultiplier, int maxLevel, double expBonus) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.minecraftColor = minecraftColor;
        this.statMultiplier = statMultiplier;
        this. maxLevel = maxLevel;
        this. expBonus = expBonus;
    }

    /**
     * 표시 이름 반환
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 색상 코드 반환 (&)
     */
    public String getColorCode() {
        return colorCode;
    }

    /**
     * 마인크래프트 색상 코드 반환 (§)
     */
    public String getMinecraftColor() {
        return minecraftColor;
    }

    /**
     * 색상 적용된 이름 반환
     */
    public String getColoredName() {
        return colorCode + displayName;
    }

    /**
     * 마인크래프트 색상 적용된 이름 반환
     */
    public String getMinecraftColoredName() {
        return minecraftColor + displayName;
    }

    /**
     * 스탯 배율 반환
     */
    public double getStatMultiplier() {
        return statMultiplier;
    }

    /**
     * 최대 레벨 반환
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * 경험치 보너스 반환 (%)
     */
    public double getExpBonus() {
        return expBonus;
    }

    /**
     * 경험치 배율 반환 (1.0 + bonus/100)
     */
    public double getExpMultiplier() {
        return 1.0 + (expBonus / 100.0);
    }

    /**
     * 스킬 슬롯 수 반환
     */
    public int getSkillSlots() {
        switch (this) {
            case COMMON: 
                return 2;
            case UNCOMMON:
                return 3;
            case RARE: 
                return 4;
            case EPIC:
                return 5;
            case LEGENDARY: 
                return 6;
            case MYTHIC:
                return 8;
            default: 
                return 2;
        }
    }

    /**
     * 진화 가능 횟수 반환
     */
    public int getMaxEvolutions() {
        switch (this) {
            case COMMON:
                return 1;
            case UNCOMMON: 
                return 2;
            case RARE:
                return 2;
            case EPIC:
                return 3;
            case LEGENDARY: 
                return 3;
            case MYTHIC:
                return 4;
            default:
                return 1;
        }
    }

    /**
     * 장비 슬롯 수 반환
     */
    public int getEquipmentSlots() {
        switch (this) {
            case COMMON:
                return 1;
            case UNCOMMON:
                return 2;
            case RARE: 
                return 3;
            case EPIC:
                return 4;
            case LEGENDARY: 
                return 4;
            case MYTHIC:
                return 4;
            default: 
                return 1;
        }
    }

    /**
     * 드롭 확률 반환 (%)
     */
    public double getDropChance() {
        switch (this) {
            case COMMON:
                return 50.0;
            case UNCOMMON: 
                return 25.0;
            case RARE: 
                return 15.0;
            case EPIC:
                return 7.0;
            case LEGENDARY:
                return 2.5;
            case MYTHIC:
                return 0.5;
            default:
                return 50.0;
        }
    }

    /**
     * 교배 시 상위 등급 확률 반환 (%)
     */
    public double getBreedingUpgradeChance() {
        switch (this) {
            case COMMON:
                return 10.0;
            case UNCOMMON: 
                return 8.0;
            case RARE: 
                return 5.0;
            case EPIC:
                return 3.0;
            case LEGENDARY:
                return 1.0;
            case MYTHIC:
                return 0.0; // 최고 등급
            default:
                return 0.0;
        }
    }

    /**
     * 다음 등급 반환
     */
    public PetRarity getNextRarity() {
        int nextOrdinal = this.ordinal() + 1;
        if (nextOrdinal >= values().length) {
            return this; // 최고 등급이면 자신 반환
        }
        return values()[nextOrdinal];
    }

    /**
     * 이전 등급 반환
     */
    public PetRarity getPreviousRarity() {
        int prevOrdinal = this.ordinal() - 1;
        if (prevOrdinal < 0) {
            return this; // 최저 등급이면 자신 반환
        }
        return values()[prevOrdinal];
    }

    /**
     * 최고 등급인지 확인
     */
    public boolean isMaxRarity() {
        return this == MYTHIC;
    }

    /**
     * 최저 등급인지 확인
     */
    public boolean isMinRarity() {
        return this == COMMON;
    }

    /**
     * 특정 등급 이상인지 확인
     */
    public boolean isAtLeast(PetRarity other) {
        return this.ordinal() >= other.ordinal();
    }

    /**
     * 특정 등급 이하인지 확인
     */
    public boolean isAtMost(PetRarity other) {
        return this.ordinal() <= other.ordinal();
    }

    /**
     * 별 개수 반환 (UI 표시용)
     */
    public int getStarCount() {
        return this.ordinal() + 1;
    }

    /**
     * 별 문자열 반환 (UI 표시용)
     */
    public String getStars() {
        StringBuilder stars = new StringBuilder();
        int count = getStarCount();
        for (int i = 0; i < count; i++) {
            stars. append("★");
        }
        for (int i = count; i < 6; i++) {
            stars.append("☆");
        }
        return minecraftColor + stars. toString();
    }

    /**
     * 문자열로 PetRarity 찾기
     *
     * @param name 이름
     * @return PetRarity 또는 null
     */
    public static PetRarity fromString(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        String upperName = name.toUpperCase().trim();

        // 영어 이름으로 찾기
        try {
            return PetRarity.valueOf(upperName);
        } catch (IllegalArgumentException ignored) {
        }

        // 한글 이름으로 찾기
        for (PetRarity rarity : values()) {
            if (rarity.getDisplayName().equals(name)) {
                return rarity;
            }
        }

        return null;
    }

    /**
     * 확률 기반 랜덤 희귀도 반환
     *
     * @return 랜덤 PetRarity
     */
    public static PetRarity getRandomByChance() {
        double random = Math.random() * 100;
        double cumulative = 0;

        // 역순으로 체크 (높은 등급부터)
        PetRarity[] rarities = values();
        for (int i = rarities.length - 1; i >= 0; i--) {
            cumulative += rarities[i].getDropChance();
            if (random <= cumulative) {
                return rarities[i];
            }
        }

        return COMMON;
    }

    /**
     * 기본 희귀도 반환
     */
    public static PetRarity getDefault() {
        return COMMON;
    }

    /**
     * 모든 희귀도의 표시 이름 목록 반환
     */
    public static String[] getDisplayNames() {
        PetRarity[] rarities = values();
        String[] names = new String[rarities.length];
        for (int i = 0; i < rarities.length; i++) {
            names[i] = rarities[i]. getDisplayName();
        }
        return names;
    }
}