package com.multiverse.party.utils;

import org.bukkit.Location;

/**
 * 위치/거리 관련 유틸리티
 */
public class LocationUtil {

    /** 위치 간 거리 계산 */
    public static double distance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null || !loc1.getWorld().equals(loc2.getWorld())) return -1;
        return loc1.distance(loc2);
    }

    /** 좌표 문자열 저장/파싱 예시 */
    public static String locationToString(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    // 추가적인 위치 관련 도구들 필요시 확장 가능
}