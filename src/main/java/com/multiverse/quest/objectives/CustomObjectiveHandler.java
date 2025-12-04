package com.multiverse.quest.objectives;

import com.multiverse. quest.models.*;
import org.bukkit.entity.Player;
import org.bukkit. Bukkit;
import java.util.*;
import java.util.function.Function;

/**
 * 커스텀 목표 핸들러
 * 플러그인 개발자가 자신만의 목표를 만들 수 있도록 하는 핸들러입니다.
 */
public class CustomObjectiveHandler implements ObjectiveHandler {
    private QuestObjective objective;
    private String customType;                      // 커스텀 목표 타입
    private String customDescription;               // 커스텀 설명
    private int requiredValue;                      // 필요한 값
    private Map<UUID, Integer> playerProgress;     // 플레이어 진행도
    private Map<UUID, Long> lastUpdateTime;        // 마지막 업데이트 시간
    private Map<String, Object> customData;        // 커스텀 데이터
    private Function<Player, Boolean> progressCondition;  // 진행 조건 함수
    private Function<Player, Boolean> completeCondition;  // 완료 조건 함수
    private Runnable onProgressAction;              // 진행 시 실행할 액션
    private Runnable onCompleteAction;              // 완료 시 실행할 액션
    private boolean enabled;

    /**
     * 생성자
     */
    public CustomObjectiveHandler() {
        this.playerProgress = new HashMap<>();
        this.lastUpdateTime = new HashMap<>();
        this.customData = new HashMap<>();
        this.enabled = true;
        this.requiredValue = 1;
        
        // 기본 조건 설정
        this. progressCondition = player -> player != null && player.isOnline() && ! player.isDead();
        this.completeCondition = player -> player != null && player.isOnline() && !player.isDead();
    }

    // ============ Initialization ============

    @Override
    public void initialize(QuestObjective objective) {
        this.objective = objective;
        this.customType = objective.getObjectiveType();
        this.customDescription = objective.getDescription();
        this.requiredValue = objective.getRequired();
    }

    @Override
    public void cleanup() {
        playerProgress.clear();
        lastUpdateTime.clear();
        customData.clear();
        onProgressAction = null;
        onCompleteAction = null;
    }

    // ============ Progress Tracking ============

    @Override
    public boolean updateProgress(Player player, UUID playerUUID, int amount) {
        if (!enabled || ! canProgress(player, playerUUID)) {
            return false;
        }

        int currentProgress = playerProgress.getOrDefault(playerUUID, 0);
        int newProgress = Math.min(currentProgress + amount, requiredValue);

        if (newProgress == currentProgress) {
            return false;
        }

        playerProgress.put(playerUUID, newProgress);
        lastUpdateTime. put(playerUUID, System. currentTimeMillis());

        onProgress(player, playerUUID, amount);

        if (newProgress >= requiredValue) {
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
        return getProgress(playerUUID) >= requiredValue;
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

        if (!checkConditions(player)) {
            return false;
        }

        return getProgress(playerUUID) < requiredValue;
    }

    @Override
    public boolean isValid() {
        return objective != null &&
               customType != null && !customType.isEmpty() &&
               requiredValue > 0;
    }

    // ============ Information ============

    @Override
    public String getObjectiveType() {
        return customType != null ? customType : "CUSTOM";
    }

    @Override
    public String getDescription() {
        return customDescription != null ? customDescription : "";
    }

    @Override
    public String getProgressString(UUID playerUUID) {
        return String.format("%d/%d", getProgress(playerUUID), requiredValue);
    }

    @Override
    public String getDetailedInfo(UUID playerUUID) {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 커스텀 목표 ===§r\n");
        sb.append("§7타입: §f").append(customType).append("\n");
        sb.append("§7설명: §f").append(customDescription).append("\n");
        sb.append("§7진행도: §f").append(getProgressString(playerUUID)).append("\n");
        sb.append("§7완료: ").append(isCompleted(playerUUID) ?  "§a완료" : "§c진행중").append("\n");
        return sb.toString();
    }

    // ============ Events ============

    @Override
    public void onStart(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage("§a목표: " + customDescription);
        }
    }

    @Override
    public void onProgress(Player player, UUID playerUUID, int amount) {
        if (player != null) {
            int current = getProgress(playerUUID);
            player.sendMessage(String.format("§7[§a진행§7] %s: §f%d/%d",
                customDescription, current, requiredValue));
        }

        // 커스텀 액션 실행
        if (onProgressAction != null) {
            try {
                onProgressAction.run();
            } catch (Exception e) {
                Bukkit.getLogger().warning("커스텀 진행 액션 실행 실패: " + e.getMessage());
            }
        }
    }

    @Override
    public void onComplete(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage("§a✓ 목표 완료!");
        }

        // 커스텀 액션 실행
        if (onCompleteAction != null) {
            try {
                onCompleteAction.run();
            } catch (Exception e) {
                Bukkit.getLogger().warning("커스텀 완료 액션 실행 실패: " + e.getMessage());
            }
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
        data. put("description", customDescription);
        data.put("requiredValue", requiredValue);
        data.put("enabled", enabled);
        data.putAll(customData);
        return data;
    }

    @Override
    public void deserialize(Map<String, Object> data) {
        if (data.containsKey("description")) {
            this.customDescription = (String) data.get("description");
        }
        if (data.containsKey("requiredValue")) {
            this.requiredValue = (Integer) data.get("requiredValue");
        }
        if (data.containsKey("enabled")) {
            this.enabled = (Boolean) data.get("enabled");
        }

        // 커스텀 데이터 로드
        customData.putAll(data);
    }

    // ============ Conditions ============

    @Override
    public boolean checkConditions(Player player) {
        if (player == null) {
            return false;
        }

        try {
            return progressCondition.apply(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("커스텀 조건 확인 실패: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean checkCondition(Player player, String condition) {
        if (player == null || condition == null) {
            return false;
        }

        switch (condition. toLowerCase()) {
            case "alive":
                return ! player.isDead();
            case "online":
                return player. isOnline();
            default:
                return true;
        }
    }

    // ============ Custom Methods ============

    /**
     * 진행 조건 함수 설정
     */
    public void setProgressCondition(Function<Player, Boolean> condition) {
        this.progressCondition = condition != null ? condition : player -> true;
    }

    /**
     * 완료 조건 함수 설정
     */
    public void setCompleteCondition(Function<Player, Boolean> condition) {
        this.completeCondition = condition != null ? condition : player -> true;
    }

    /**
     * 진행 시 실행할 액션 설정
     */
    public void setOnProgressAction(Runnable action) {
        this.onProgressAction = action;
    }

    /**
     * 완료 시 실행할 액션 설정
     */
    public void setOnCompleteAction(Runnable action) {
        this.onCompleteAction = action;
    }

    /**
     * 커스텀 데이터 저장
     */
    public void setCustomData(String key, Object value) {
        customData.put(key, value);
    }

    /**
     * 커스텀 데이터 조회
     */
    public Object getCustomData(String key) {
        return customData.get(key);
    }

    /**
     * 커스텀 데이터 조회 (기본값)
     */
    public Object getCustomData(String key, Object defaultValue) {
        return customData.getOrDefault(key, defaultValue);
    }

    /**
     * 모든 커스텀 데이터 반환
     */
    public Map<String, Object> getAllCustomData() {
        return new HashMap<>(customData);
    }

    /**
     * 커스텀 타입 설정
     */
    public void setCustomType(String type) {
        this.customType = type;
    }

    /**
     * 커스텀 설명 설정
     */
    public void setCustomDescription(String description) {
        this.customDescription = description;
    }

    /**
     * 필요 값 설정
     */
    public void setRequiredValue(int value) {
        this.requiredValue = Math.max(value, 1);
    }

    /**
     * 필요 값 반환
     */
    public int getRequiredValue() {
        return requiredValue;
    }

    // ============ Statistics ============

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("type", getObjectiveType());
        stats.put("customType", customType);
        stats.put("description", customDescription);
        stats. put("requiredValue", requiredValue);
        stats.put("totalPlayers", playerProgress.size());
        stats.put("enabled", enabled);
        return stats;
    }

    @Override
    public Map<String, Object> getPlayerStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats. put("playerUUID", playerUUID);
        stats.put("progress", getProgress(playerUUID));
        stats.put("required", requiredValue);
        stats.put("percentage", (getProgress(playerUUID) * 100) / requiredValue);
        stats.put("completed", isCompleted(playerUUID));

        Long lastUpdate = lastUpdateTime.get(playerUUID);
        if (lastUpdate != null) {
            stats.put("lastUpdateTime", lastUpdate);
        }

        return stats;
    }

    // ============ Getters & Setters ============

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
        playerProgress. clear();
        lastUpdateTime. clear();
    }

    /**
     * 진행도 캐시 정보 반환
     */
    public Map<UUID, Integer> getProgressCache() {
        return new HashMap<>(playerProgress);
    }
}