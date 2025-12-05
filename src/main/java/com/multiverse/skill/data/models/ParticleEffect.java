package com.multiverse.skill.data.models;

import org.bukkit. Particle;
import org.bukkit.Color;

public class ParticleEffect {

    private Particle particleType;
    private int count;
    private double offsetX;
    private double offsetY;
    private double offsetZ;
    private double speed;
    private Color color;
    private String name;

    public ParticleEffect() {
    }

    public ParticleEffect(Particle particleType, int count) {
        this.particleType = particleType;
        this.count = count;
        this.offsetX = 0. 5;
        this.offsetY = 0.5;
        this. offsetZ = 0.5;
        this.speed = 1.0;
    }

    // Getters and Setters

    public Particle getParticleType() {
        return particleType;
    }

    public void setParticleType(Particle particleType) {
        this.particleType = particleType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this. offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getOffsetZ() {
        return offsetZ;
    }

    public void setOffsetZ(double offsetZ) {
        this.offsetZ = offsetZ;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 파티클 효과 복사
     */
    public ParticleEffect copy() {
        ParticleEffect copy = new ParticleEffect();
        copy.particleType = this.particleType;
        copy.count = this.count;
        copy.offsetX = this. offsetX;
        copy.offsetY = this.offsetY;
        copy.offsetZ = this. offsetZ;
        copy.speed = this.speed;
        copy.color = this.color;
        copy.name = this.name;
        return copy;
    }

    /**
     * 파티클 효과 정보 문자열
     */
    public String getInfoString() {
        return String. format("파티클: %s | 개수: %d | 속도: %.2f | 범위: (%.2f, %.2f, %.2f)",
                particleType.name(),
                count,
                speed,
                offsetX,
                offsetY,
                offsetZ);
    }

    @Override
    public String toString() {
        return "ParticleEffect{" +
                "name='" + name + '\'' +
                ", particleType=" + particleType +
                ", count=" + count +
                '}';
    }
}