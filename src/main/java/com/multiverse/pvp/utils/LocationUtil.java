package com.multiverse. pvp.utils;

import org.bukkit. Bukkit;
import org.bukkit. Location;
import org.bukkit.World;
import org. bukkit.block. Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.Random;

public class LocationUtil {

    private static final Random random = new Random();

    /**
     * Location을 문자열로 변환
     */
    public static String locationToString(Location location) {
        if (location == null || location.getWorld() == null) {
            return null;
        }

        return location.getWorld().getName() + "," +
               location.getX() + "," +
               location.getY() + "," +
               location. getZ() + "," +
               location.getYaw() + "," +
               location.getPitch();
    }

    /**
     * 문자열을 Location으로 변환
     */
    public static Location stringToLocation(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        String[] parts = str.split(",");
        if (parts.length < 4) {
            return null;
        }

        try {
            World world = Bukkit.getWorld(parts[0]);
            if (world == null) {
                return null;
            }

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            float yaw = parts. length > 4 ? Float.parseFloat(parts[4]) : 0;
            float pitch = parts.length > 5 ? Float. parseFloat(parts[5]) : 0;

            return new Location(world, x, y, z, yaw, pitch);

        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 두 위치 사이의 거리 계산
     */
    public static double getDistance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return -1;
        }

        if (! loc1.getWorld().equals(loc2.getWorld())) {
            return -1;
        }

        return loc1.distance(loc2);
    }

    /**
     * 두 위치 사이의 2D 거리 계산 (Y축 무시)
     */
    public static double getDistance2D(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return -1;
        }

        if (!loc1.getWorld().equals(loc2.getWorld())) {
            return -1;
        }

        double dx = loc1.getX() - loc2.getX();
        double dz = loc1.getZ() - loc2.getZ();

        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * 위치가 영역 내에 있는지 확인
     */
    public static boolean isInArea(Location location, Location corner1, Location corner2) {
        if (location == null || corner1 == null || corner2 == null) {
            return false;
        }

        if (! location.getWorld().equals(corner1.getWorld())) {
            return false;
        }

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math. max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return location.getX() >= minX && location.getX() <= maxX &&
               location.getY() >= minY && location.getY() <= maxY &&
               location.getZ() >= minZ && location.getZ() <= maxZ;
    }

    /**
     * 영역의 중심점 계산
     */
    public static Location getCenter(Location corner1, Location corner2) {
        if (corner1 == null || corner2 == null) {
            return null;
        }

        if (!corner1.getWorld().equals(corner2.getWorld())) {
            return null;
        }

        double x = (corner1.getX() + corner2.getX()) / 2;
        double y = (corner1.getY() + corner2.getY()) / 2;
        double z = (corner1.getZ() + corner2.getZ()) / 2;

        return new Location(corner1.getWorld(), x, y, z);
    }

    /**
     * 영역 내 랜덤 위치 생성
     */
    public static Location getRandomLocation(Location corner1, Location corner2) {
        if (corner1 == null || corner2 == null) {
            return null;
        }

        if (!corner1.getWorld().equals(corner2.getWorld())) {
            return null;
        }

        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math. max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        double x = minX + random.nextDouble() * (maxX - minX);
        double y = minY + random. nextDouble() * (maxY - minY);
        double z = minZ + random.nextDouble() * (maxZ - minZ);

        return new Location(corner1.getWorld(), x, y, z);
    }

    /**
     * 안전한 스폰 위치 찾기
     */
    public static Location getSafeLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return null;
        }

        Location safe = location.clone();
        World world = safe.getWorld();

        // 위로 올라가면서 안전한 위치 찾기
        for (int y = (int) safe.getY(); y < world.getMaxHeight() - 1; y++) {
            safe.setY(y);

            Block block = safe.getBlock();
            Block above = block.getRelative(BlockFace.UP);
            Block below = block.getRelative(BlockFace.DOWN);

            if (isSafeBlock(block) && isSafeBlock(above) && below. getType().isSolid()) {
                return safe;
            }
        }

        // 아래로 내려가면서 찾기
        for (int y = (int) location.getY(); y > 0; y--) {
            safe.setY(y);

            Block block = safe.getBlock();
            Block above = block.getRelative(BlockFace.UP);
            Block below = block. getRelative(BlockFace.DOWN);

            if (isSafeBlock(block) && isSafeBlock(above) && below.getType().isSolid()) {
                return safe;
            }
        }

        return null;
    }

    /**
     * 블록이 안전한지 확인
     */
    private static boolean isSafeBlock(Block block) {
        if (block == null) {
            return false;
        }

        switch (block.getType()) {
            case AIR:
            case CAVE_AIR:
            case VOID_AIR:
                return true;
            default:
                return false;
        }
    }

    /**
     * 지상 위치 찾기
     */
    public static Location getGroundLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return null;
        }

        Location ground = location.clone();
        World world = ground.getWorld();

        // 아래로 내려가면서 땅 찾기
        for (int y = (int) ground.getY(); y > 0; y--) {
            ground.setY(y);

            Block block = ground. getBlock();
            if (block. getType().isSolid()) {
                ground. setY(y + 1);
                return ground;
            }
        }

        return null;
    }

    /**
     * 플레이어를 향하는 방향 계산
     */
    public static Location lookAt(Location from, Location to) {
        if (from == null || to == null) {
            return from;
        }

        Location result = from.clone();

        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to. getZ() - from.getZ();

        double distance = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(Math.atan2(-dy, distance));

        result.setYaw(yaw);
        result.setPitch(pitch);

        return result;
    }

    /**
     * 플레이어 앞 위치 계산
     */
    public static Location getLocationInFront(Player player, double distance) {
        if (player == null) {
            return null;
        }

        Location location = player.getLocation().clone();
        double yaw = Math.toRadians(location.getYaw());

        double x = -Math.sin(yaw) * distance;
        double z = Math.cos(yaw) * distance;

        return location.add(x, 0, z);
    }

    /**
     * 위치 비교 (블록 단위)
     */
    public static boolean isSameBlock(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return false;
        }

        if (!loc1.getWorld().equals(loc2.getWorld())) {
            return false;
        }

        return loc1.getBlockX() == loc2.getBlockX() &&
               loc1.getBlockY() == loc2.getBlockY() &&
               loc1.getBlockZ() == loc2.getBlockZ();
    }

    /**
     * 영역 크기 계산
     */
    public static long getAreaVolume(Location corner1, Location corner2) {
        if (corner1 == null || corner2 == null) {
            return 0;
        }

        long width = Math.abs(corner1.getBlockX() - corner2.getBlockX()) + 1;
        long height = Math. abs(corner1.getBlockY() - corner2.getBlockY()) + 1;
        long depth = Math.abs(corner1.getBlockZ() - corner2.getBlockZ()) + 1;

        return width * height * depth;
    }

    /**
     * 위치 포맷팅
     */
    public static String formatLocation(Location location) {
        if (location == null) {
            return "없음";
        }

        return String.format("%s, %. 1f, %.1f, %.1f",
                location.getWorld() != null ? location.getWorld().getName() : "unknown",
                location. getX(),
                location.getY(),
                location.getZ());
    }

    /**
     * 간단한 위치 포맷팅 (블록 좌표)
     */
    public static String formatLocationSimple(Location location) {
        if (location == null) {
            return "없음";
        }

        return String.format("%d, %d, %d",
                location.getBlockX(),
                location.getBlockY(),
                location. getBlockZ());
    }
}