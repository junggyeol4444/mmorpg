package com.multiverse.quest.objectives;

import com.multiverse. quest.models.*;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit. World;
import org.bukkit. Bukkit;
import java.util.*;

/**
 * 지역 탐험 목표 핸들러
 * 플레이어가 특정 지역을 탐험하도록 하는 목표를 관리합니다.
 */
public class ExploreRegionHandler implements ObjectiveHandler {
    private QuestObjective objective;
    private String regionName;                      // 지역 이름
    private Location regionCenter;                  // 지역 중심
    private double regionRadius;                    // 지역 반지름
    private int requiredPoints;                     // 필요한 탐험 지점 수
    private Set<String> exploredPoints;            // 탐험한 지점 (위치 저장)
    private Map<UUID, Set<String>> playerExplored;  // 플레이어별 탐험한 지점
    private Map<UUID, Long> lastUpdateTime;        // 마지막 업데이트 시간
    private boolean enabled;

    /**
     * 생성자
     */
    public ExploreRegionHandler() {
        this.exploredPoints = new HashSet<>();
        this.playerExplored = new HashMap<>();
        this.lastUpdateTime = new HashMap<>();
        this.enabled = true;
        this.regionRadius = 100.0;
        this.requiredPoints = 5;
    }

    // ============ Initialization ============

    @Override
    public void initialize(QuestObjective objective) {
        this.objective = objective;
        this.regionName = objective.getDescription();
        this.requiredPoints = objective.getRequired();
        
        // 탐험 지점 초기화 (원형 격자)
        generateExplorationPoints();
    }

    @Override
    public void cleanup() {
        exploredPoints.clear();
        playerExplored.clear();
        lastUpdateTime.clear();
    }

    /**
     * 탐험 지점 생성
     */
    private void generateExplorationPoints() {
        if (regionCenter == null) return;

        exploredPoints.clear();
        double angle = 360.0 / requiredPoints;

        for (int i = 0; i < requiredPoints; i++) {
            double rad = Math.toRadians(angle * i);
            double x = regionCenter.getX() + regionRadius * Math.cos(rad);
            double z = regionCenter.getZ() + regionRadius * Math.sin(rad);
            double y = regionCenter.getY();

            String pointKey = String.format("%. 0f,%.0f,%.0f", x, y, z);
            exploredPoints.add(pointKey);
        }
    }

    // ============ Progress Tracking ============

    @Override
    public boolean updateProgress(Player player, UUID playerUUID, int amount) {
        if (!enabled || ! canProgress(player, playerUUID)) {
            return false;
        }

        if (regionCenter == null || player.getLocation(). getWorld() != regionCenter.getWorld()) {
            return false;
        }

        Location playerLoc = player.getLocation();
        String pointKey = String.format("%.0f,%.0f,%.0f", playerLoc.getX(), playerLoc.getY(), playerLoc.getZ());

        Set<String> explored = playerExplored.computeIfAbsent(playerUUID, k -> new HashSet<>());

        // 가장 가까운 탐험 지점 찾기
        String closestPoint = findClosestExplorationPoint(playerLoc);
        if (closestPoint != null && ! explored.contains(closestPoint)) {
            explored.add(closestPoint);
            lastUpdateTime.put(playerUUID, System.currentTimeMillis());
            onProgress(player, playerUUID, 1);

            if (explored.size() >= requiredPoints) {
                onComplete(player, playerUUID);
            }
            return true;
        }

        return false;
    }

    @Override
    public int getProgress(UUID playerUUID) {
        Set<String> explored = playerExplored.getOrDefault(playerUUID, new HashSet<>());
        return explored.size();
    }

    @Override
    public boolean isCompleted(UUID playerUUID) {
        return getProgress(playerUUID) >= requiredPoints;
    }

    @Override
    public void resetProgress(UUID playerUUID) {
        playerExplored.remove(playerUUID);
        lastUpdateTime. remove(playerUUID);
    }

    // ============ Validation ============

    @Override
    public boolean canProgress(Player player, UUID playerUUID) {
        if (player == null || ! enabled) {
            return false;
        }

        if (! checkConditions(player)) {
            return false;
        }

        return getProgress(playerUUID) < requiredPoints;
    }

    @Override
    public boolean isValid() {
        return objective != null &&
               regionName != null && ! regionName.isEmpty() &&
               regionCenter != null &&
               regionRadius > 0 &&
               requiredPoints > 0;
    }

    // ============ Information ============

    @Override
    public String getObjectiveType() {
        return "EXPLORE_REGION";
    }

    @Override
    public String getDescription() {
        return objective != null ? objective.getDescription() : "";
    }

    @Override
    public String getProgressString(UUID playerUUID) {
        return String.format("%d/%d", getProgress(playerUUID), requiredPoints);
    }

    @Override
    public String getDetailedInfo(UUID playerUUID) {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 지역 탐험 목표 ===§r\n");
        sb.append("§7지역: §f").append(regionName).append("\n");
        sb.append("§7진행도: §f").append(getProgressString(playerUUID)).append("\n");
        sb.append("§7반지름: §f").append(String.format("%.1f", regionRadius)).append("\n");
        sb.append("§7완료: ").append(isCompleted(playerUUID) ?  "§a완료" : "§c진행중"). append("\n");
        return sb.toString();
    }

    // ============ Events ============

    @Override
    public void onStart(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage("§a목표: " + regionName + " 지역 " + requiredPoints + "곳 탐험하기");
        }
    }

    @Override
    public void onProgress(Player player, UUID playerUUID, int amount) {
        if (player != null) {
            int current = getProgress(playerUUID);
            player.sendMessage(String.format("§7[§a탐험§7] %s: §f%d/%d 지점 발견", 
                regionName, current, requiredPoints));
        }
    }

    @Override
    public void onComplete(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage("§a✓ 지역 탐험 목표 완료!");
        }
    }

    @Override
    public void onFail(Player player, UUID playerUUID, String reason) {
        if (player != null) {
            player.sendMessage(String.format("§c목표 실패: %s", reason));
        }
    }

    // ============ Data Management ============

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("type", getObjectiveType());
        data. put("regionName", regionName);
        data.put("requiredPoints", requiredPoints);
        data.put("regionRadius", regionRadius);
        
        if (regionCenter != null) {
            data.put("centerX", regionCenter.getX());
            data.put("centerY", regionCenter.getY());
            data.put("centerZ", regionCenter.getZ());
            data.put("world", regionCenter.getWorld().getName());
        }
        
        data.put("enabled", enabled);
        return data;
    }

    @Override
    public void deserialize(Map<String, Object> data) {
        if (data. containsKey("regionName")) {
            this.regionName = (String) data.get("regionName");
        }
        if (data.containsKey("requiredPoints")) {
            this.requiredPoints = (Integer) data.get("requiredPoints");
        }
        if (data.containsKey("regionRadius")) {
            this.regionRadius = ((Number) data.get("regionRadius")).doubleValue();
        }
        
        if (data.containsKey("centerX") && data.containsKey("centerY") && 
            data. containsKey("centerZ") && data.containsKey("world")) {
            double x = ((Number) data.get("centerX")).doubleValue();
            double y = ((Number) data.get("centerY")).doubleValue();
            double z = ((Number) data.get("centerZ")).doubleValue();
            World world = Bukkit.getWorld((String) data.get("world"));
            
            if (world != null) {
                this.regionCenter = new Location(world, x, y, z);
            }
        }
        
        if (data. containsKey("enabled")) {
            this.enabled = (Boolean) data.get("enabled");
        }
        
        generateExplorationPoints();
    }

    // ============ Conditions ============

    @Override
    public boolean checkConditions(Player player) {
        if (player == null) {
            return false;
        }

        if (player.isDead()) {
            return false;
        }

        return player.isOnline();
    }

    @Override
    public boolean checkCondition(Player player, String condition) {
        if (player == null || condition == null) {
            return false;
        }

        switch (condition.toLowerCase()) {
            case "alive":
                return ! player.isDead();
            case "online":
                return player. isOnline();
            case "in_region":
                return isPlayerInRegion(player);
            case "has_location":
                return regionCenter != null;
            default:
                return true;
        }
    }

    // ============ Region Management ============

    /**
     * 플레이어가 지역 내에 있는지 확인
     */
    private boolean isPlayerInRegion(Player player) {
        if (player == null || regionCenter == null) {
            return false;
        }

        if (! player.getWorld().equals(regionCenter.getWorld())) {
            return false;
        }

        return player. getLocation().distance(regionCenter) <= regionRadius;
    }

    /**
     * 가장 가까운 탐험 지점 찾기
     */
    private String findClosestExplorationPoint(Location playerLoc) {
        String closest = null;
        double minDist = 50.0; // 최소 거리

        for (String pointKey : exploredPoints) {
            String[] coords = pointKey.split(",");
            if (coords.length != 3) continue;

            try {
                double x = Double. parseDouble(coords[0]);
                double y = Double.parseDouble(coords[1]);
                double z = Double.parseDouble(coords[2]);

                Location pointLoc = new Location(playerLoc.getWorld(), x, y, z);
                double dist = playerLoc.distance(pointLoc);

                if (dist < minDist) {
                    minDist = dist;
                    closest = pointKey;
                }
            } catch (NumberFormatException e) {
                // Skip
            }
        }

        return closest;
    }

    // ============ Statistics ============

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("type", getObjectiveType());
        stats.put("regionName", regionName);
        stats.put("requiredPoints", requiredPoints);
        stats.put("totalPlayers", playerExplored. size());
        stats.put("regionRadius", regionRadius);
        stats.put("enabled", enabled);
        return stats;
    }

    @Override
    public Map<String, Object> getPlayerStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("playerUUID", playerUUID);
        stats.put("progress", getProgress(playerUUID));
        stats.put("required", requiredPoints);
        stats.put("percentage", (getProgress(playerUUID) * 100) / requiredPoints);
        stats.put("completed", isCompleted(playerUUID));
        
        Long lastUpdate = lastUpdateTime.get(playerUUID);
        if (lastUpdate != null) {
            stats.put("lastUpdateTime", lastUpdate);
        }
        
        return stats;
    }

    // ============ Getters & Setters ============

    /**
     * 지역 이름 반환
     */
    public String getRegionName() {
        return regionName;
    }

    /**
     * 지역 이름 설정
     */
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    /**
     * 지역 중심 반환
     */
    public Location getRegionCenter() {
        return regionCenter;
    }

    /**
     * 지역 중심 설정
     */
    public void setRegionCenter(Location regionCenter) {
        this.regionCenter = regionCenter;
        generateExplorationPoints();
    }

    /**
     * 지역 반지름 반환
     */
    public double getRegionRadius() {
        return regionRadius;
    }

    /**
     * 지역 반지름 설정
     */
    public void setRegionRadius(double regionRadius) {
        this.regionRadius = Math.max(regionRadius, 1.0);
        generateExplorationPoints();
    }

    /**
     * 필요한 지점 수 반환
     */
    public int getRequiredPoints() {
        return requiredPoints;
    }

    /**
     * 필요한 지점 수 설정
     */
    public void setRequiredPoints(int requiredPoints) {
        this.requiredPoints = Math.max(requiredPoints, 1);
        generateExplorationPoints();
    }

    /**
     * 활성화 여부 설정
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 활성화 여부 조회
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 모든 플레이어 진행도 초기화
     */
    public void resetAllProgress() {
        playerExplored.clear();
        lastUpdateTime.clear();
    }
}