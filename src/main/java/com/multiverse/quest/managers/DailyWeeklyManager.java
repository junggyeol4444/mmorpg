package com.multiverse.quest.managers;

import com.multiverse.   quest.models.*;
import com.multiverse.quest.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.  Bukkit;
import java.util.*;
import java.time.*;
import java.time.temporal.TemporalAdjusters;

/**
 * 일일/주간 퀘스트 관리자
 * 일일 및 주간 퀘스트의 리셋 및 상태를 관리합니다.
 */
public class DailyWeeklyManager {
    private final DataManager dataManager;
    private final Map<UUID, Map<String, Long>> playerLastCompletionTime;
    private boolean initialized;

    private static final long DAILY_RESET_INTERVAL = 24 * 60 * 60 * 1000; // 24시간
    private static final long WEEKLY_RESET_INTERVAL = 7 * 24 * 60 * 60 * 1000; // 7일

    /**
     * 생성자
     * @param dataManager 데이터 관리자
     */
    public DailyWeeklyManager(DataManager dataManager) {
        this.dataManager = dataManager;
        this.playerLastCompletionTime = new HashMap<>();
        this.initialized = false;
    }

    // ============ Initialization ============

    /**
     * 일일/주간 관리자 초기화
     */
    public boolean initialize() {
        try {
            initialized = true;
            Bukkit.getLogger().info("일일/주간 퀘스트 관리자 초기화 완료");
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().warning("일일/주간 퀘스트 관리자 초기화 실패: " + e.getMessage());
            initialized = false;
            return false;
        }
    }

    /**
     * 초기화 여부 확인
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 종료
     */
    public boolean shutdown() {
        try {
            playerLastCompletionTime.clear();
            initialized = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Daily Quest Management ============

    /**
     * 플레이어가 일일 퀘스트를 수락할 수 있는지 확인
     */
    public boolean canAcceptDailyQuest(UUID playerUUID, String questId) {
        if (playerUUID == null || questId == null) {
            return false;
        }

        Long lastCompletionTime = getLastCompletionTime(playerUUID, questId);
        if (lastCompletionTime == null) {
            return true; // 처음 시도
        }

        long currentTime = System.currentTimeMillis();
        return (currentTime - lastCompletionTime) >= DAILY_RESET_INTERVAL;
    }

    /**
     * 일일 퀨스트 완료 기록
     */
    public boolean recordDailyQuestCompletion(UUID playerUUID, String questId) {
        if (playerUUID == null || questId == null) {
            return false;
        }

        Map<String, Long> playerRecord = playerLastCompletionTime
            .computeIfAbsent(playerUUID, k -> new HashMap<>());
        playerRecord.put(questId, System.currentTimeMillis());

        return true;
    }

    /**
     * 일일 퀨스트 완료까지 남은 시간 (초)
     */
    public long getDailyQuestCooldown(UUID playerUUID, String questId) {
        if (playerUUID == null || questId == null) {
            return 0;
        }

        Long lastCompletionTime = getLastCompletionTime(playerUUID, questId);
        if (lastCompletionTime == null) {
            return 0;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastCompletionTime;

        if (elapsedTime >= DAILY_RESET_INTERVAL) {
            return 0;
        }

        return (DAILY_RESET_INTERVAL - elapsedTime) / 1000; // 초 단위
    }

    /**
     * 플레이어의 모든 일일 퀨스트 리셋
     */
    public boolean resetPlayerDailyQuests(UUID playerUUID) {
        if (playerUUID == null) {
            return false;
        }

        try {
            Map<String, Long> playerRecord = playerLastCompletionTime.get(playerUUID);
            if (playerRecord != null) {
                playerRecord. clear();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 일일 퀨스트 리셋 시간까지 남은 시간 (초)
     */
    public long getTimeUntilDailyReset() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime nextReset = now.toLocalDate().plusDays(1)
            .atStartOfDay(ZoneId.systemDefault());

        long secondsUntilReset = ChronoUnit.SECONDS.between(now, nextReset);
        return Math.max(secondsUntilReset, 0);
    }

    // ============ Weekly Quest Management ============

    /**
     * 플레이어가 주간 퀘스트를 수락할 수 있는지 확인
     */
    public boolean canAcceptWeeklyQuest(UUID playerUUID, String questId) {
        if (playerUUID == null || questId == null) {
            return false;
        }

        Long lastCompletionTime = getLastCompletionTime(playerUUID, questId);
        if (lastCompletionTime == null) {
            return true; // 처음 시도
        }

        long currentTime = System. currentTimeMillis();
        return (currentTime - lastCompletionTime) >= WEEKLY_RESET_INTERVAL;
    }

    /**
     * 주간 퀨스트 완료 기록
     */
    public boolean recordWeeklyQuestCompletion(UUID playerUUID, String questId) {
        if (playerUUID == null || questId == null) {
            return false;
        }

        Map<String, Long> playerRecord = playerLastCompletionTime
            .computeIfAbsent(playerUUID, k -> new HashMap<>());
        playerRecord.put(questId, System.currentTimeMillis());

        return true;
    }

    /**
     * 주간 퀨스트 완료까지 남은 시간 (초)
     */
    public long getWeeklyQuestCooldown(UUID playerUUID, String questId) {
        if (playerUUID == null || questId == null) {
            return 0;
        }

        Long lastCompletionTime = getLastCompletionTime(playerUUID, questId);
        if (lastCompletionTime == null) {
            return 0;
        }

        long currentTime = System. currentTimeMillis();
        long elapsedTime = currentTime - lastCompletionTime;

        if (elapsedTime >= WEEKLY_RESET_INTERVAL) {
            return 0;
        }

        return (WEEKLY_RESET_INTERVAL - elapsedTime) / 1000; // 초 단위
    }

    /**
     * 플레이어의 모든 주간 퀨스트 리셋
     */
    public boolean resetPlayerWeeklyQuests(UUID playerUUID) {
        if (playerUUID == null) {
            return false;
        }

        try {
            Map<String, Long> playerRecord = playerLastCompletionTime.get(playerUUID);
            if (playerRecord != null) {
                playerRecord.clear();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 주간 퀨스트 리셋 시간까지 남은 시간 (초)
     */
    public long getTimeUntilWeeklyReset() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime nextReset = now.with(TemporalAdjusters.next(java.time.DayOfWeek.MONDAY))
            .toLocalDate().atStartOfDay(ZoneId. systemDefault());

        long secondsUntilReset = ChronoUnit.SECONDS.between(now, nextReset);
        return Math.max(secondsUntilReset, 0);
    }

    // ============ Completion Tracking ============

    /**
     * 마지막 완료 시간 조회
     */
    private Long getLastCompletionTime(UUID playerUUID, String questId) {
        Map<String, Long> playerRecord = playerLastCompletionTime.get(playerUUID);
        if (playerRecord == null) {
            return null;
        }

        return playerRecord.get(questId);
    }

    /**
     * 플레이어의 모든 완료 기록 조회
     */
    public Map<String, Long> getPlayerCompletionRecord(UUID playerUUID) {
        if (playerUUID == null) {
            return new HashMap<>();
        }

        Map<String, Long> record = playerLastCompletionTime. get(playerUUID);
        return record != null ? new HashMap<>(record) : new HashMap<>();
    }

    /**
     * 완료 기록 삭제
     */
    public boolean deleteCompletionRecord(UUID playerUUID, String questId) {
        if (playerUUID == null || questId == null) {
            return false;
        }

        Map<String, Long> playerRecord = playerLastCompletionTime. get(playerUUID);
        if (playerRecord != null) {
            playerRecord.remove(questId);
            return true;
        }

        return false;
    }

    // ============ Bulk Reset Operations ============

    /**
     * 모든 플레이어의 일일 퀨스트 리셋
     */
    public int resetAllDailyQuests() {
        int resetCount = 0;

        for (UUID playerUUID : playerLastCompletionTime.keySet()) {
            if (resetPlayerDailyQuests(playerUUID)) {
                resetCount++;
            }
        }

        Bukkit.getLogger().info("일일 퀨스트 리셋: " + resetCount + "명");
        return resetCount;
    }

    /**
     * 모든 플레이어의 주간 퀨스트 리셋
     */
    public int resetAllWeeklyQuests() {
        int resetCount = 0;

        for (UUID playerUUID : playerLastCompletionTime.keySet()) {
            if (resetPlayerWeeklyQuests(playerUUID)) {
                resetCount++;
            }
        }

        Bukkit.getLogger(). info("주간 퀨스트 리셋: " + resetCount + "명");
        return resetCount;
    }

    /**
     * 리셋 가능한 퀨스트 확인 및 리셋
     */
    public int autoResetDailyQuests() {
        int resetCount = 0;
        List<UUID> playersToReset = new ArrayList<>();

        for (Map.Entry<UUID, Map<String, Long>> entry : playerLastCompletionTime.entrySet()) {
            UUID playerUUID = entry.getKey();
            Map<String, Long> record = entry.getValue();

            long currentTime = System.currentTimeMillis();
            List<String> toRemove = new ArrayList<>();

            for (Map.Entry<String, Long> questEntry : record.entrySet()) {
                if ((currentTime - questEntry.getValue()) >= DAILY_RESET_INTERVAL) {
                    toRemove.add(questEntry.getKey());
                }
            }

            if (!toRemove.isEmpty()) {
                toRemove.forEach(record::remove);
                resetCount += toRemove.size();
            }
        }

        return resetCount;
    }

    /**
     * 리셋 가능한 주간 퀨스트 확인 및 리셋
     */
    public int autoResetWeeklyQuests() {
        int resetCount = 0;

        for (Map.Entry<UUID, Map<String, Long>> entry : playerLastCompletionTime. entrySet()) {
            Map<String, Long> record = entry.getValue();

            long currentTime = System.currentTimeMillis();
            List<String> toRemove = new ArrayList<>();

            for (Map.Entry<String, Long> questEntry : record.entrySet()) {
                if ((currentTime - questEntry.getValue()) >= WEEKLY_RESET_INTERVAL) {
                    toRemove.add(questEntry.getKey());
                }
            }

            if (!toRemove.isEmpty()) {
                toRemove. forEach(record::remove);
                resetCount += toRemove.size();
            }
        }

        return resetCount;
    }

    // ============ Statistics ============

    /**
     * 플레이어 완료 통계
     */
    public Map<String, Object> getPlayerStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();

        Map<String, Long> record = getPlayerCompletionRecord(playerUUID);
        stats.put("playerUUID", playerUUID);
        stats.put("totalCompletions", record.size());
        stats.put("nextDailyReset", formatTimeUntilReset(getTimeUntilDailyReset()));
        stats.put("nextWeeklyReset", formatTimeUntilReset(getTimeUntilWeeklyReset()));

        return stats;
    }

    /**
     * 전체 리셋 통계
     */
    public Map<String, Object> getGlobalStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        int totalPlayers = playerLastCompletionTime.size();
        int totalRecords = 0;

        for (Map<String, Long> record : playerLastCompletionTime. values()) {
            totalRecords += record.size();
        }

        stats.put("totalPlayers", totalPlayers);
        stats.put("totalCompletionRecords", totalRecords);
        stats.put("nextDailyReset", formatTimeUntilReset(getTimeUntilDailyReset()));
        stats.put("nextWeeklyReset", formatTimeUntilReset(getTimeUntilWeeklyReset()));

        return stats;
    }

    // ============ Utility Methods ============

    /**
     * 시간을 포맷된 문자열로 변환
     */
    private String formatTimeUntilReset(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%d시간 %d분 %d초", hours, minutes, secs);
    }

    /**
     * 시간 포맷
     */
    public String formatTime(long seconds) {
        if (seconds < 60) {
            return seconds + "초";
        } else if (seconds < 3600) {
            return (seconds / 60) + "분";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "시간";
        } else {
            return (seconds / 86400) + "일";
        }
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 일일/주간 퀨스트 관리자 상태 ===§r\n");
        sb.append("§7초기화: ").append(initialized ? "§a완료" : "§c미완료").append("\n");
        sb.append("§7진행 중인 플레이어: §f").append(playerLastCompletionTime.  size()).append("\n");
        sb.append("§7다음 일일 리셋: §f").append(formatTimeUntilReset(getTimeUntilDailyReset())).append("\n");
        sb.append("§7다음 주간 리셋: §f"). append(formatTimeUntilReset(getTimeUntilWeeklyReset())).append("\n");

        return sb.toString();
    }

    // ============ Getters & Setters ============

    /**
     * 데이터 관리자 반환
     */
    public DataManager getDataManager() {
        return dataManager;
    }

    /**
     * 캐시 정보 반환
     */
    public Map<String, Object> getCacheInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info. put("cachedPlayers", playerLastCompletionTime.size());

        int totalRecords = 0;
        for (Map<String, Long> record : playerLastCompletionTime.values()) {
            totalRecords += record. size();
        }
        info.put("totalRecords", totalRecords);

        return info;
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        playerLastCompletionTime.clear();
    }
}