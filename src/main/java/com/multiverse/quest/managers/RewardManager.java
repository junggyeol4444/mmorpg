package com.multiverse.quest.managers;

import com.multiverse. quest.models.*;
import com.multiverse.quest.rewards.*;
import com.multiverse.quest.data.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * 보상 관리자
 * 퀘스트 보상의 지급 및 관리를 담당합니다.
 */
public class RewardManager {
    private final DataManager dataManager;
    private final Map<String, RewardHandler> rewardHandlers;
    private final Map<UUID, Map<String, Long>> playerRewardHistory;
    private boolean initialized;

    /**
     * 생성자
     * @param dataManager 데이터 관리자
     */
    public RewardManager(DataManager dataManager) {
        this. dataManager = dataManager;
        this.rewardHandlers = new HashMap<>();
        this.playerRewardHistory = new HashMap<>();
        this.initialized = false;

        // 기본 보상 핸들러 등록
        registerDefaultHandlers();
    }

    // ============ Initialization ============

    /**
     * 보상 관리자 초기화
     */
    public boolean initialize() {
        try {
            initialized = true;
            Bukkit.getLogger().info("보상 관리자 초기화 완료");
            return true;
        } catch (Exception e) {
            Bukkit.getLogger().warning("보상 관리자 초기화 실패: " + e.getMessage());
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
            rewardHandlers.clear();
            playerRewardHistory. clear();
            initialized = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Default Handlers Registration ============

    /**
     * 기본 보상 핸들러 등록
     */
    private void registerDefaultHandlers() {
        registerRewardHandler("EXPERIENCE", new ExperienceReward());
        registerRewardHandler("MONEY", new MoneyReward());
        registerRewardHandler("ITEM", new ItemReward());
        registerRewardHandler("COMMAND", new CommandReward());
    }

    // ============ Reward Handler Management ============

    /**
     * 보상 핸들러 등록
     */
    public void registerRewardHandler(String rewardType, RewardHandler handler) {
        if (rewardType != null && handler != null) {
            rewardHandlers.put(rewardType, handler);
            Bukkit.getLogger().info("보상 핸들러 등록: " + rewardType);
        }
    }

    /**
     * 보상 핸들러 조회
     */
    public RewardHandler getRewardHandler(String rewardType) {
        return rewardHandlers.getOrDefault(rewardType, null);
    }

    /**
     * 모든 보상 핸들러 조회
     */
    public Map<String, RewardHandler> getAllRewardHandlers() {
        return new HashMap<>(rewardHandlers);
    }

    /**
     * 보상 핸들러 제거
     */
    public void unregisterRewardHandler(String rewardType) {
        rewardHandlers.remove(rewardType);
    }

    /**
     * 보상 핸들러 존재 여부 확인
     */
    public boolean hasRewardHandler(String rewardType) {
        return rewardHandlers.containsKey(rewardType);
    }

    // ============ Reward Distribution ============

    /**
     * 플레이어에게 보상 지급
     */
    public boolean giveReward(Player player, QuestReward reward) {
        if (player == null || reward == null) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();

        try {
            String rewardType = reward.getType();
            RewardHandler handler = getRewardHandler(rewardType);

            if (handler == null) {
                player.sendMessage("§c보상 타입을 찾을 수 없습니다: " + rewardType);
                return false;
            }

            if (!handler.canGiveReward(player, playerUUID)) {
                player. sendMessage("§c보상을 받을 수 없습니다.");
                return false;
            }

            // 보상 지급
            boolean success = handler.giveReward(player, playerUUID);

            if (success) {
                // 이력 기록
                recordRewardHistory(playerUUID, rewardType);
            }

            return success;
        } catch (Exception e) {
            player.sendMessage("§c보상 지급 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 플레이어에게 여러 보상 지급
     */
    public boolean giveRewards(Player player, List<QuestReward> rewards) {
        if (player == null || rewards == null || rewards.isEmpty()) {
            return false;
        }

        boolean allSuccess = true;

        for (QuestReward reward : rewards) {
            if (! giveReward(player, reward)) {
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    /**
     * 보상 미리보기
     */
    public void previewReward(Player player, QuestReward reward) {
        if (player == null || reward == null) {
            return;
        }

        UUID playerUUID = player. getUniqueId();
        RewardHandler handler = getRewardHandler(reward.getType());

        if (handler != null) {
            handler.previewReward(player, playerUUID);
        }
    }

    /**
     * 모든 보상 미리보기
     */
    public void previewRewards(Player player, List<QuestReward> rewards) {
        if (player == null || rewards == null) {
            return;
        }

        player.sendMessage("§6=== 보상 목록 ===§r");
        for (QuestReward reward : rewards) {
            previewReward(player, reward);
        }
    }

    // ============ Reward Validation ============

    /**
     * 보상이 유효한지 확인
     */
    public boolean isRewardValid(QuestReward reward) {
        if (reward == null) {
            return false;
        }

        RewardHandler handler = getRewardHandler(reward.getType());
        if (handler == null) {
            return false;
        }

        return handler.isValid();
    }

    /**
     * 모든 보상이 유효한지 확인
     */
    public boolean areRewardsValid(List<QuestReward> rewards) {
        if (rewards == null || rewards. isEmpty()) {
            return false;
        }

        for (QuestReward reward : rewards) {
            if (!isRewardValid(reward)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 플레이어가 보상을 받을 수 있는지 확인
     */
    public boolean canGiveReward(Player player, QuestReward reward) {
        if (player == null || reward == null) {
            return false;
        }

        UUID playerUUID = player.getUniqueId();
        RewardHandler handler = getRewardHandler(reward.getType());

        if (handler == null) {
            return false;
        }

        return handler.canGiveReward(player, playerUUID);
    }

    // ============ Reward History ============

    /**
     * 보상 지급 이력 기록
     */
    private void recordRewardHistory(UUID playerUUID, String rewardType) {
        Map<String, Long> history = playerRewardHistory
            .computeIfAbsent(playerUUID, k -> new HashMap<>());
        
        long count = history.getOrDefault(rewardType, 0L);
        history.put(rewardType, count + 1);
    }

    /**
     * 플레이어 보상 이력 조회
     */
    public Map<String, Long> getPlayerRewardHistory(UUID playerUUID) {
        if (playerUUID == null) {
            return new HashMap<>();
        }

        Map<String, Long> history = playerRewardHistory.get(playerUUID);
        return history != null ? new HashMap<>(history) : new HashMap<>();
    }

    /**
     * 특정 타입의 보상 지급 횟수
     */
    public long getRewardCount(UUID playerUUID, String rewardType) {
        Map<String, Long> history = getPlayerRewardHistory(playerUUID);
        return history. getOrDefault(rewardType, 0L);
    }

    /**
     * 총 보상 지급 횟수
     */
    public long getTotalRewardCount(UUID playerUUID) {
        Map<String, Long> history = getPlayerRewardHistory(playerUUID);
        return history.values().stream().mapToLong(Long::longValue).sum();
    }

    // ============ Reward Value Calculation ============

    /**
     * 보상 총 가치 계산
     */
    public double calculateTotalRewardValue(List<QuestReward> rewards) {
        if (rewards == null || rewards.isEmpty()) {
            return 0;
        }

        double totalValue = 0;

        for (QuestReward reward : rewards) {
            RewardHandler handler = getRewardHandler(reward.getType());
            if (handler != null) {
                totalValue += handler.getRewardValue();
            }
        }

        return totalValue;
    }

    /**
     * 개별 보상 가치 계산
     */
    public double calculateRewardValue(QuestReward reward) {
        if (reward == null) {
            return 0;
        }

        RewardHandler handler = getRewardHandler(reward.getType());
        if (handler == null) {
            return 0;
        }

        return handler.getRewardValue();
    }

    // ============ Statistics ============

    /**
     * 플레이어 보상 통계
     */
    public Map<String, Object> getPlayerRewardStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();

        Map<String, Long> history = getPlayerRewardHistory(playerUUID);
        stats.put("playerUUID", playerUUID);
        stats.put("totalRewardsReceived", getTotalRewardCount(playerUUID));
        stats.put("rewardsByType", new HashMap<>(history));

        return stats;
    }

    /**
     * 전체 보상 통계
     */
    public Map<String, Object> getGlobalRewardStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();

        long totalRewards = 0;
        Map<String, Long> globalRewardCount = new HashMap<>();

        for (Map<String, Long> history : playerRewardHistory.values()) {
            for (Map.Entry<String, Long> entry : history.entrySet()) {
                globalRewardCount.put(
                    entry.getKey(),
                    globalRewardCount.getOrDefault(entry.getKey(), 0L) + entry.getValue()
                );
                totalRewards += entry.getValue();
            }
        }

        stats.put("totalRewardsDistributed", totalRewards);
        stats.put("playersRewarded", playerRewardHistory.size());
        stats.put("rewardsByType", globalRewardCount);
        stats.put("registeredHandlers", rewardHandlers.size());

        return stats;
    }

    /**
     * 보상 핸들러 통계
     */
    public Map<String, Object> getRewardHandlerStatistics(String rewardType) {
        Map<String, Object> stats = new LinkedHashMap<>();

        RewardHandler handler = getRewardHandler(rewardType);
        if (handler != null) {
            stats.putAll(handler.getStatistics());
        }

        return stats;
    }

    // ============ Search & Filter ============

    /**
     * 특정 타입의 보상 찾기
     */
    public List<QuestReward> filterRewardsByType(List<QuestReward> rewards, String type) {
        if (rewards == null || type == null) {
            return new ArrayList<>();
        }

        return rewards.stream()
            .filter(r -> r.getType().equals(type))
            .toList();
    }

    /**
     * 특정 값 범위의 보상 찾기
     */
    public List<QuestReward> filterRewardsByValue(List<QuestReward> rewards, double minValue, double maxValue) {
        if (rewards == null) {
            return new ArrayList<>();
        }

        return rewards.stream()
            .filter(r -> {
                double value = calculateRewardValue(r);
                return value >= minValue && value <= maxValue;
            })
            .toList();
    }

    // ============ Bulk Operations ============

    /**
     * 모든 보상 핸들러 저장
     */
    public boolean saveAll() {
        try {
            for (RewardHandler handler : rewardHandlers. values()) {
                // 각 핸들러별 저장 로직
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 플레이어 보상 이력 초기화
     */
    public boolean resetPlayerRewardHistory(UUID playerUUID) {
        try {
            playerRewardHistory. remove(playerUUID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 모든 보상 이력 초기화
     */
    public boolean resetAllRewardHistory() {
        try {
            playerRewardHistory.clear();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============ Status Information ============

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 보상 관리자 상태 ===§r\n");
        sb.append("§7초기화: ").append(initialized ? "§a완료" : "§c미완료").append("\n");
        sb.append("§7등록된 핸들러: §f").append(rewardHandlers. size()).append("\n");
        
        for (String type : rewardHandlers.keySet()) {
            sb.append("§7  - §f").append(type).append("\n");
        }
        
        sb.append("§7보상 이력 기록: §f").append(playerRewardHistory.size()).append(" 명\n");

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
        info.put("registeredHandlers", rewardHandlers.size());
        info.put("playersWithHistory", playerRewardHistory.size());

        return info;
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        playerRewardHistory.clear();
    }
}