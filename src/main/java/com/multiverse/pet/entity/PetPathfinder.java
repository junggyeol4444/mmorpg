package com.multiverse. pet.entity;

import org.bukkit.Location;
import org. bukkit.Material;
import org. bukkit.World;
import org. bukkit.block.Block;
import org.bukkit. block.BlockFace;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * 펫 경로 탐색 클래스
 * A* 알고리즘 기반 경로 탐색
 */
public class PetPathfinder {

    // 경로 탐색 설정
    private static final int MAX_ITERATIONS = 500;
    private static final double NODE_DISTANCE = 1.0;
    private static final int MAX_PATH_LENGTH = 100;
    private static final double MAX_FALL_DISTANCE = 3.0;
    private static final double MAX_JUMP_HEIGHT = 1.5;

    // 이동 방향
    private static final int[][] DIRECTIONS = {
        {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1},  // 기본 4방향
        {1, 0, 1}, {1, 0, -1}, {-1, 0, 1}, {-1, 0, -1}  // 대각선 4방향
    };

    /**
     * 경로 탐색
     *
     * @param start 시작 위치
     * @param end 목표 위치
     * @return 경로 (위치 목록) 또는 null
     */
    public static List<Location> findPath(Location start, Location end) {
        if (start == null || end == null) return null;
        if (!start.getWorld().equals(end.getWorld())) return null;

        // 거리가 너무 멀면 직선 경로
        if (start. distance(end) > MAX_PATH_LENGTH) {
            return createDirectPath(start, end);
        }

        // A* 탐색
        return aStarSearch(start, end);
    }

    /**
     * A* 경로 탐색
     */
    private static List<Location> aStarSearch(Location start, Location end) {
        World world = start.getWorld();

        // 시작/끝 노드
        PathNode startNode = new PathNode(normalizeLocation(start));
        PathNode endNode = new PathNode(normalizeLocation(end));

        // 열린 목록 (우선순위 큐)
        PriorityQueue<PathNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        
        // 닫힌 목록
        Set<String> closedSet = new HashSet<>();

        // 노드 맵 (위치 -> 노드)
        Map<String, PathNode> nodeMap = new HashMap<>();

        startNode.gCost = 0;
        startNode.hCost = heuristic(startNode.location, endNode.location);
        startNode.fCost = startNode.gCost + startNode.hCost;

        openSet.add(startNode);
        nodeMap.put(locationKey(startNode.location), startNode);

        int iterations = 0;

        while (!openSet.isEmpty() && iterations < MAX_ITERATIONS) {
            iterations++;

            PathNode current = openSet.poll();
            String currentKey = locationKey(current.location);

            // 목표 도달
            if (isNear(current.location, endNode.location, 1.5)) {
                return reconstructPath(current, end);
            }

            closedSet.add(currentKey);

            // 이웃 노드 탐색
            for (int[] dir : DIRECTIONS) {
                Location neighborLoc = current.location.clone().add(dir[0], dir[1], dir[2]);
                
                // 유효한 위치인지 확인
                neighborLoc = findValidY(neighborLoc, world);
                if (neighborLoc == null) continue;

                String neighborKey = locationKey(neighborLoc);

                // 이미 처리됨
                if (closedSet.contains(neighborKey)) continue;

                // 이동 가능한지 확인
                if (! isWalkable(neighborLoc, current.location, world)) continue;

                double tentativeG = current.gCost + current.location.distance(neighborLoc);

                PathNode neighbor = nodeMap.get(neighborKey);
                if (neighbor == null) {
                    neighbor = new PathNode(neighborLoc);
                    nodeMap.put(neighborKey, neighbor);
                }

                if (tentativeG < neighbor.gCost) {
                    neighbor.parent = current;
                    neighbor.gCost = tentativeG;
                    neighbor.hCost = heuristic(neighborLoc, endNode.location);
                    neighbor.fCost = neighbor.gCost + neighbor. hCost;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // 경로를 찾지 못함 - 직선 경로 반환
        return createDirectPath(start, end);
    }

    /**
     * 휴리스틱 함수 (맨해튼 거리 + 유클리드 거리 혼합)
     */
    private static double heuristic(Location a, Location b) {
        double dx = Math.abs(a. getX() - b.getX());
        double dy = Math.abs(a. getY() - b.getY());
        double dz = Math.abs(a.getZ() - b.getZ());
        return (dx + dy + dz) + a.distance(b) * 0.5;
    }

    /**
     * 유효한 Y 좌표 찾기
     */
    private static Location findValidY(Location loc, World world) {
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        int startY = loc.getBlockY();

        // 위로 검색 (점프)
        for (int y = startY; y <= startY + (int) MAX_JUMP_HEIGHT; y++) {
            if (y < world.getMinHeight() || y >= world.getMaxHeight()) continue;
            
            Location testLoc = new Location(world, x + 0.5, y, z + 0.5);
            if (isStandable(testLoc, world)) {
                return testLoc;
            }
        }

        // 아래로 검색 (낙하)
        for (int y = startY - 1; y >= startY - (int) MAX_FALL_DISTANCE; y--) {
            if (y < world. getMinHeight() || y >= world.getMaxHeight()) continue;
            
            Location testLoc = new Location(world, x + 0.5, y, z + 0.5);
            if (isStandable(testLoc, world)) {
                return testLoc;
            }
        }

        return null;
    }

    /**
     * 서 있을 수 있는 위치인지 확인
     */
    private static boolean isStandable(Location loc, World world) {
        Block feetBlock = loc. getBlock();
        Block headBlock = feetBlock.getRelative(BlockFace.UP);
        Block groundBlock = feetBlock.getRelative(BlockFace.DOWN);

        // 발과 머리 위치가 통과 가능해야 함
        if (! isPassable(feetBlock) || !isPassable(headBlock)) {
            return false;
        }

        // 바닥이 단단해야 함
        return isSolid(groundBlock);
    }

    /**
     * 이동 가능한지 확인
     */
    private static boolean isWalkable(Location to, Location from, World world) {
        // 목표 위치가 서 있을 수 있어야 함
        if (!isStandable(to, world)) {
            return false;
        }

        // 높이 차이 확인
        double heightDiff = to.getY() - from.getY();
        if (heightDiff > MAX_JUMP_HEIGHT || heightDiff < -MAX_FALL_DISTANCE) {
            return false;
        }

        // 경로 상에 장애물 확인 (대각선 이동 시)
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();

        if (Math. abs(dx) > 0.5 && Math.abs(dz) > 0.5) {
            // 대각선 이동 - 양쪽 모두 통과 가능해야 함
            Location side1 = from.clone().add(dx, 0, 0);
            Location side2 = from.clone().add(0, 0, dz);
            
            if (! isPassable(side1.getBlock()) || !isPassable(side2.getBlock())) {
                return false;
            }
        }

        return true;
    }

    /**
     * 통과 가능한 블록인지 확인
     */
    private static boolean isPassable(Block block) {
        Material type = block.getType();
        return type. isAir() || 
               type == Material.WATER || 
               type == Material.GRASS ||
               type == Material.TALL_GRASS ||
               type == Material. FERN ||
               type == Material.LARGE_FERN ||
               ! type.isSolid();
    }

    /**
     * 단단한 블록인지 확인
     */
    private static boolean isSolid(Block block) {
        Material type = block.getType();
        return type.isSolid() && 
               type != Material.CACTUS &&
               type != Material. SWEET_BERRY_BUSH &&
               type != Material. FIRE &&
               type != Material.SOUL_FIRE &&
               type != Material. LAVA;
    }

    /**
     * 위치 정규화
     */
    private static Location normalizeLocation(Location loc) {
        return new Location(
            loc.getWorld(),
            loc.getBlockX() + 0.5,
            loc.getBlockY(),
            loc.getBlockZ() + 0.5
        );
    }

    /**
     * 위치 키 생성
     */
    private static String locationKey(Location loc) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    /**
     * 근접 여부 확인
     */
    private static boolean isNear(Location a, Location b, double distance) {
        return a.distanceSquared(b) <= distance * distance;
    }

    /**
     * 경로 재구성
     */
    private static List<Location> reconstructPath(PathNode endNode, Location finalTarget) {
        List<Location> path = new ArrayList<>();
        
        PathNode current = endNode;
        while (current != null) {
            path. add(0, current.location);
            current = current.parent;
        }

        // 최종 목표 추가
        if (! path.isEmpty() && path.get(path.size() - 1).distance(finalTarget) > 0.5) {
            path.add(finalTarget);
        }

        // 경로 단순화
        return simplifyPath(path);
    }

    /**
     * 경로 단순화
     */
    private static List<Location> simplifyPath(List<Location> path) {
        if (path.size() <= 2) return path;

        List<Location> simplified = new ArrayList<>();
        simplified.add(path. get(0));

        for (int i = 1; i < path.size() - 1; i++) {
            Location prev = path.get(i - 1);
            Location current = path.get(i);
            Location next = path.get(i + 1);

            // 방향이 변경되는 지점만 유지
            Vector dir1 = current.toVector().subtract(prev.toVector()).normalize();
            Vector dir2 = next.toVector().subtract(current.toVector()).normalize();

            if (dir1.angle(dir2) > 0.1) {
                simplified.add(current);
            }
        }

        simplified.add(path. get(path.size() - 1));
        return simplified;
    }

    /**
     * 직선 경로 생성
     */
    private static List<Location> createDirectPath(Location start, Location end) {
        List<Location> path = new ArrayList<>();
        path.add(start);

        // 중간 지점 추가
        double distance = start.distance(end);
        int segments = (int) Math.min(distance / 3, 10);

        for (int i = 1; i < segments; i++) {
            double ratio = (double) i / segments;
            Location intermediate = start.clone().add(
                (end.getX() - start.getX()) * ratio,
                (end.getY() - start.getY()) * ratio,
                (end.getZ() - start.getZ()) * ratio
            );
            path.add(intermediate);
        }

        path.add(end);
        return path;
    }

    /**
     * 다음 이동 방향 계산
     */
    public static Vector getNextDirection(Location current, Location target) {
        Vector direction = target.toVector().subtract(current.toVector());
        
        if (direction.lengthSquared() < 0.01) {
            return new Vector(0, 0, 0);
        }

        return direction.normalize();
    }

    /**
     * 시야 확인
     */
    public static boolean hasLineOfSight(Location from, Location to) {
        if (!from.getWorld().equals(to.getWorld())) return false;

        Vector direction = to.toVector().subtract(from.toVector());
        double distance = direction.length();
        direction. normalize();

        Location current = from.clone();
        double step = 0.5;

        for (double d = 0; d < distance; d += step) {
            current. add(direction.clone().multiply(step));
            if (current.getBlock().getType().isSolid()) {
                return false;
            }
        }

        return true;
    }

    // ===== 내부 클래스 =====

    /**
     * 경로 노드
     */
    private static class PathNode {
        Location location;
        PathNode parent;
        double gCost = Double.MAX_VALUE;  // 시작점에서의 비용
        double hCost = 0;                  // 목표점까지의 예상 비용
        double fCost = Double.MAX_VALUE;  // 총 비용

        PathNode(Location location) {
            this.location = location;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof PathNode)) return false;
            PathNode other = (PathNode) obj;
            return location. getBlockX() == other.location.getBlockX() &&
                   location.getBlockY() == other.location.getBlockY() &&
                   location.getBlockZ() == other.location.getBlockZ();
        }

        @Override
        public int hashCode() {
            return Objects. hash(location. getBlockX(), location.getBlockY(), location.getBlockZ());
        }
    }
}