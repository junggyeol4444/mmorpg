package com.multiverse.quest.managers;

import com.multiverse.  quest.models.*;
import com.multiverse.quest.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * 퀘스트 추적 관리자
 * 플레이어의 퀘스트 추적 상태를 관리합니다.
 */
public class QuestTrackerManager {
    private final DataManager dataManager;
    private final Map<UUID, QuestTracker> trackerCache;
    private boolean initialized;

    /**
     * 생성자
     * @param dataManager 데이터 관리자
     */
    public QuestTrackerManager(DataManager dataManager) {
        this.dataManager = dataManager;
        this.trackerCache = new HashMap<>();
        this.initialized = false;
    }

    // ============ Initialization ============

    /**
     * 추적 관리자 초기화
     */
    public boolean initialize() {
        try {
            initialized = true;
            Bukkit.getLogger().info("퀘스트 추적 관리자 초기화 완료");
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().warning("퀘스트 추적 관리자 초기화 실패: " + e.getMessage());
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
            trackerCache.clear();
            initialized = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Tracker Loading ============

    /**
     * 플레이어 추적기 로드
     */
    public QuestTracker loadTracker(UUID playerUUID) {
        if (playerUUID == null) {
            return null;
        }

        if (trackerCache.containsKey(playerUUID)) {
            return trackerCache.get(playerUUID);
        }

        QuestTracker tracker = dataManager.getPlayerTracker(playerUUID);
        if (tracker == null) {
            tracker = new QuestTracker(playerUUID);
        }

        trackerCache.put(playerUUID, tracker);
        return tracker;
    }

    /**
     * 모든 추적기 로드
     */
    public void loadAllTrackers() {
        trackerCache.clear();
        // 모든 플레이어의 추적기 로드 (커스텀 구현)
    }

    // ============ Tracker Management ============

    /**
     * 퀘스트 추적 시작
     */
    public boolean startTracking(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();

        try {
            QuestTracker tracker = loadTracker(playerUUID);
            tracker.setTrackedQuestId(questId);
            tracker.setEnabled(true);

            return updateTracker(playerUUID, tracker);
        } catch (Exception e) {
            player.sendMessage("§c퀘스트 추적 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 퀘스트 추적 중지
     */
    public boolean stopTracking(Player player) {
        if (player == null) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();

        try {
            QuestTracker tracker = loadTracker(playerUUID);
            tracker.setEnabled(false);
            tracker.setTrackedQuestId(null);

            return updateTracker(playerUUID, tracker);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 추적기 위치 설정
     */
    public boolean setTrackerPosition(Player player, TrackerPosition position) {
        if (player == null || position == null) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();

        try {
            QuestTracker tracker = loadTracker(playerUUID);
            tracker.setPosition(position);

            return updateTracker(playerUUID, tracker);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 추적기 업데이트
     */
    private boolean updateTracker(UUID playerUUID, QuestTracker tracker) {
        try {
            trackerCache.put(playerUUID, tracker);
            dataManager.savePlayerTracker(playerUUID, tracker);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Tracker Retrieval ============

    /**
     * 플레이어 추적기 조회
     */
    public QuestTracker getTracker(UUID playerUUID) {
        return loadTracker(playerUUID);
    }

    /**
     * 추적 중인 퀨스트 ID 조회
     */
    public String getTrackedQuestId(UUID playerUUID) {
        QuestTracker tracker = loadTracker(playerUUID);
        if (tracker == null || ! tracker.isEnabled()) {
            return null;
        }

        return tracker.getTrackedQuestId();
    }

    /**
     * 추적 위치 조회
     */
    public TrackerPosition getTrackerPosition(UUID playerUUID) {
        QuestTracker tracker = loadTracker(playerUUID);
        if (tracker == null) {
            return TrackerPosition.TOP_RIGHT;
        }

        return tracker.getPosition();
    }

    /**
     * 추적기 활성화 여부 확인
     */
    public boolean isTrackerEnabled(UUID playerUUID) {
        QuestTracker tracker = loadTracker(playerUUID);
        return tracker != null && tracker.isEnabled();
    }

    /**
     * 추적 중인지 확인
     */
    public boolean isTracking(UUID playerUUID, String questId) {
        QuestTracker tracker = loadTracker(playerUUID);
        if (tracker == null || !tracker.isEnabled()) {
            return false;
        }

        return questId. equals(tracker.getTrackedQuestId());
    }

    // ============ Tracker Display ============

    /**
     * 추적기 화면에 표시
     */
    public void displayTracker(Player player) {
        if (player == null) {
            return;
        }

        UUID playerUUID = player.getUniqueId();
        QuestTracker tracker = loadTracker(playerUUID);

        if (tracker == null || !tracker.isEnabled()) {
            return;
        }

        String questId = tracker.getTrackedQuestId();
        if (questId == null) {
            return;
        }

        try {
            // 추적기 정보 표시 (실제 구현은 스코어보드 등 사용)
            StringBuilder sb = new StringBuilder();
            sb.append("\n§6=== 퀨스트 추적 ===§r\n");
            sb.append("§7퀨스트: §f"). append(questId).append("\n");

            player.sendMessage(sb.toString());
        } catch (Exception e) {
            Bukkit.getLogger().warning("추적기 표시 실패: " + e.getMessage());
        }
    }

    /**
     * 모든 플레이어에게 추적기 업데이트
     */
    public void updateAllTrackers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            displayTracker(player);
        }
    }

    // ============ Tracker Validation ============

    /**
     * 추적기가 유효한지 확인
     */
    public boolean isTrackerValid(UUID playerUUID) {
        QuestTracker tracker = loadTracker(playerUUID);
        if (tracker == null) {
            return false;
        }

        if (!tracker.isEnabled()) {
            return false;
        }

        if (tracker.getTrackedQuestId() == null || tracker.getTrackedQuestId().isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * 추적기 설정 유효성 확인
     */
    public boolean validateTrackerSettings(QuestTracker tracker) {
        if (tracker == null) {
            return false;
        }

        if (tracker.getPlayerUUID() == null) {
            return false;
        }

        if (tracker.getPosition() == null) {
            return false;
        }

        return true;
    }

    // ============ Statistics ============

    /**
     * 플레이어 추적 통계
     */
    public Map<String, Object> getPlayerTrackerStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();
        QuestTracker tracker = loadTracker(playerUUID);

        if (tracker != null) {
            stats.put("playerUUID", playerUUID);
            stats.put("enabled", tracker.isEnabled());
            stats.put("trackedQuestId", tracker.getTrackedQuestId());
            stats.put("position", tracker.getPosition(). name());
        }

        return stats;
    }

    /**
     * 전체 추적 통계
     */
    public Map<String, Object> getGlobalTrackerStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        int activeTrackers = 0;
        Map<String, Integer> positionCount = new HashMap<>();

        for (QuestTracker tracker : trackerCache.values()) {
            if (tracker. isEnabled()) {
                activeTrackers++;
            }

            String position = tracker.getPosition().name();
            positionCount.put(position, positionCount.getOrDefault(position, 0) + 1);
        }

        stats.put("totalTrackers", trackerCache.size());
        stats.put("activeTrackers", activeTrackers);
        stats. put("byPosition", positionCount);

        return stats;
    }

    // ============ Position Management ============

    /**
     * 모든 추적기의 위치를 한 위치로 변경
     */
    public void setAllTrackersPosition(TrackerPosition position) {
        if (position == null) {
            return;
        }

        for (QuestTracker tracker : trackerCache.values()) {
            tracker.setPosition(position);
        }
    }

    /**
     * 위치별 추적기 개수 반환
     */
    public int getTrackerCountByPosition(TrackerPosition position) {
        if (position == null) {
            return 0;
        }

        return (int) trackerCache.values(). stream()
            .filter(t -> t.getPosition() == position)
            .count();
    }

    /**
     * 위치별 추적기 목록
     */
    public List<QuestTracker> getTrackersByPosition(TrackerPosition position) {
        if (position == null) {
            return new ArrayList<>();
        }

        return trackerCache.values().stream()
            .filter(t -> t.getPosition() == position)
            . toList();
    }

    // ============ Bulk Operations ============

    /**
     * 모든 추적기 저장
     */
    public boolean saveAll() {
        try {
            for (Map.Entry<UUID, QuestTracker> entry : trackerCache.entrySet()) {
                dataManager.savePlayerTracker(entry.getKey(), entry.getValue());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 플레이어 추적기 삭제
     */
    public boolean deleteTracker(UUID playerUUID) {
        try {
            trackerCache.remove(playerUUID);
            dataManager.deletePlayerTracker(playerUUID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 비활성 추적기 정리
     */
    public int cleanupInactiveTrackers() {
        int cleaned = 0;
        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, QuestTracker> entry : trackerCache.entrySet()) {
            if (! entry.getValue().isEnabled()) {
                toRemove.add(entry.getKey());
                cleaned++;
            }
        }

        toRemove.forEach(trackerCache::remove);
        return cleaned;
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 퀘스트 추적 관리자 상태 ===§r\n");
        sb.append("§7초기화: ").append(initialized ?  "§a완료" : "§c미완료").append("\n");
        sb.append("§7전체 추적기: §f").append(trackerCache. size()).append("\n");

        long activeTrackers = trackerCache.values().stream()
            .filter(QuestTracker::isEnabled)
            .count();
        sb. append("§7활성 추적기: §f").append(activeTrackers). append("\n");

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
        info. put("cachedTrackers", trackerCache. size());

        long activeTrackers = trackerCache.values().stream()
            .filter(QuestTracker::isEnabled)
            .count();
        info.put("activeTrackers", activeTrackers);

        return info;
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        trackerCache.clear();
    }

    /**
     * 모든 추적기 반환
     */
    public Map<UUID, QuestTracker> getAllTrackers() {
        return new HashMap<>(trackerCache);
    }
}