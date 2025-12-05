package com.multiverse.skill.data.models;

import com.multiverse.skill.data. enums.ProjectileType;

public class ProjectileConfig {

    private ProjectileType type;
    private double speed;
    private boolean homing;
    private boolean piercing;
    private int maxPierceTargets;
    private ParticleEffect particle;

    public ProjectileConfig() {
    }

    // Getters and Setters

    public ProjectileType getType() {
        return type;
    }

    public void setType(ProjectileType type) {
        this.type = type;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isHoming() {
        return homing;
    }

    public void setHoming(boolean homing) {
        this.homing = homing;
    }

    public boolean isPiercing() {
        return piercing;
    }

    public void setPiercing(boolean piercing) {
        this.piercing = piercing;
    }

    public int getMaxPierceTargets() {
        return maxPierceTargets;
    }

    public void setMaxPierceTargets(int maxPierceTargets) {
        this.maxPierceTargets = maxPierceTargets;
    }

    public ParticleEffect getParticle() {
        return particle;
    }

    public void setParticle(ParticleEffect particle) {
        this.particle = particle;
    }

    /**
     * 투사체 속도 계산 (블록/틱)
     */
    public double getVelocityMagnitude() {
        return speed / 10.0;
    }

    /**
     * 투사체 정보 문자열
     */
    public String getInfoString() {
        return String. format("Type: %s | Speed: %.2f | Homing: %s | Piercing: %s | MaxTargets: %d",
                type.name(),
                speed,
                homing ?  "예" : "아니오",
                piercing ? "예" : "아니오",
                maxPierceTargets);
    }

    @Override
    public String toString() {
        return "ProjectileConfig{" +
                "type=" + type +
                ", speed=" + speed +
                ", homing=" + homing +
                ", piercing=" + piercing +
                '}';
    }
}