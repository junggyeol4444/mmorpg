package com.multiverse.skill.data.enums;

/**
 * 투사체 타입
 */
public enum ProjectileType {
    ARROW("화살", "ARROW", "CRIT"),
    FIREBALL("파이어볼", "FIREBALL", "FLAME"),
    SNOWBALL("스노우볼", "SNOWBALL", "SNOWBALL"),
    WITHER_SKULL("위더 스컬", "WITHER_SKULL", "SMOKE"),
    TRIDENT("삼지창", "TRIDENT", "WATER_SPLASH"),
    SPECTRAL_ARROW("분광 화살", "ARROW", "ELECTRIC_SPARK"),
    CUSTOM_PROJECTILE("커스텀 투사체", "ARROW", "CRIT");

    private final String displayName;
    private final String bukkitType;
    private final String particleName;

    ProjectileType(String displayName, String bukkitType, String particleName) {
        this.displayName = displayName;
        this.bukkitType = bukkitType;
        this.particleName = particleName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBukkitType() {
        return bukkitType;
    }

    public String getParticleName() {
        return particleName;
    }

    /**
     * 투사체 속도 (기본값)
     */
    public double getDefaultSpeed() {
        return switch (this) {
            case ARROW -> 2.0;
            case FIREBALL -> 1.5;
            case SNOWBALL -> 1.5;
            case WITHER_SKULL -> 1.2;
            case TRIDENT -> 2.5;
            case SPECTRAL_ARROW -> 2.2;
            default -> 1.5;
        };
    }

    /**
     * 투사체 크기
     */
    public double getSize() {
        return switch (this) {
            case FIREBALL -> 1.5;
            case WITHER_SKULL -> 1. 2;
            case TRIDENT -> 0.8;
            default -> 0.5;
        };
    }

    /**
     * 자동 추적 가능 여부
     */
    public boolean canHome() {
        return this == FIREBALL || this == WITHER_SKULL;
    }

    /**
     * 관통 가능 여부
     */
    public boolean canPierce() {
        return this == ARROW || this == SPECTRAL_ARROW || this == TRIDENT;
    }
}