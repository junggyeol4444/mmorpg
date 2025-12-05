package com. multiverse.skill.utils;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org. bukkit.util.Vector;

/**
 * 위치 유틸리티
 */
public class LocationUtils {

    /**
     * 두 위치 사이의 거리 계산
     */
    public static double getDistance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return 0.0;
        }

        if (! loc1.getWorld().equals(loc2.getWorld())) {
            return Double.MAX_VALUE;
        }

        return loc1.distance(loc2);
    }

    /**
     * 두 위치 사이의 방향 벡터
     */
    public static Vector getDirection(Location from, Location to) {
        if (from == null || to == null) {
            return new Vector(0, 0, 0);
        }

        return to.clone().subtract(from). toVector(). normalize();
    }

    /**
     * 위치에서 거리와 각도만큼 떨어진 위치 계산
     */
    public static Location getLocationAtDistance(Location origin, double distance, float yaw, float pitch) {
        if (origin == null) {
            return null;
        }

        Vector direction = getDirectionFromRotation(yaw, pitch);
        return origin.clone().add(direction. multiply(distance));
    }

    /**
     * Yaw, Pitch로부터 방향 벡터 계산
     */
    public static Vector getDirectionFromRotation(float yaw, float pitch) {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);

        return new Vector(x, y, z);
    }

    /**
     * 엔티티 사이의 높이 차이
     */
    public static double getHeightDifference(LivingEntity entity1, LivingEntity entity2) {
        if (entity1 == null || entity2 == null) {
            return 0.0;
        }

        return entity1.getLocation().getY() - entity2. getLocation().getY();
    }

    /**
     * 위치가 지정된 범위 내에 있는지 확인
     */
    public static boolean isLocationInRange(Location location, Location center, double radius) {
        if (location == null || center == null) {
            return false;
        }

        if (!location.getWorld().equals(center.getWorld())) {
            return false;
        }

        return location.distance(center) <= radius;
    }

    /**
     * 위치 복제
     */
    public static Location cloneLocation(Location location) {
        if (location == null) {
            return null;
        }

        return location.clone();
    }

    /**
     * 위치 반올림 (블록 중앙으로)
     */
    public static Location roundLocation(Location location) {
        if (location == null) {
            return null;
        }

        Location rounded = location.clone();
        rounded.setX(Math.floor(location.getX()) + 0.5);
        rounded.setY(Math.floor(location.getY()));
        rounded.setZ(Math.floor(location.getZ()) + 0.5);

        return rounded;
    }
}