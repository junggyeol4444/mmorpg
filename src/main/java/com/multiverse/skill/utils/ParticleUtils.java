package com.multiverse.skill.utils;

import org.bukkit.Location;
import org.bukkit. Particle;
import org.bukkit.World;
import org.bukkit. entity.Player;

/**
 * 파티클 유틸리티
 */
public class ParticleUtils {

    /**
     * 파티클 생성
     */
    public static void playParticle(Location location, Particle particle, int count) {
        if (location == null || particle == null) {
            return;
        }

        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.spawnParticle(particle, location, count);
    }

    /**
     * 파티클 생성 (속도 포함)
     */
    public static void playParticleWithVelocity(Location location, Particle particle, int count, 
                                               double offsetX, double offsetY, double offsetZ, double speed) {
        if (location == null || particle == null) {
            return;
        }

        World world = location.getWorld();
        if (world == null) {
            return;
        }

        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
    }

    /**
     * 원형 파티클 생성
     */
    public static void playCircleParticles(Location center, Particle particle, double radius, int count) {
        if (center == null || particle == null) {
            return;
        }

        double angleStep = 360.0 / count;

        for (int i = 0; i < count; i++) {
            double angle = Math.toRadians(angleStep * i);
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);

            Location particleLocation = new Location(center. getWorld(), x, center.getY(), z);
            playParticle(particleLocation, particle, 1);
        }
    }

    /**
     * 구형 파티클 생성
     */
    public static void playSphereParticles(Location center, Particle particle, double radius, int count) {
        if (center == null || particle == null) {
            return;
        }

        for (int i = 0; i < count; i++) {
            double phi = Math.acos(2.0 * Math.random() - 1.0);
            double theta = 2. 0 * Math.PI * Math.random();

            double x = center.getX() + radius * Math.sin(phi) * Math.cos(theta);
            double y = center. getY() + radius * Math. sin(phi) * Math.sin(theta);
            double z = center.getZ() + radius * Math.cos(phi);

            Location particleLocation = new Location(center.getWorld(), x, y, z);
            playParticle(particleLocation, particle, 1);
        }
    }

    /**
     * 선형 파티클 생성
     */
    public static void playLineParticles(Location start, Location end, Particle particle, double interval) {
        if (start == null || end == null || particle == null) {
            return;
        }

        double distance = start.distance(end);
        int stepCount = (int) (distance / interval);

        for (int i = 0; i <= stepCount; i++) {
            double t = (double) i / stepCount;
            Location particleLocation = start.clone().add(
                    (end.getX() - start.getX()) * t,
                    (end.getY() - start.getY()) * t,
                    (end.getZ() - start.getZ()) * t
            );

            playParticle(particleLocation, particle, 1);
        }
    }
}