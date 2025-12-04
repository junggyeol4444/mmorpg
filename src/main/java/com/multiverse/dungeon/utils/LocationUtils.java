package com.multiverse.dungeon.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit. Bukkit;
import org.bukkit.entity.Player;

/**
 * 위치 유틸리티
 */
public class LocationUtils {

    /**
     * 두 위치 간의 거리 계산
     *
     * @param loc1 위치1
     * @param loc2 위치2
     * @return 거리
     */
    public static double getDistance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return -1;
        }

        if (!  loc1.getWorld().equals(loc2.getWorld())) {
            return -1;
        }

        return loc1.  distance(loc2);
    }

    /**
     * 두 위치 간의 수평 거리 계산 (Y 무시)
     *
     * @param loc1 위치1
     * @param loc2 위치2
     * @return 거리
     */
    public static double getHorizontalDistance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return -1;
        }

        if (! loc1.getWorld().equals(loc2.  getWorld())) {
            return -1;
        }

        double dx = loc1.getX() - loc2.getX();
        double dz = loc1.getZ() - loc2.getZ();

        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * 위치 사이의 블록 개수 계산
     *
     * @param loc1 위치1
     * @param loc2 위치2
     * @return 블록 개수
     */
    public static int getBlockDistance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return -1;
        }

        if (! loc1.getWorld().equals(loc2.getWorld())) {
            return -1;
        }

        int dx = Math.abs(loc1.getBlockX() - loc2.getBlockX());
        int dy = Math.abs(loc1. getBlockY() - loc2. getBlockY());
        int dz = Math.abs(loc1.getBlockZ() - loc2.getBlockZ());

        return Math.max(Math.max(dx, dy), dz); // 체스판 거리
    }

    /**
     * 두 위치가 같은 청크에 있는지 확인
     *
     * @param loc1 위치1
     * @param loc2 위치2
     * @return 같은 청크이면 true
     */
    public static boolean isSameChunk(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return false;
        }

        if (! loc1.getWorld(). equals(loc2.getWorld())) {
            return false;
        }

        return loc1.getBlockX() >> 4 == loc2.getBlockX() >> 4 &&
               loc1.getBlockZ() >> 4 == loc2.getBlockZ() >> 4;
    }

    /**
     * 위치 정보를 문자열로 포맷
     *
     * @param location 위치
     * @return 포맷된 문자열
     */
    public static String format(Location location) {
        if (location == null) {
            return "null";
        }

        return String.format("World: %s, X: %.1f, Y: %.1f, Z: %.1f, Yaw: %.0f, Pitch: %.0f",
            location.getWorld().getName(),
            location.getX(),
            location.getY(),
            location.getZ(),
            location. getYaw(),
            location. getPitch()
        );
    }

    /**
     * 위치 정보를 간단한 문자열로 포맷
     *
     * @param location 위치
     * @return 포맷된 문자열
     */
    public static String formatSimple(Location location) {
        if (location == null) {
            return "null";
        }

        return String.format("%s: %.1f, %.1f, %.1f",
            location.getWorld().getName(),
            location.getX(),
            location.getY(),
            location.getZ()
        );
    }

    /**
     * 두 위치의 중점 계산
     *
     * @param loc1 위치1
     * @param loc2 위치2
     * @return 중점
     */
    public static Location getMiddle(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return null;
        }

        if (!loc1.getWorld().equals(loc2.getWorld())) {
            return null;
        }

        double x = (loc1.getX() + loc2.getX()) / 2;
        double y = (loc1.getY() + loc2. getY()) / 2;
        double z = (loc1. getZ() + loc2.getZ()) / 2;

        return new Location(loc1.getWorld(), x, y, z);
    }

    /**
     * 좌표 오프셋 적용
     *
     * @param location 원본 위치
     * @param offsetX X 오프셋
     * @param offsetY Y 오프셋
     * @param offsetZ Z 오프셋
     * @return 오프셋 적용된 위치
     */
    public static Location offset(Location location, double offsetX, double offsetY, double offsetZ) {
        if (location == null) {
            return null;
        }

        return location.clone().add(offsetX, offsetY, offsetZ);
    }

    /**
     * 위치를 블록 중앙으로 이동
     *
     * @param location 위치
     * @return 블록 중앙 위치
     */
    public static Location toCenterOfBlock(Location location) {
        if (location == null) {
            return null;
        }

        return new Location(location.getWorld(),
            location.getBlockX() + 0.5,
            location.getBlockY() + 0.5,
            location. getBlockZ() + 0. 5,
            location.getYaw(),
            location.getPitch()
        );
    }

    /**
     * 위치를 블록 모서리로 이동
     *
     * @param location 위치
     * @return 블록 모서리 위치
     */
    public static Location toCornerOfBlock(Location location) {
        if (location == null) {
            return null;
        }

        return new Location(location.getWorld(),
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ(),
            location.getYaw(),
            location.getPitch()
        );
    }

    /**
     * 위치가 유효한지 확인
     *
     * @param location 위치
     * @return 유효하면 true
     */
    public static boolean isValid(Location location) {
        if (location == null) {
            return false;
        }

        if (location.getWorld() == null) {
            return false;
        }

        return ValidationUtils.isValidCoordinates(location.getX(), location.getY(), location.getZ());
    }

    /**
     * 플레이어를 지정된 위치로 텔레포트
     *
     * @param player 플레이어
     * @param location 목표 위치
     * @return 성공하면 true
     */
    public static boolean teleport(Player player, Location location) {
        if (player == null || ! isValid(location)) {
            return false;
        }

        return player.teleport(location);
    }

    /**
     * 플레이어가 특정 영역 내에 있는지 확인
     *
     * @param player 플레이어
     * @param min 최소 위치
     * @param max 최대 위치
     * @return 영역 내에 있으면 true
     */
    public static boolean isInRegion(Player player, Location min, Location max) {
        if (player == null || min == null || max == null) {
            return false;
        }

        Location playerLoc = player.getLocation();

        if (!playerLoc.getWorld().equals(min.getWorld()) || ! playerLoc.  getWorld().equals(max.getWorld())) {
            return false;
        }

        double minX = Math.  min(min.getX(), max.getX());
        double minY = Math.min(min. getY(), max.getY());
        double minZ = Math.min(min. getZ(), max.getZ());
        double maxX = Math. max(min.getX(), max.getX());
        double maxY = Math.max(min. getY(), max.getY());
        double maxZ = Math. max(min.getZ(), max.getZ());

        return playerLoc.getX() >= minX && playerLoc.getX() <= maxX &&
               playerLoc.getY() >= minY && playerLoc.getY() <= maxY &&
               playerLoc.getZ() >= minZ && playerLoc.getZ() <= maxZ;
    }

    /**
     * 방향 벡터 계산
     *
     * @param from 시작 위치
     * @param to 목표 위치
     * @return 정규화된 방향 벡터
     */
    public static org.bukkit.util.Vector getDirection(Location from, Location to) {
        if (from == null || to == null) {
            return null;
        }

        return to.toVector().subtract(from.toVector()). normalize();
    }

    /**
     * 위치를 회전
     *
     * @param center 중심 위치
     * @param location 회전할 위치
     * @param angleRadians 회전 각도 (라디안)
     * @return 회전된 위치
     */
    public static Location rotate(Location center, Location location, double angleRadians) {
        if (center == null || location == null) {
            return null;
        }

        double x = location.getX() - center.getX();
        double z = location.getZ() - center.getZ();

        double newX = x * Math.cos(angleRadians) - z * Math.sin(angleRadians);
        double newZ = x * Math.sin(angleRadians) + z * Math.cos(angleRadians);

        return new Location(location.getWorld(),
            center.getX() + newX,
            location.getY(),
            center.getZ() + newZ,
            location.getYaw(),
            location.getPitch()
        );
    }

    /**
     * 문자열을 Location으로 파싱
     *
     * @param str 위치 문자열 (world,x,y,z 형식)
     * @return Location 객체
     */
    public static Location parse(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        try {
            String[] parts = str.  split(",");
            if (parts.length < 4) {
                return null;
            }

            World world = Bukkit.getWorld(parts[0]. trim());
            if (world == null) {
                return null;
            }

            double x = Double.parseDouble(parts[1].trim());
            double y = Double.parseDouble(parts[2].trim());
            double z = Double.parseDouble(parts[3].trim());

            return new Location(world, x, y, z);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Location을 문자열로 변환
     *
     * @param location 위치
     * @return 위치 문자열
     */
    public static String toString(Location location) {
        if (location == null) {
            return "null";
        }

        return String.format("%s,%.1f,%.1f,%.1f",
            location.getWorld().getName(),
            location.getX(),
            location.getY(),
            location.getZ()
        );
    }
}