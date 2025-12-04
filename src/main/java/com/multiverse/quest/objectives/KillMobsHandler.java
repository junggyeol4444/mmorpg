package com.multiverse.quest.objectives;

import com.multiverse. quest.models.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit. Bukkit;
import java.util.*;

/**
 * 몹 처치 목표 핸들러
 * 플레이어가 특정 몹을 처치하도록 하는 목표를 관리합니다.
 */
public class KillMobsHandler implements ObjectiveHandler {
    private QuestObjective objective;
    private String mobType;                         // 처치할 몹 타입
    private int requiredCount;                      // 필요한 처치 수
    private Map<UUID, Integer> playerProgress;     // 플레이어 진행도
    private Map<UUID, Long> lastUpdateTime;        // 마지막 업데이트 시간
    private boolean enabled;

    /**
     * 생성자
     */
    public KillMobsHandler() {
        this.playerProgress = new HashMap<>();
        this.lastUpdateTime = new HashMap<>();
        this.enabled = true;
    }

    // ============ Initialization ============

    @Override
    public void initialize(QuestObjective objective) {
        this.objective = objective;
        this.mobType = objective.getDescription();
        this.requiredCount = objective.getRequired();
    }

    @Override
    public void cleanup() {
        playerProgress.clear();
        lastUpdateTime.clear();
    }

    // ============ Progress Tracking ============

    @Override
    public boolean updateProgress(Player player, UUID playerUUID, int amount) {
        if (! enabled || ! canProgress(player, playerUUID)) {
            return false;
        }

        int currentProgress = playerProgress.getOrDefault(playerUUID, 0);
        int newProgress = Math.min(currentProgress + amount, requiredCount);

        if (newProgress == currentProgress) {
            return false;
        }

        playerProgress. put(playerUUID, newProgress);
        lastUpdateTime.put(playerUUID, System.currentTimeMillis());

        onProgress(player, playerUUID, amount);

        if (newProgress >= requiredCount) {
            onComplete(player, playerUUID);
        }

        return true;
    }

    @Override
    public int getProgress(UUID playerUUID) {
        return playerProgress.getOrDefault(playerUUID, 0);
    }

    @Override
    public boolean isCompleted(UUID playerUUID) {
        return getProgress(playerUUID) >= requiredCount;
    }

    @Override
    public void resetProgress(UUID playerUUID) {
        playerProgress.remove(playerUUID);
        lastUpdateTime.remove(playerUUID);
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

        return getProgress(playerUUID) < requiredCount;
    }

    @Override
    public boolean isValid() {
        return objective != null && 
               mobType != null && ! mobType.isEmpty() &&
               requiredCount > 0;
    }

    // ============ Information ============

    @Override
    public String getObjectiveType() {
        return "KILL_MOBS";
    }

    @Override
    public String getDescription() {
        return objective != null ? objective.getDescription() : "";
    }

    @Override
    public String getProgressString(UUID playerUUID) {
        return String.format("%d/%d", getProgress(playerUUID), requiredCount);
    }

    @Override
    public String getDetailedInfo(UUID playerUUID) {
        StringBuilder sb = new StringBuilder();
        sb.  append("§6=== 몹 처치 목표 ===§r\n");
        sb.append("§7몹 타입: §f").append(mobType).append("\n");
        sb.append("§7진행도: §f").append(getProgressString(playerUUID)). append("\n");
        sb. append("§7완료: ").append(isCompleted(playerUUID) ?  "§a완료" : "§c진행중").append("\n");
        return sb.toString();
    }

    // ============ Events ============

    @Override
    public void onStart(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage("§a목표: " + mobType + " " + requiredCount + "마리 처치하기");
        }
    }

    @Override
    public void onProgress(Player player, UUID playerUUID, int amount) {
        if (player != null) {
            int current = getProgress(playerUUID);
            player.sendMessage(String.format("§7[§a진행§7] %s: §f%d/%d", 
                mobType, current, requiredCount));
        }
    }

    @Override
    public void onComplete(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage("§a✓ 몹 처치 목표 완료!");
        }
    }

    @Override
    public void onFail(Player player, UUID playerUUID, String reason) {
        if (player != null) {
            player. sendMessage(String.format("§c목표 실패: %s", reason));
        }
    }

    // ============ Data Management ============

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("type", getObjectiveType());
        data. put("mobType", mobType);
        data.put("requiredCount", requiredCount);
        data.put("enabled", enabled);
        return data;
    }

    @Override
    public void deserialize(Map<String, Object> data) {
        if (data.containsKey("mobType")) {
            this.mobType = (String) data.get("mobType");
        }
        if (data.containsKey("requiredCount")) {
            this.requiredCount = (Integer) data.get("requiredCount");
        }
        if (data.containsKey("enabled")) {
            this.enabled = (Boolean) data.get("enabled");
        }
    }

    // ============ Conditions ============

    @Override
    public boolean checkConditions(Player player) {
        if (player == null) {
            return false;
        }

        // 플레이어가 생존 중인지 확인
        if (player.isDead()) {
            return false;
        }

        // 플레이어가 온라인인지 확인
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
                return player.isOnline();
            case "in_world":
                return player.getWorld() != null;
            default:
                return true;
        }
    }

    // ============ Statistics ============

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats. put("type", getObjectiveType());
        stats.put("mobType", mobType);
        stats.put("requiredCount", requiredCount);
        stats.put("totalPlayers", playerProgress.size());
        stats. put("enabled", enabled);
        return stats;
    }

    @Override
    public Map<String, Object> getPlayerStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats. put("playerUUID", playerUUID);
        stats.  put("progress", getProgress(playerUUID));
        stats.put("required", requiredCount);
        stats.put("percentage", (getProgress(playerUUID) * 100) / requiredCount);
        stats.put("completed", isCompleted(playerUUID));
        
        Long lastUpdate = lastUpdateTime.get(playerUUID);
        if (lastUpdate != null) {
            stats.put("lastUpdateTime", lastUpdate);
        }
        
        return stats;
    }

    // ============ Getters & Setters ============

    /**
     * 몹 타입 반환
     */
    public String getMobType() {
        return mobType;
    }

    /**
     * 몹 타입 설정
     */
    public void setMobType(String mobType) {
        this.mobType = mobType;
    }

    /**
     * 필요한 처치 수 반환
     */
    public int getRequiredCount() {
        return requiredCount;
    }

    /**
     * 필요한 처치 수 설정
     */
    public void setRequiredCount(int requiredCount) {
        this. requiredCount = Math.max(requiredCount, 1);
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
        playerProgress.clear();
        lastUpdateTime.clear();
    }
}