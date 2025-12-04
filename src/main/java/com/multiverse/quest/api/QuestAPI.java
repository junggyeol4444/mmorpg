package com.multiverse.quest.api;

import com.multiverse.  quest.QuestCore;
import com.multiverse.  quest.models.*;
import com.multiverse.  quest.managers.QuestDataManager;
import org.bukkit.entity.Player;
import java.util.*;

/**
 * QuestCore API
 * 다른 플러그인에서 QuestCore 기능을 사용할 수 있는 API입니다.
 */
public class QuestAPI {
    private static QuestCore plugin;
    private static QuestDataManager questDataManager;

    /**
     * API 초기화
     */
    public static void initialize(QuestCore questCorePlugin) {
        plugin = questCorePlugin;
        questDataManager = plugin.getQuestDataManager();
    }

    // ============ Quest Management ============

    /**
     * 플레이어에게 퀨스트 수락
     */
    public static boolean acceptQuest(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        return questDataManager.getQuestProgressManager().acceptQuest(player. getUniqueId(), questId);
    }

    /**
     * 플레이어 퀨스트 완료
     */
    public static boolean completeQuest(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        return questDataManager.getQuestProgressManager(). completeQuest(player.getUniqueId(), questId);
    }

    /**
     * 플레이어 퀨스트 포기
     */
    public static boolean abandonQuest(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        return questDataManager. getQuestProgressManager().abandonQuest(player.getUniqueId(), questId);
    }

    /**
     * 퀨스트 조회
     */
    public static Quest getQuest(String questId) {
        if (questId == null) {
            return null;
        }

        return questDataManager.getQuest(questId);
    }

    /**
     * 모든 퀨스트 조회
     */
    public static List<Quest> getAllQuests() {
        return questDataManager.getAllQuests();
    }

    /**
     * 특정 타입의 퀨스트 조회
     */
    public static List<Quest> getQuestsByType(String type) {
        if (type == null) {
            return new ArrayList<>();
        }

        return questDataManager.getQuestManager().getQuestsByType(type);
    }

    // ============ Player Quest Status ============

    /**
     * 플레이어의 진행 중인 퀨스트 조회
     */
    public static List<PlayerQuest> getPlayerInProgressQuests(Player player) {
        if (player == null) {
            return new ArrayList<>();
        }

        return questDataManager.getPlayerInProgressQuests(player.getUniqueId());
    }

    /**
     * 플레이어의 완료된 퀨스트 조회
     */
    public static List<PlayerQuest> getPlayerCompletedQuests(Player player) {
        if (player == null) {
            return new ArrayList<>();
        }

        return questDataManager.getPlayerCompletedQuests(player.getUniqueId());
    }

    /**
     * 플레이어가 특정 퀨스트를 수락했는지 확인
     */
    public static boolean hasAcceptedQuest(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        PlayerQuest pq = questDataManager.getPlayerQuest(player.getUniqueId(), questId);
        return pq != null;
    }

    /**
     * 플레이어가 특정 퀨스트를 완료했는지 확인
     */
    public static boolean hasCompletedQuest(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        List<PlayerQuest> completedQuests = getPlayerCompletedQuests(player);
        return completedQuests.stream()
            .anyMatch(pq -> pq.getQuestId().equals(questId));
    }

    // ============ Quest Tracking ============

    /**
     * 플레이어가 퀨스트 추적 시작
     */
    public static boolean trackQuest(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        questDataManager.getTrackerManager().setTrackedQuest(player.getUniqueId(), questId);
        return true;
    }

    /**
     * 플레이어가 추적 중인 퀨스트 조회
     */
    public static String getTrackedQuest(Player player) {
        if (player == null) {
            return null;
        }

        return questDataManager.getTrackerManager().getTrackedQuestId(player.getUniqueId());
    }

    /**
     * 플레이어 퀨스트 추적 중지
     */
    public static boolean untrackQuest(Player player) {
        if (player == null) {
            return false;
        }

        questDataManager.getTrackerManager(). setTrackedQuest(player.getUniqueId(), null);
        return true;
    }

    // ============ Objective Progress ============

    /**
     * 목표 진행도 업데이트
     */
    public static boolean updateObjectiveProgress(Player player, String questId, String objectiveId, int progress) {
        if (player == null || questId == null || objectiveId == null) {
            return false;
        }

        PlayerQuest pq = questDataManager.getPlayerQuest(player. getUniqueId(), questId);
        if (pq == null) {
            return false;
        }

        pq. setObjectiveProgress(objectiveId, progress);
        return true;
    }

    /**
     * 목표 진행도 조회
     */
    public static int getObjectiveProgress(Player player, String questId, String objectiveId) {
        if (player == null || questId == null || objectiveId == null) {
            return 0;
        }

        PlayerQuest pq = questDataManager.getPlayerQuest(player.getUniqueId(), questId);
        if (pq == null) {
            return 0;
        }

        return pq.getObjectiveProgress(objectiveId);
    }

    // ============ Rewards ============

    /**
     * 플레이어에게 보상 지급
     */
    public static boolean grantReward(Player player, QuestReward reward) {
        if (player == null || reward == null) {
            return false;
        }

        questDataManager.getRewardManager().grantReward(player, reward);
        return true;
    }

    /**
     * 플레이어에게 경험치 보상 지급
     */
    public static void grantExperienceReward(Player player, long amount) {
        if (player == null) {
            return;
        }

        QuestReward reward = new QuestReward("experience", amount, "");
        grantReward(player, reward);
    }

    /**
     * 플레이어에게 돈 보상 지급
     */
    public static void grantMoneyReward(Player player, long amount) {
        if (player == null) {
            return;
        }

        QuestReward reward = new QuestReward("money", amount, "");
        grantReward(player, reward);
    }

    // ============ Daily/Weekly Quests ============

    /**
     * 플레이어가 일일 퀨스트를 수락할 수 있는지 확인
     */
    public static boolean canAcceptDailyQuest(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        return questDataManager.canAcceptDailyQuest(player.getUniqueId(), questId);
    }

    /**
     * 플레이어가 주간 퀨스트를 수락할 수 있는지 확인
     */
    public static boolean canAcceptWeeklyQuest(Player player, String questId) {
        if (player == null || questId == null) {
            return false;
        }

        return questDataManager.canAcceptWeeklyQuest(player.getUniqueId(), questId);
    }

    // ============ Statistics ============

    /**
     * 플레이어의 완료한 퀨스트 개수
     */
    public static int getCompletedQuestCount(Player player) {
        if (player == null) {
            return 0;
        }

        return getPlayerCompletedQuests(player).size();
    }

    /**
     * 플레이어의 진행 중인 퀨스트 개수
     */
    public static int getInProgressQuestCount(Player player) {
        if (player == null) {
            return 0;
        }

        return getPlayerInProgressQuests(player).size();
    }

    /**
     * 플레이어의 퀨스트 완료율
     */
    public static double getQuestCompletionRate(Player player) {
        if (player == null) {
            return 0.0;
        }

        List<Quest> allQuests = getAllQuests();
        if (allQuests. isEmpty()) {
            return 0.0;
        }

        int completedCount = getCompletedQuestCount(player);
        return (completedCount * 100.0) / allQuests. size();
    }

    // ============ Plugin Information ============

    /**
     * QuestCore 플러그인 인스턴스 반환
     */
    public static QuestCore getPlugin() {
        return plugin;
    }

    /**
     * QuestDataManager 반환
     */
    public static QuestDataManager getQuestDataManager() {
        return questDataManager;
    }

    /**
     * API 버전 반환
     */
    public static String getAPIVersion() {
        return plugin != null ? plugin.getDescription().getVersion() : "Unknown";
    }

    /**
     * API 활성화 여부 확인
     */
    public static boolean isAPIEnabled() {
        return plugin != null && questDataManager != null;
    }

    /**
     * API 상태 정보
     */
    public static String getAPIStatus() {
        if (! isAPIEnabled()) {
            return "§cQuestCore API가 활성화되지 않았습니다.";
        }

        StringBuilder sb = new StringBuilder();
        sb. append("§6=== QuestCore API 상태 ===§r\n");
        sb.append("§7버전: §f"). append(getAPIVersion()).append("\n");
        sb.append("§7상태: §a활성화됨\n");
        sb.append("§7플러그인: §f").append(plugin.getName()).append("\n");

        return sb.toString();
    }
}