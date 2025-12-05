package com.multiverse.skill.data.enums;

/**
 * 데미지 타입
 */
public enum DamageType {
    PHYSICAL("물리", "§f", 1. 0),
    FIRE("화염", "§c", 1.2),
    FROST("냉기", "§b", 1.1),
    LIGHTNING("번개", "§e", 1.15),
    HOLY("신성", "§a", 1. 1),
    SHADOW("암흑", "§8", 1.15),
    NATURE("자연", "§2", 1.0),
    MAGIC("마법", "§d", 1.1);

    private final String displayName;
    private final String colorCode;
    private final double multiplier;

    DamageType(String displayName, String colorCode, double multiplier) {
        this.displayName = displayName;
        this.colorCode = colorCode;
        this.multiplier = multiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public double getMultiplier() {
        return multiplier;
    }

    /**
     * 색상이 적용된 이름
     */
    public String getColoredName() {
        return colorCode + "§l" + displayName;
    }

    /**
     * 데미지 타입별 저항력 계산
     */
    public double getResistanceMultiplier(DamageType targetResistance) {
        if (targetResistance == this) {
            return 0.75;
        }
        return 1.0;
    }

    /**
     * 약점 계산
     */
    public double getWeaknessMultiplier(DamageType targetWeakness) {
        // 화염 vs 냉기 = 약점
        if (this == FIRE && targetWeakness == FROST) return 1.5;
        if (this == FROST && targetWeakness == FIRE) return 1.5;
        if (this == LIGHTNING && targetWeakness == NATURE) return 1.5;
        if (this == NATURE && targetWeakness == LIGHTNING) return 1.5;
        
        return 1.0;
    }

    /**
     * 파티클 이름
     */
    public String getParticleName() {
        return switch (this) {
            case FIRE -> "FLAME";
            case FROST -> "SNOWBALL";
            case LIGHTNING -> "ELECTRIC_SPARK";
            case HOLY -> "HAPPY_VILLAGER";
            case SHADOW -> "SMOKE";
            case NATURE -> "VILLAGER_HAPPY";
            default -> "CRIT";
        };
    }
}