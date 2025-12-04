package com.multiverse.quest.managers;

import com.multiverse. quest.models.*;
import com.multiverse.quest.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * 퀘스트 데이터 관리자
 * 모든 퀘스트 관련 데이터를 통합 관리합니다. 
 */
public class QuestDataManager {
    private final DataManager dataManager;
    private final QuestManager questManager;
    private final QuestProgressManager progressManager;
    private final QuestChainManager chainManager;
    private final RewardManager rewardManager;
    private final DailyWeeklyManager dailyWeeklyManager;
    private final NotificationManager notificationManager;
    private boolean initialized;

    /**
     * 생성자
     * @param dataManager 데이터 관리자
     */
    public QuestDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        this.questManager = new QuestManager(dataManager);
        this.progressManager = new QuestProgressManager(dataManager);
        this.chainManager = new QuestChainManager(dataManager);
        this.rewardManager = new RewardManager(dataManager);
        this.dailyWeeklyManager = new DailyWeeklyManager(dataManager);
        this.notificationManager = new NotificationManager();
        this.initialized = false;
    }

    // ============ Initialization ============

    /**
     * 데이터 관리자 초기화
     */
    public boolean initialize() {
        try {
            if (!questManager.initialize()) {
                return false;
            }
            if (!progressManager.initialize()) {
                return false;
            }
            if (! chainManager.initialize()) {
                return false;
            }
            if (!rewardManager.initialize()) {
                return false;
            }
            if (!dailyWeeklyManager.initialize()) {
                return false;
            }
            if (!notificationManager.initialize()) {
                return false;
            }

            initialized = true;
            Bukkit.getLogger().info("퀨스트 데이터 관리자 초기화 완료");
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().warning("퀨스트 데이터 관리자 초기화 실패: " + e.getMessage());
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
            questManager.shutdown();
            progressManager.shutdown();
            chainManager. shutdown();
            rewardManager. shutdown();
            dailyWeeklyManager.shutdown();
            notificationManager.shutdown();
            initialized = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Quest Management Delegation ============

    /**
     * 퀨스트 조회
     */
    public Quest getQuest(String questId) {
        return questManager.getQuest(questId);
    }

    /**
     * 모든 퀨스트 조회
     */
    public List<Quest> getAllQuests() {
        return questManager.getAllQuests();
    }

    /**
     * 퀨스트 생성
     */
    public boolean createQuest(Quest quest) {
        return questManager.createQuest(quest);
    }

    /**
     * 퀨스트 수정
     */
    public boolean updateQuest(Quest quest) {
        return questManager.updateQuest(quest);
    }

    /**
     * 퀨스트 삭제
     */
    public boolean deleteQuest(String questId) {
        return questManager. deleteQuest(questId);
    }

    /**
     * 플레이어가 퀨스트를 수락할 수 있는지 확인
     */
    public boolean canAcceptQuest(Player player, String questId) {
        return questManager.canAcceptQuest(player, questId);
    }

    // ============ Quest Progress Delegation ============

    /**
     * 플레이어 퀨스트 수락
     */
    public boolean acceptQuest(Player player, String questId) {
        if (!canAcceptQuest(player, questId)) {
            return false;
        }

        if (progressManager.acceptQuest(player, questId)) {
            notificationManager.notifyQuestAccepted(player, questId);
            return true;
        }
        return false;
    }

    /**
     * 플레이어 퀨스트 완료
     */
    public boolean completeQuest(Player player, String questId) {
        if (progressManager.completeQuest(player, questId)) {
            notificationManager.notifyQuestCompleted(player, questId);
            
            // 보상 지급
            Quest quest = getQuest(questId);
            if (quest != null && quest.getRewards() != null) {
                rewardManager.giveRewards(player, quest. getRewards());
            }

            return true;
        }
        return false;
    }

    /**
     * 플레이어 퀨스트 포기
     */
    public boolean abandonQuest(Player player, String questId) {
        if (progressManager.abandonQuest(player, questId)) {
            notificationManager.notifyQuestAbandoned(player, questId);
            return true;
        }
        return false;
    }

    /**
     * 플레이어 퀨스트 실패
     */
    public boolean failQuest(Player player, String questId, String reason) {
        if (progressManager.failQuest(player, questId, reason)) {
            notificationManager.notifyQuestFailed(player, questId, reason);
            return true;
        }
        return false;
    }

    /**
     * 플레이어 진행 중인 퀨스트 조회
     */
    public List<PlayerQuest> getPlayerInProgressQuests(UUID playerUUID) {
        return progressManager.getInProgressQuests(playerUUID);
    }

    /**
     * 플레이어 완료된 퀨스트 조회
     */
    public List<PlayerQuest> getPlayerCompletedQuests(UUID playerUUID) {
        return progressManager.getCompletedQuests(playerUUID);
    }

    // ============ Quest Chain Delegation ============

    /**
     * 체인 조회
     */
    public QuestChain getChain(String chainId) {
        return chainManager.getChain(chainId);
    }

    /**
     * 모든 체인 조회
     */
    public List<QuestChain> getAllChains() {
        return chainManager.getAllChains();
    }

    /**
     * 체인 생성
     */
    public boolean createChain(QuestChain chain) {
        return chainManager.createChain(chain);
    }

    /**
     * 체인 수정
     */
    public boolean updateChain(QuestChain chain) {
        return chainManager.updateChain(chain);
    }

    /**
     * 체인 삭제
     */
    public boolean deleteChain(String chainId) {
        return chainManager. deleteChain(chainId);
    }

    /**
     * 플레이어 체인 시작
     */
    public boolean startChain(Player player, String chainId) {
        if (chainManager.startChain(player, chainId)) {
            notificationManager.notifyChainStarted(player, chainId);
            return true;
        }
        return false;
    }

    /**
     * 플레이어 체인 완료
     */
    public boolean completeChain(Player player, String chainId) {
        if (chainManager.completeChain(player, chainId)) {
            notificationManager.notifyChainCompleted(player, chainId);
            return true;
        }
        return false;
    }

    /**
     * 플레이어 진행 중인 체인 조회
     */
    public List<String> getPlayerChains(UUID playerUUID) {
        return chainManager.getPlayerChains(playerUUID);
    }

    // ============ Reward Delegation ============

    /**
     * 플레이어에게 보상 지급
     */
    public boolean giveReward(Player player, QuestReward reward) {
        return rewardManager.giveReward(player, reward);
    }

    /**
     * 플레이어에게 여러 보상 지급
     */
    public boolean giveRewards(Player player, List<QuestReward> rewards) {
        return rewardManager.giveRewards(player, rewards);
    }

    /**
     * 보상 미리보기
     */
    public void previewRewards(Player player, List<QuestReward> rewards) {
        rewardManager.previewRewards(player, rewards);
    }

    // ============ Daily/Weekly Delegation ============

    /**
     * 플레이어가 일일 퀨스트를 수락할 수 있는지 확인
     */
    public boolean canAcceptDailyQuest(UUID playerUUID, String questId) {
        return dailyWeeklyManager.canAcceptDailyQuest(playerUUID, questId);
    }

    /**
     * 플레이어가 주간 퀨스트를 수락할 수 있는지 확인
     */
    public boolean canAcceptWeeklyQuest(UUID playerUUID, String questId) {
        return dailyWeeklyManager. canAcceptWeeklyQuest(playerUUID, questId);
    }

    /**
     * 일일 퀨스트 완료 기록
     */
    public boolean recordDailyQuestCompletion(UUID playerUUID, String questId) {
        return dailyWeeklyManager.recordDailyQuestCompletion(playerUUID, questId);
    }

    /**
     * 주간 퀨스트 완료 기록
     */
    public boolean recordWeeklyQuestCompletion(UUID playerUUID, String questId) {
        return dailyWeeklyManager.recordWeeklyQuestCompletion(playerUUID, questId);
    }

    /**
     * 모든 플레이어 일일 퀨스트 리셋
     */
    public int resetAllDailyQuests() {
        return dailyWeeklyManager.resetAllDailyQuests();
    }

    /**
     * 모든 플레이어 주간 퀨스트 리셋
     */
    public int resetAllWeeklyQuests() {
        return dailyWeeklyManager.resetAllWeeklyQuests();
    }

    // ============ Notification Delegation ============

    /**
     * 퀨스트 진행도 알림
     */
    public void notifyQuestProgress(Player player, String questName, int current, int required) {
        notificationManager. notifyQuestProgress(player, questName, current, required);
    }

    /**
     * 목표 완료 알림
     */
    public void notifyObjectiveCompleted(Player player, String objectiveName) {
        notificationManager.notifyObjectiveCompleted(player, objectiveName);
    }

    /**
     * 보상 획득 알림
     */
    public void notifyRewardReceived(Player player, String rewardDescription) {
        notificationManager. notifyRewardReceived(player, rewardDescription);
    }

    /**
     * 모든 온라인 플레이어에게 알림 처리
     */
    public void processAllNotifications() {
        notificationManager.processAllNotifications();
    }

    // ============ Statistics ============

    /**
     * 종합 통계 반환
     */
    public Map<String, Object> getComprehensiveStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        stats.put("quests", questManager.getGlobalStatistics());
        stats.put("chains", chainManager.getGlobalChainStatistics());
        stats. put("rewards", rewardManager. getGlobalRewardStatistics());
        stats. put("dailyWeekly", dailyWeeklyManager.getGlobalStatistics());
        stats.put("notifications", notificationManager.getNotificationStatistics());

        return stats;
    }

    /**
     * 플레이어 종합 통계 반환
     */
    public Map<String, Object> getPlayerComprehensiveStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();

        stats.put("quests", progressManager.getPlayerStatistics(playerUUID));
        stats.put("chains", chainManager.getPlayerChainStatistics(playerUUID));
        stats.put("rewards", rewardManager.getPlayerRewardStatistics(playerUUID));
        stats.put("dailyWeekly", dailyWeeklyManager.getPlayerStatistics(playerUUID));

        return stats;
    }

    // ============ Bulk Operations ============

    /**
     * 모든 데이터 저장
     */
    public boolean saveAll() {
        try {
            questManager.saveAll();
            progressManager.saveAll();
            chainManager.saveAll();
            rewardManager.saveAll();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 모든 데이터 리로드
     */
    public boolean reloadAll() {
        try {
            questManager.reloadAll();
            chainManager.reloadAll();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 플레이어 데이터 초기화
     */
    public boolean resetPlayerData(UUID playerUUID) {
        try {
            progressManager.deletePlayerAllQuests(playerUUID);
            chainManager.resetPlayerChains(playerUUID);
            rewardManager.resetPlayerRewardHistory(playerUUID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Manager Accessors ============

    /**
     * QuestManager 반환
     */
    public QuestManager getQuestManager() {
        return questManager;
    }

    /**
     * QuestProgressManager 반환
     */
    public QuestProgressManager getProgressManager() {
        return progressManager;
    }

    /**
     * QuestChainManager 반환
     */
    public QuestChainManager getChainManager() {
        return chainManager;
    }

    /**
     * RewardManager 반환
     */
    public RewardManager getRewardManager() {
        return rewardManager;
    }

    /**
     * DailyWeeklyManager 반환
     */
    public DailyWeeklyManager getDailyWeeklyManager() {
        return dailyWeeklyManager;
    }

    /**
     * NotificationManager 반환
     */
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6========== 퀨스트 데이터 관리자 상태 ==========§r\n");
        sb.append("\n"). append(questManager.getStatusInfo()).append("\n");
        sb.append(progressManager.getStatusInfo()).append("\n");
        sb.append(chainManager.getStatusInfo()).append("\n");
        sb.append(rewardManager.getStatusInfo()).append("\n");
        sb.append(dailyWeeklyManager.getStatusInfo()).append("\n");
        sb.append(notificationManager.getStatusInfo()). append("\n");
        sb.append("§6==============================================§r");
        return sb.toString();
    }

    // ============ Cache Information ============

    /**
     * 캐시 정보 반환
     */
    public Map<String, Object> getCacheInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("quests", questManager.getCacheInfo());
        info.put("progress", progressManager.getCacheInfo());
        info.put("chains", chainManager.getCacheInfo());
        info.put("rewards", rewardManager.getCacheInfo());
        info.put("dailyWeekly", dailyWeeklyManager.getCacheInfo());
        info.put("notifications", notificationManager.getCacheInfo());
        return info;
    }

    /**
     * 모든 캐시 초기화
     */
    public void clearAllCache() {
        questManager. clearCache();
        progressManager.clearCache();
        chainManager.clearCache();
        rewardManager.clearCache();
        dailyWeeklyManager.clearCache();
        notificationManager.clearCache();
    }
}