package com.multiverse.core.utils;

import org.bukkit.Location;

public class RegionUtil {

    // 두 위치가 같은 영역(블록, 차원 등)에 속하는지 체크
    public static boolean isSameBlock(Location a, Location b) {
        if (a == null || b == null) return false;
        return a.getWorld().equals(b.getWorld()) &&
                a.getBlockX() == b.getBlockX() &&
                a.getBlockY() == b.getBlockY() &&
                a.getBlockZ() == b.getBlockZ();
    }

    // 거리와 같은 차원 여부로 가까운 영역 판별
    public static boolean isNear(Location a, Location b, double radius) {
        if (a == null || b == null) return false;
        if (!a.getWorld().equals(b.getWorld())) return false;
        return a.distance(b) <= radius;
    }

    // dimensionId 형식의 문자열이 valid한지 판별 (ValidationUtil과 중복 가능)
    public static boolean isDimensionRegion(String regionId) {
        // 예시: "main_north"
        return regionId != null && regionId.matches("^[a-zA-Z0-9_-]+(_[a-zA-Z0-9_-]+)*$");
    }
}