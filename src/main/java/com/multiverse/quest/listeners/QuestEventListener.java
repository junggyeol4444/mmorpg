package com.multiverse.quest.listeners;

import com.multiverse. quest.models.*;
import com.multiverse.quest.managers.QuestDataManager;
import com.multiverse.quest.events.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event. EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.   Bukkit;
import java.util.*;

/**
 * 퀨스트 이벤트 리스너
 * 커스텀 퀨스트 이벤트를 감지하고 처리합니다.
 */
public class QuestEventListener implements Listener {
    private final QuestDataManager questDataManager;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     */
    public QuestEventListener(QuestDataManager questDataManager) {
        this.questDataManager = questDataManager;
    }

    // ============ Quest Accepted Events ============

    /**
     * 퀨스트 수락 이벤트 감지
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onQuestAccepted(QuestAcceptedEvent event) {
        Player player = event.getPlayer();
        String questId = event.getQuestId();

        if (player == null || questId == null) {
            return;
        }

        Quest quest = questDataManager.getQuest(questId);
        if (quest == null) {
            return;
        }

        // 퀨스트 알림
        questDataManager.getNotificationManager().notifyQuestAccepted(player, quest. getName());

        // 로깅
        Bukkit.getLogger().info(player.getName() + "이 퀨스트를 수락했습니다: " + questId);
    }

    /**
     * 퀨스트 완료 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuestCompleted(QuestCompletedEvent event) {
        Player player = event.getPlayer();
        String questId = event.getQuestId();
        long completionTime = event.getCompletionTime();

        if (player == null || questId == null) {
            return;
        }

        Quest quest = questDataManager.getQuest(questId);
        if (quest == null) {
            return;
        }

        // 보상 지급
        if (quest.getRewards() != null && !quest.getRewards().isEmpty()) {
            questDataManager.giveRewards(player, quest.getRewards());
        }

        // 일일/주간 완료 기록
        UUID playerUUID = player.getUniqueId();
        if (quest.getType() == QuestType.  DAILY) {
            questDataManager.recordDailyQuestCompletion(playerUUID, questId);
        } else if (quest.getType() == QuestType.WEEKLY) {
            questDataManager.recordWeeklyQuestCompletion(playerUUID, questId);
        }

        // 퀨스트 알림
        questDataManager.getNotificationManager().notifyQuestCompleted(player, quest.getName());

        // 로깅
        Bukkit.getLogger(). info(player.getName() + "이 퀨스트를 완료했습니다: " + questId);

        // 다음 체인 퀨스트 확인 및 시작
        if (quest.getNextQuest() != null && ! quest.getNextQuest().isEmpty()) {
            String nextQuestId = quest.getNextQuest();
            if (questDataManager.canAcceptQuest(player, nextQuestId)) {
                questDataManager.acceptQuest(player, nextQuestId);
            }
        }
    }

    /**
     * 퀨스트 포기 이벤트 감지
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onQuestAbandoned(QuestAbandonedEvent event) {
        Player player = event.getPlayer();
        String questId = event.getQuestId();

        if (player == null || questId == null) {
            return;
        }

        Quest quest = questDataManager.getQuest(questId);
        if (quest == null) {
            return;
        }

        // 퀨스트 알림
        questDataManager.getNotificationManager().notifyQuestAbandoned(player, quest.getName());

        // 로깅
        Bukkit.getLogger().info(player.getName() + "이 퀨스트를 포기했습니다: " + questId);
    }

    /**
     * 퀨스트 실패 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuestFailed(QuestFailedEvent event) {
        Player player = event.getPlayer();
        String questId = event. getQuestId();
        String reason = event.getReason();

        if (player == null || questId == null) {
            return;
        }

        Quest quest = questDataManager.getQuest(questId);
        if (quest == null) {
            return;
        }

        // 퀨스트 알림
        questDataManager.getNotificationManager().notifyQuestFailed(player, quest.getName(), reason);

        // 로깅
        Bukkit.getLogger().info(player.getName() + "의 퀨스트 실패: " + questId + " (사유: " + reason + ")");
    }

    // ============ Objective Events ============

    /**
     * 목표 시작 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onObjectiveStarted(ObjectiveStartedEvent event) {
        Player player = event.getPlayer();
        String objectiveName = event.getObjectiveName();

        if (player == null || objectiveName == null) {
            return;
        }

        questDataManager.getNotificationManager(). notifyObjectiveStarted(player, objectiveName);
    }

    /**
     * 목표 완료 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onObjectiveCompleted(ObjectiveCompletedEvent event) {
        Player player = event.getPlayer();
        String objectiveName = event.getObjectiveName();

        if (player == null || objectiveName == null) {
            return;
        }

        questDataManager.getNotificationManager(). notifyObjectiveCompleted(player, objectiveName);

        Bukkit.getLogger().info(player.getName() + "이 목표를 완료했습니다: " + objectiveName);
    }

    /**
     * 목표 실패 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onObjectiveFailed(ObjectiveFailedEvent event) {
        Player player = event.getPlayer();
        String objectiveName = event.getObjectiveName();
        String reason = event.getReason();

        if (player == null || objectiveName == null) {
            return;
        }

        player.sendMessage("§c목표 실패: " + objectiveName);
        if (reason != null && !reason. isEmpty()) {
            player.sendMessage("§7사유: §f" + reason);
        }

        Bukkit.getLogger().info(player.getName() + "의 목표 실패: " + objectiveName);
    }

    // ============ Reward Events ============

    /**
     * 보상 획득 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onRewardReceived(RewardReceivedEvent event) {
        Player player = event.getPlayer();
        String rewardDescription = event.getRewardDescription();

        if (player == null || rewardDescription == null) {
            return;
        }

        questDataManager.getNotificationManager(). notifyRewardReceived(player, rewardDescription);

        Bukkit.getLogger(). info(player.getName() + "이 보상을 받았습니다: " + rewardDescription);
    }

    /**
     * 보상 실패 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onRewardFailed(RewardFailedEvent event) {
        Player player = event.getPlayer();
        String reason = event.getReason();

        if (player == null) {
            return;
        }

        player.sendMessage("§c보상 지급 실패");
        if (reason != null && !reason.isEmpty()) {
            player.sendMessage("§7사유: §f" + reason);
        }

        Bukkit.getLogger().info(player. getName() + "의 보상 지급 실패: " + reason);
    }

    // ============ Chain Events ============

    /**
     * 체인 시작 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onChainStarted(ChainStartedEvent event) {
        Player player = event.getPlayer();
        String chainId = event.getChainId();

        if (player == null || chainId == null) {
            return;
        }

        QuestChain chain = questDataManager.getChain(chainId);
        if (chain == null) {
            return;
        }

        questDataManager.getNotificationManager(). notifyChainStarted(player, chain.getName());

        Bukkit.getLogger(). info(player.getName() + "이 체인을 시작했습니다: " + chainId);
    }

    /**
     * 체인 완료 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onChainCompleted(ChainCompletedEvent event) {
        Player player = event.getPlayer();
        String chainId = event.getChainId();

        if (player == null || chainId == null) {
            return;
        }

        QuestChain chain = questDataManager.getChain(chainId);
        if (chain == null) {
            return;
        }

        questDataManager.getNotificationManager().notifyChainCompleted(player, chain.getName());

        Bukkit.getLogger().info(player.getName() + "이 체인을 완료했습니다: " + chainId);
    }

    /**
     * 체인 포기 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onChainAbandoned(ChainAbandonedEvent event) {
        Player player = event. getPlayer();
        String chainId = event.getChainId();

        if (player == null || chainId == null) {
            return;
        }

        QuestChain chain = questDataManager.getChain(chainId);
        if (chain == null) {
            return;
        }

        player.sendMessage("§c체인을 포기했습니다: " + chain.getName());

        Bukkit.getLogger().info(player. getName() + "이 체인을 포기했습니다: " + chainId);
    }

    // ============ Progress Events ============

    /**
     * 진행도 업데이트 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onProgressUpdated(ProgressUpdatedEvent event) {
        Player player = event. getPlayer();
        String questId = event.getQuestId();
        int currentProgress = event.getCurrentProgress();
        int requiredProgress = event.getRequiredProgress();

        if (player == null || questId == null) {
            return;
        }

        Quest quest = questDataManager.getQuest(questId);
        if (quest == null) {
            return;
        }

        // 진행도 알림
        questDataManager.notifyQuestProgress(player, quest. getName(), currentProgress, requiredProgress);
    }

    /**
     * 진행도 리셋 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onProgressReset(ProgressResetEvent event) {
        Player player = event.getPlayer();
        String questId = event.getQuestId();

        if (player == null || questId == null) {
            return;
        }

        player.sendMessage("§7진행도가 초기화되었습니다: " + questId);

        Bukkit.getLogger().info(player.getName() + "의 진행도가 초기화되었습니다: " + questId);
    }

    // ============ Tracker Events ============

    /**
     * 추적 시작 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTrackingStarted(TrackingStartedEvent event) {
        Player player = event.getPlayer();
        String questId = event.getQuestId();

        if (player == null || questId == null) {
            return;
        }

        Quest quest = questDataManager.getQuest(questId);
        if (quest == null) {
            return;
        }

        player.sendMessage("§a퀨스트 추적 시작: " + quest.getName());

        Bukkit.getLogger().info(player.getName() + "이 퀨스트 추적을 시작했습니다: " + questId);
    }

    /**
     * 추적 중지 이벤트 감지
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onTrackingStopped(TrackingStoppedEvent event) {
        Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        player.sendMessage("§7퀨스트 추적을 중지했습니다.");

        Bukkit.getLogger().info(player.getName() + "이 퀨스트 추적을 중지했습니다.");
    }

    // ============ Statistics Events ============

    /**
     * 통계 업데이트 이벤트 감지
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onStatisticsUpdated(StatisticsUpdatedEvent event) {
        Map<String, Object> stats = event.getStatistics();

        if (stats == null) {
            return;
        }

        // 통계 기록
        Bukkit.getLogger().fine("통계 업데이트: " + stats. toString());
    }

    // ============ Utility Methods ============

    /**
     * 플레이어별 이벤트 통계
     */
    private final Map<UUID, Map<String, Integer>> playerEventStats = new HashMap<>();

    /**
     * 플레이어 이벤트 통계 기록
     */
    private void recordEventStatistic(UUID playerUUID, String eventType) {
        Map<String, Integer> stats = playerEventStats.computeIfAbsent(playerUUID, k -> new HashMap<>());
        stats.put(eventType, stats. getOrDefault(eventType, 0) + 1);
    }

    /**
     * 플레이어 이벤트 통계 조회
     */
    public Map<String, Integer> getPlayerEventStatistics(UUID playerUUID) {
        return new HashMap<>(playerEventStats.getOrDefault(playerUUID, new HashMap<>()));
    }

    /**
     * 모든 이벤트 통계 조회
     */
    public Map<UUID, Map<String, Integer>> getAllEventStatistics() {
        return new HashMap<>(playerEventStats);
    }

    /**
     * 이벤트 통계 초기화
     */
    public void clearEventStatistics() {
        playerEventStats.clear();
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 퀨스트 이벤트 리스너 상태 ===§r\n");
        sb.append("§7추적 중인 플레이어: §f"). append(playerEventStats.size()).append("\n");

        int totalEvents = playerEventStats.values().stream()
            .mapToInt(m -> m.values().stream().mapToInt(Integer::intValue).sum())
            .sum();
        sb.append("§7총 이벤트: §f").append(totalEvents).append("\n");

        return sb.toString();
    }

    // ============ Getters ============

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }
}