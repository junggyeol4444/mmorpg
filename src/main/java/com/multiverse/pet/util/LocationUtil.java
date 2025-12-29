package com.multiverse.pet.        util;

import org.  bukkit.        Bukkit;
import org. bukkit.         Location;
import org.  bukkit.        Material;
import org.  bukkit.        World;
import org. bukkit.        block.Block;
import org.bukkit.         block.BlockFace;
import org.bukkit.        entity.Entity;
import org. bukkit.        entity.Player;
import org.bukkit.         util.Vector;

import java.util.          ArrayList;
import java.  util.         List;
import java.  util.         Random;

/**
 * 위치 유틸리티
 * 위치 관련 계산 및 유틸리티
 */
public class LocationUtil {

    private static final Random random = new Random();

    /**
     * 안전한 스폰 위치 찾기
     */
    public static Location findSafeLocation(Location center) {
        return findSafeLocation(center, 3);
    }

    /**
     * 안전한 스폰 위치 찾기 (반경 지정)
     */
    public static Location findSafeLocation(Location center, int radius) {
        World world = center.getWorld();
        if (world == null) return center;

        // 주변 위치 시도
        for (int attempt = 0; attempt < 10; attempt++) {
            double offsetX = (random.nextDouble() - 0.5) * 2 * radius;
            double offsetZ = (random.nextDouble() - 0.5) * 2 * radius;

            Location test = center.clone().add(offsetX, 0, offsetZ);

            // 지면 높이 찾기
            test.setY(world.getHighestBlockYAt(test) + 1);

            if (isSafeLocation(test)) {
                return test;
            }
        }

        // 안전한 위치를 못 찾으면 원래 위치 반환
        Location fallback = center.clone();
        fallback.setY(world.getHighestBlockYAt(center) + 1);
        return fallback;
    }

    /**
     * 안전한 위치인지 확인
     */
    public static boolean isSafeLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        Block feet = location.getBlock();
        Block head = feet.getRelative(BlockFace.UP);
        Block ground = feet.getRelative(BlockFace.DOWN);

        // 발과 머리 위치가 비어있어야 함
        if (! isPassable(feet) || !isPassable(head)) {
            return false;
        }

        // 바닥이 고체여야 함
        if (! ground.getType().isSolid()) {
            return false;
        }

        // 위험한 블록 체크
        if (isDangerous(ground) || isDangerous(feet)) {
            return false;
        }

        return true;
    }

    /**
     * 통과 가능한 블록인지
     */
    public static boolean isPassable(Block block) {
        Material type = block.getType();
        return type.isAir() || 
               type == Material.WATER || 
               type == Material.GRASS ||
               type == Material.TALL_GRASS ||
               type == Material. SNOW ||
               ! type.isSolid();
    }

    /**
     * 위험한 블록인지
     */
    public static boolean isDangerous(Block block) {
        Material type = block.getType();
        return type == Material. LAVA ||
               type == Material.FIRE ||
               type == Material.MAGMA_BLOCK ||
               type == Material. CACTUS ||
               type == Material. SWEET_BERRY_BUSH ||
               type == Material. WITHER_ROSE ||
               type == Material.CAMPFIRE ||
               type == Material.SOUL_CAMPFIRE;
    }

    /**
     * 플레이어 뒤쪽 위치
     */
    public static Location getBehindLocation(Player player, double distance) {
        Location loc = player.getLocation().clone();
        Vector direction = loc.getDirection().normalize().multiply(-distance);
        loc.add(direction);
        loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 1);
        return loc;
    }

    /**
     * 플레이어 옆 위치
     */
    public static Location getSideLocation(Player player, double distance, boolean left) {
        Location loc = player.getLocation().clone();
        Vector direction = loc.getDirection().normalize();
        Vector side = new Vector(-direction.getZ(), 0, direction.getX()).normalize();

        if (left) {
            side. multiply(-distance);
        } else {
            side. multiply(distance);
        }

        loc.add(side);
        loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 1);
        return loc;
    }

    /**
     * 원형 위치 목록 생성
     */
    public static List<Location> getCircleLocations(Location center, double radius, int points) {
        List<Location> locations = new ArrayList<>();
        double angleIncrement = 2 * Math.PI / points;

        for (int i = 0; i < points; i++) {
            double angle = i * angleIncrement;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);

            Location loc = new Location(center.getWorld(), x, center.getY(), z);
            locations.add(loc);
        }

        return locations;
    }

    /**
     * 두 위치 사이 거리
     */
    public static double getDistance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) return Double.MAX_VALUE;
        if (! loc1.getWorld().equals(loc2.getWorld())) return Double.MAX_VALUE;
        return loc1.distance(loc2);
    }

    /**
     * 두 위치 사이 2D 거리 (Y 무시)
     */
    public static double getDistance2D(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) return Double.MAX_VALUE;
        if (! loc1.getWorld().equals(loc2.getWorld())) return Double.MAX_VALUE;

        double dx = loc1.getX() - loc2.getX();
        double dz = loc1.getZ() - loc2.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * 범위 내에 있는지 확인
     */
    public static boolean isWithinRange(Location loc1, Location loc2, double range) {
        return getDistance(loc1, loc2) <= range;
    }

    /**
     * 두 위치 사이의 방향 벡터
     */
    public static Vector getDirection(Location from, Location to) {
        return to.toVector().subtract(from.toVector()).normalize();
    }

    /**
     * 위치를 바라보도록 설정
     */
    public static Location lookAt(Location from, Location target) {
        Location result = from.clone();
        Vector direction = getDirection(from, target);
        result.setDirection(direction);
        return result;
    }

    /**
     * 랜덤 오프셋 적용
     */
    public static Location addRandomOffset(Location location, double maxOffset) {
        Location result = location.clone();
        result.add(
            (random.nextDouble() - 0.5) * 2 * maxOffset,
            0,
            (random. nextDouble() - 0.5) * 2 * maxOffset
        );
        return result;
    }

    /**
     * 중심점 계산
     */
    public static Location getCenter(Location...  locations) {
        if (locations == null || locations.length == 0) return null;

        double x = 0, y = 0, z = 0;
        World world = locations[0].getWorld();

        for (Location loc : locations) {
            x += loc.getX();
            y += loc.getY();
            z += loc.getZ();
        }

        int count = locations.length;
        return new Location(world, x / count, y / count, z / count);
    }

    /**
     * 시선 방향의 엔티티 찾기
     */
    public static Entity getTargetEntity(Player player, double range, Class<? extends Entity> entityClass) {
        List<Entity> nearby = player.getNearbyEntities(range, range, range);
        Location eyeLocation = player. getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();

        Entity closest = null;
        double closestAngle = Double.MAX_VALUE;

        for (Entity entity :  nearby) {
            if (! entityClass.isInstance(entity)) continue;
            if (entity. equals(player)) continue;

            Vector toEntity = entity.getLocation().toVector()
                    .subtract(eyeLocation.toVector()).normalize();

            double angle = direction.angle(toEntity);

            if (angle < Math.PI / 8 && angle < closestAngle) { // 약 22.5도 이내
                closestAngle = angle;
                closest = entity;
            }
        }

        return closest;
    }

    /**
     * 위치 직렬화 (문자열로)
     */
    public static String serializeLocation(Location location) {
        if (location == null || location.getWorld() == null) return null;

        return String.format("%s,%. 2f,%.2f,%.2f,%.2f,%.2f",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }

    /**
     * 위치 역직렬화 (문자열에서)
     */
    public static Location deserializeLocation(String serialized) {
        if (serialized == null || serialized.isEmpty()) return null;

        try {
            String[] parts = serialized. split(",");
            if (parts.length < 4) return null;

            World world = Bukkit.getWorld(parts[0]);
            if (world == null) return null;

            double x = Double.parseDouble(parts[1]);
            double y = Double. parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);

            float yaw = parts. length > 4 ? Float.parseFloat(parts[4]) : 0;
            float pitch = parts.length > 5 ? Float. parseFloat(parts[5]) : 0;

            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 청크 로드 확인
     */
    public static boolean isChunkLoaded(Location location) {
        if (location == null || location.getWorld() == null) return false;
        return location.getWorld().isChunkLoaded(
                location.getBlockX() >> 4,
                location.getBlockZ() >> 4
        );
    }

    /**
     * 가장 가까운 플레이어 찾기
     */
    public static Player getNearestPlayer(Location location, double range) {
        if (location == null || location.getWorld() == null) return null;

        Player nearest = null;
        double nearestDistance = range;

        for (Player player : location.getWorld().getPlayers()) {
            double distance = player.getLocation().distance(location);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = player;
            }
        }

        return nearest;
    }
}