package com.multiverse.quest.managers;

import com.multiverse.  quest.models.*;
import com.multiverse.quest.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * 퀘스트 진행도 관리자
 * 플레이어의 퀘스트 진행 상태를 관리합니다.
 */
public class QuestProgressManager {
    private final DataManager dataManager;
    private final Map<UUID, Map<String, PlayerQuest>> playerQuestCache;
    private boolean initialized;

    /**
     * 생성자
     * @param dataManager 데이터 관리자
     */
    public QuestProgressManager(DataManager dataManager) {
        this.dataManager = dataManager;
        this.playerQuestCache = new HashMap<>();
        this.initialized = false;
    }

    // ============ Initialization ============

    /**
     * 진행도 관리자 초기화
     */
    public boolean initialize() {
        try {
            initialized = true;
            Bukkit.getLogger().info("퀘스트 진행도 관리자 초기화 완료");
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().warning("퀘스트 진행도 관리자 초기화 실패: " + e.getMessage());
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
            playerQuestCache.clear();
            initialized = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Progress Loading ============

    /**
     * 플레이어의 모든 퀘스트 진행도 로드
     */
    public void loadPlayerQuests(UUID playerUUID) {
        if (playerUUID == null) {
            return;
        }

        List<PlayerQuest> quests = dataManager.getPlayerAllQuests(playerUUID);
        Map<String, PlayerQuest> questMap = new HashMap<>();
        quests.forEach(pq -> questMap.put(pq.getQuestId(), pq));
        playerQuestCache.put(playerUUID, questMap);
    }

    /**
     * 플레이어 퀘스트 진행도 로드 (ID로)
     */
    public PlayerQuest loadPlayerQuest(UUID playerUUID, String questId) {
        if (playerUUID == null || questId == null) {
            return null;
        }

        Map<String, PlayerQuest> playerQuests = playerQuestCache. get(playerUUID);
        if (playerQuests != null && playerQuests.containsKey(questId)) {
            return playerQuests.get(questId);
        }

        PlayerQuest pq = dataManager.getPlayerQuest(playerUUID, questId);
        if (pq != null) {
            playerQuestCache.computeIfAbsent(playerUUID, k -> new HashMap<>())
                . put(questId, pq);
        }
        return pq;
    }

    // ============ Quest Progress Management ============

    /**
     * 플레이어 퀘스트 수락
     */
    public boolean acceptQuest(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();
        PlayerQuest pq = loadPlayerQuest(playerUUID, questId);

        if (pq != null) {
            player.sendMessage("§c이미 수락한 퀘스트입니다.");
            return false;
        }

        try {
            PlayerQuest newQuest = new PlayerQuest(playerUUID, questId);
            newQuest.setAcceptedTime(System.currentTimeMillis());
            
            return updatePlayerQuest(playerUUID, newQuest);
        } catch (Exception e) {
            player.sendMessage("§c퀘스트 수락 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 플레이어 퀘스트 진행도 업데이트
     */
    public boolean updateProgress(Player player, String questId, int amount) {
        if (player == null || questId == null) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();
        PlayerQuest pq = loadPlayerQuest(playerUUID, questId);

        if (pq == null) {
            player.sendMessage("§c진행 중인 퀘스트가 아닙니다.");
            return false;
        }

        try {
            // 진행도 업데이트 로직
            return updatePlayerQuest(playerUUID, pq);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 플레이어 퀘스트 완료
     */
    public boolean completeQuest(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();
        PlayerQuest pq = loadPlayerQuest(playerUUID, questId);

        if (pq == null) {
            return false;
        }

        try {
            pq.setCompletedTime(System.currentTimeMillis());
            
            return updatePlayerQuest(playerUUID, pq);
        } catch (Exception e) {
            player.sendMessage("§c퀘스트 완료 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 플레이어 퀘스트 포기
     */
    public boolean abandonQuest(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();

        try {
            Map<String, PlayerQuest> playerQuests = playerQuestCache. get(playerUUID);
            if (playerQuests != null) {
                playerQuests.remove(questId);
            }

            dataManager.deletePlayerQuest(playerUUID, questId);
            player.sendMessage("§7퀘스트를 포기했습니다: " + questId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 플레이어 퀘스트 실패
     */
    public boolean failQuest(Player player, String questId, String reason) {
        if (player == null || questId == null) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();
        PlayerQuest pq = loadPlayerQuest(playerUUID, questId);

        if (pq == null) {
            return false;
        }

        try {
            pq.setFailReason(reason);
            
            return updatePlayerQuest(playerUUID, pq);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 플레이어 퀨스트 업데이트
     */
    private boolean updatePlayerQuest(UUID playerUUID, PlayerQuest pq) {
        try {
            Map<String, PlayerQuest> playerQuests = playerQuestCache
                .computeIfAbsent(playerUUID, k -> new HashMap<>());
            playerQuests.put(pq.getQuestId(), pq);

            dataManager.savePlayerQuest(playerUUID, pq);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Progress Retrieval ============

    /**
     * 플레이어 퀘스트 진행도 조회
     */
    public PlayerQuest getPlayerQuest(UUID playerUUID, String questId) {
        return loadPlayerQuest(playerUUID, questId);
    }

    /**
     * 플레이어의 모든 퀨스트 진행도 조회
     */
    public List<PlayerQuest> getAllPlayerQuests(UUID playerUUID) {
        if (playerUUID == null) {
            return new ArrayList<>();
        }

        Map<String, PlayerQuest> playerQuests = playerQuestCache.get(playerUUID);
        if (playerQuests != null) {
            return new ArrayList<>(playerQuests. values());
        }

        return dataManager.getPlayerAllQuests(playerUUID);
    }

    /**
     * 플레이어의 진행 중인 퀨스트 조회
     */
    public List<PlayerQuest> getInProgressQuests(UUID playerUUID) {
        return getAllPlayerQuests(playerUUID). stream()
            .filter(pq -> pq.getStatus(). isActive())
            .toList();
    }

    /**
     * 플레이어의 완료된 퀨스트 조회
     */
    public List<PlayerQuest> getCompletedQuests(UUID playerUUID) {
        return getAllPlayerQuests(playerUUID).stream()
            .filter(pq -> pq.getStatus().isCompleted())
            .toList();
    }

    /**
     * 플레이어의 실패한 퀨스트 조회
     */
    public List<PlayerQuest> getFailedQuests(UUID playerUUID) {
        return getAllPlayerQuests(playerUUID).stream()
            .filter(pq -> pq.getStatus().name().equals("FAILED"))
            .toList();
    }

    /**
     * 상태별 퀨스트 조회
     */
    public List<PlayerQuest> getQuestsByStatus(UUID playerUUID, String status) {
        return getAllPlayerQuests(playerUUID).stream()
            .filter(pq -> pq.getStatus().name().equals(status))
            .toList();
    }

    // ============ Progress Validation ============

    /**
     * 플레이어가 퀨스트를 진행 중인지 확인
     */
    public boolean isQuestInProgress(UUID playerUUID, String questId) {
        PlayerQuest pq = loadPlayerQuest(playerUUID, questId);
        return pq != null && pq.getStatus().isActive();
    }

    /**
     * 플레이어가 퀨스트를 완료했는지 확인
     */
    public boolean isQuestCompleted(UUID playerUUID, String questId) {
        PlayerQuest pq = loadPlayerQuest(playerUUID, questId);
        return pq != null && pq.getStatus().isCompleted();
    }

    /**
     * 플레이어가 퀨스트를 수락했는지 확인
     */
    public boolean hasQuestAccepted(UUID playerUUID, String questId) {
        return loadPlayerQuest(playerUUID, questId) != null;
    }

    // ============ Statistics ============

    /**
     * 플레이어 퀨스트 통계
     */
    public Map<String, Object> getPlayerStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();
        List<PlayerQuest> allQuests = getAllPlayerQuests(playerUUID);

        stats. put("totalQuests", allQuests.size());
        stats.put("completedQuests", getCompletedQuests(playerUUID).size());
        stats. put("inProgressQuests", getInProgressQuests(playerUUID).size());
        stats.put("failedQuests", getFailedQuests(playerUUID).size());
        
        if (! allQuests.isEmpty()) {
            int completionRate = (getCompletedQuests(playerUUID).size() * 100) / allQuests.size();
            stats.put("completionRate", completionRate + "%");
        }

        return stats;
    }

    /**
     * 전체 진행도 통계
     */
    public Map<String, Object> getGlobalStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalPlayers", playerQuestCache.size());
        
        int totalQuests = 0;
        int totalCompleted = 0;
        
        for (Map<String, PlayerQuest> playerQuests : playerQuestCache.values()) {
            totalQuests += playerQuests.size();
            totalCompleted += (int) playerQuests.values().stream()
                .filter(pq -> pq.getStatus().isCompleted())
                .count();
        }

        stats.put("totalActiveQuests", totalQuests);
        stats.put("totalCompletedQuests", totalCompleted);

        return stats;
    }

    // ============ Time Management ============

    /**
     * 퀨스트 진행 시간 조회
     */
    public long getQuestElapsedTime(UUID playerUUID, String questId) {
        PlayerQuest pq = loadPlayerQuest(playerUUID, questId);
        if (pq == null) {
            return 0;
        }

        long acceptedTime = pq.getAcceptedTime();
        long currentTime = System.currentTimeMillis();

        return (currentTime - acceptedTime) / 1000; // 초 단위
    }

    /**
     * 퀨스트 시간 제한 확인
     */
    public boolean isQuestExpired(UUID playerUUID, String questId, long timeLimitSeconds) {
        return getQuestElapsedTime(playerUUID, questId) > timeLimitSeconds;
    }

    // ============ Bulk Operations ============

    /**
     * 모든 진행도 저장
     */
    public boolean saveAll() {
        try {
            for (Map<String, PlayerQuest> playerQuests : playerQuestCache.values()) {
                for (PlayerQuest pq : playerQuests.values()) {
                    // 저장 로직
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 플레이어 모든 진행도 삭제
     */
    public boolean deletePlayerAllQuests(UUID playerUUID) {
        try {
            playerQuestCache.remove(playerUUID);
            dataManager.deletePlayerAllQuests(playerUUID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 만료된 퀨스트 정리
     */
    public int cleanupExpiredQuests(long expirationTimeSeconds) {
        int cleaned = 0;

        for (Map<String, PlayerQuest> playerQuests : playerQuestCache.values()) {
            List<String> toRemove = new ArrayList<>();

            for (Map.Entry<String, PlayerQuest> entry : playerQuests.entrySet()) {
                PlayerQuest pq = entry.getValue();
                if (pq.getStatus(). isTerminated()) {
                    long age = (System.currentTimeMillis() - pq.getCompletedTime()) / 1000;
                    if (age > expirationTimeSeconds) {
                        toRemove.add(entry.getKey());
                        cleaned++;
                    }
                }
            }

            toRemove.forEach(playerQuests::remove);
        }

        return cleaned;
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 퀨스트 진행도 관리자 상태 ===§r\n");
        sb.append("§7초기화: ").append(initialized ? "§a완료" : "§c미완료").append("\n");
        sb.append("§7진행 중인 플레이어: §f").append(playerQuestCache. size()).append("\n");
        
        int totalActiveQuests = 0;
        for (Map<String, PlayerQuest> playerQuests : playerQuestCache.values()) {
            totalActiveQuests += playerQuests. size();
        }
        sb.append("§7전체 진행 퀨스트: §f"). append(totalActiveQuests).append("\n");

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
        info. put("cachedPlayers", playerQuestCache.size());
        
        int totalCachedQuests = 0;
        for (Map<String, PlayerQuest> playerQuests : playerQuestCache.values()) {
            totalCachedQuests += playerQuests.size();
        }
        info.put("totalCachedQuests", totalCachedQuests);

        return info;
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        playerQuestCache.clear();
    }
}