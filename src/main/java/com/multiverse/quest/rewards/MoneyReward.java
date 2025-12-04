package com.multiverse.quest.rewards;

import com.multiverse. quest.models.*;
import org.bukkit.entity.Player;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * 금화 보상 핸들러
 * 플레이어에게 금화를 지급합니다.
 * (Vault 플러그인 필요)
 */
public class MoneyReward implements RewardHandler {
    private QuestReward reward;
    private double moneyAmount;                     // 지급할 금화
    private String currencySymbol;                  // 통화 기호
    private boolean showNotification;               // 알림 표시 여부
    private Map<UUID, List<Long>> rewardHistory;   // 보상 지급 이력
    private Map<UUID, Double> totalGiven;          // 플레이어별 총 지급액
    private boolean enabled;

    /**
     * 생성자
     */
    public MoneyReward() {
        this.rewardHistory = new HashMap<>();
        this.totalGiven = new HashMap<>();
        this.currencySymbol = "$";
        this.showNotification = true;
        this.enabled = true;
    }

    // ============ Reward Distribution ============

    @Override
    public boolean giveReward(Player player, UUID playerUUID) {
        return giveReward(player, playerUUID, (int) moneyAmount);
    }

    @Override
    public boolean giveReward(Player player, UUID playerUUID, int amount) {
        if (! enabled || !  canGiveReward(player, playerUUID)) {
            return false;
        }

        try {
            onBeforeGive(player, playerUUID);

            double finalAmount = amount > 0 ? amount : moneyAmount;

            // Vault 통합 (실제 구현은 Vault API 사용)
            // EconomyProvider. addMoney(playerUUID, finalAmount);
            
            // 이력 기록
            rewardHistory.computeIfAbsent(playerUUID, k -> new ArrayList<>())
                .add(System.currentTimeMillis());
            totalGiven.put(playerUUID, totalGiven.getOrDefault(playerUUID, 0. 0) + finalAmount);

            onAfterGive(player, playerUUID);
            return true;
        } catch (Exception e) {
            onGiveFailed(player, playerUUID, e. getMessage());
            return false;
        }
    }

    @Override
    public boolean previewReward(Player player, UUID playerUUID) {
        if (player == null) {
            return false;
        }

        player.sendMessage(String.format("§6금화 보상: §f%s%. 2f", currencySymbol, moneyAmount));
        return true;
    }

    // ============ Validation ============

    @Override
    public boolean canGiveReward(Player player, UUID playerUUID) {
        if (player == null || !  enabled) {
            return false;
        }

        if (player.isDead()) {
            return false;
        }

        return checkConditions(player);
    }

    @Override
    public boolean isValid() {
        return moneyAmount > 0;
    }

    @Override
    public boolean validateRewardData() {
        return reward != null && moneyAmount > 0;
    }

    // ============ Information ============

    @Override
    public String getRewardType() {
        return "MONEY";
    }

    @Override
    public String getDescription() {
        return String.format("금화 %s%. 2f 획득", currencySymbol, moneyAmount);
    }

    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 금화 보상 ===§r\n");
        sb.append("§7보상 타입: §f금화\n");
        sb. append("§7금액: §f"). append(currencySymbol).append(String.format("%.2f", moneyAmount)).append("\n");
        sb.append("§7통화 기호: §f"). append(currencySymbol).append("\n");
        sb.append("§7알림: ").append(showNotification ? "§a표시" : "§c미표시").append("\n");
        sb.append("§7상태: ").append(enabled ? "§a활성화" : "§c비활성화").append("\n");
        return sb.toString();
    }

    @Override
    public double getRewardValue() {
        // 금화 가치는 실제 값
        return moneyAmount;
    }

    // ============ Configuration ============

    @Override
    public void initialize(QuestReward reward) {
        this.reward = reward;
        // 금화는 QuestReward에서 추출 (커스텀 구현 필요)
        // this.moneyAmount = reward.getMoney();
    }

    @Override
    public void cleanup() {
        rewardHistory.clear();
        totalGiven.clear();
    }

    // ============ Events ============

    @Override
    public void onBeforeGive(Player player, UUID playerUUID) {
        if (player != null && showNotification) {
            player. sendMessage(String.format("§a금화를 획득했습니다: §f+%s%.2f", 
                currencySymbol, moneyAmount));
        }
    }

    @Override
    public void onAfterGive(Player player, UUID playerUUID) {
        if (player != null && showNotification) {
            // 현재 금화 조회 (Vault 필요)
            player.sendMessage(String.format("§7현재 금화: §f%s%.2f", 
                currencySymbol, totalGiven.getOrDefault(playerUUID, 0.0)));
        }
    }

    @Override
    public void onGiveFailed(Player player, UUID playerUUID, String reason) {
        if (player != null) {
            player.sendMessage(String.format("§c금화 지급 실패: %s", reason));
        }
    }

    // ============ Data Management ============

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("type", getRewardType());
        data. put("money", moneyAmount);
        data.put("currencySymbol", currencySymbol);
        data.put("showNotification", showNotification);
        data.put("enabled", enabled);
        return data;
    }

    @Override
    public void deserialize(Map<String, Object> data) {
        if (data. containsKey("money")) {
            this.moneyAmount = ((Number) data.get("money")). doubleValue();
        }
        if (data.containsKey("currencySymbol")) {
            this.currencySymbol = (String) data.get("currencySymbol");
        }
        if (data.containsKey("showNotification")) {
            this.showNotification = (Boolean) data.get("showNotification");
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

        return player.isOnline() && !  player.isDead();
    }

    @Override
    public boolean checkCondition(Player player, String condition) {
        if (player == null || condition == null) {
            return false;
        }

        switch (condition.toLowerCase()) {
            case "alive":
                return !  player.isDead();
            case "online":
                return player. isOnline();
            case "has_money_api":
                return true; // Vault API 연결 여부 확인
            default:
                return true;
        }
    }

    // ============ Money Management ============

    /**
     * 플레이어 현재 금화 조회 (Vault 필요)
     */
    public double getPlayerMoney(Player player) {
        if (player == null) {
            return 0;
        }
        
        // 실제 구현: Vault API 사용
        // return EconomyProvider.getMoney(player.getUniqueId());
        return 0;
    }

    /**
     * 플레이어 금화 설정 (Vault 필요)
     */
    public boolean setPlayerMoney(Player player, double amount) {
        if (player == null || amount < 0) {
            return false;
        }

        // 실제 구현: Vault API 사용
        // EconomyProvider.setMoney(player.getUniqueId(), amount);
        return true;
    }

    // ============ Statistics ============

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("type", getRewardType());
        stats.put("moneyAmount", moneyAmount);
        stats.put("totalDistributed", totalGiven.values().stream().mapToDouble(Double::doubleValue).sum());
        stats.put("playersRewarded", totalGiven.size());
        stats.put("currencySymbol", currencySymbol);
        stats.put("showNotification", showNotification);
        stats.put("enabled", enabled);
        return stats;
    }

    @Override
    public Map<String, Object> getPlayerStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats. put("playerUUID", playerUUID);
        stats.put("totalMoneyGiven", totalGiven.getOrDefault(playerUUID, 0.0));
        
        List<Long> history = rewardHistory.getOrDefault(playerUUID, new ArrayList<>());
        stats.put("timesRewarded", history.size());
        
        if (!history.isEmpty()) {
            stats.put("lastRewardTime", history.get(history.size() - 1));
        }
        
        return stats;
    }

    @Override
    public int getTotalRewardsGiven() {
        return (int) totalGiven.values(). stream().mapToDouble(Double::doubleValue).sum();
    }

    @Override
    public List<Map<String, Object>> getRewardHistory(UUID playerUUID) {
        List<Map<String, Object>> history = new ArrayList<>();
        List<Long> times = rewardHistory.getOrDefault(playerUUID, new ArrayList<>());

        for (long time : times) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("timestamp", time);
            entry.put("amount", moneyAmount);
            entry.put("currency", currencySymbol);
            entry.put("type", getRewardType());
            history.add(entry);
        }

        return history;
    }

    // ============ Getters & Setters ============

    /**
     * 금화량 반환
     */
    public double getMoneyAmount() {
        return moneyAmount;
    }

    /**
     * 금화량 설정
     */
    public void setMoneyAmount(double amount) {
        this.moneyAmount = Math.max(amount, 0);
    }

    /**
     * 통화 기호 반환
     */
    public String getCurrencySymbol() {
        return currencySymbol;
    }

    /**
     * 통화 기호 설정
     */
    public void setCurrencySymbol(String symbol) {
        this.currencySymbol = symbol != null ? symbol : "$";
    }

    /**
     * 알림 표시 여부 설정
     */
    public void setShowNotification(boolean show) {
        this.showNotification = show;
    }

    /**
     * 알림 표시 여부 조회
     */
    public boolean isShowNotification() {
        return showNotification;
    }

    /**
     * 활성화 여부 설정
     */
    public void setEnabled(boolean enabled) {
        this. enabled = enabled;
    }

    /**
     * 활성화 여부 조회
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 플레이어 총 지급액 반환
     */
    public double getPlayerTotalGiven(UUID playerUUID) {
        return totalGiven.getOrDefault(playerUUID, 0.0);
    }

    /**
     * 모든 보상 이력 초기화
     */
    public void resetHistory() {
        rewardHistory. clear();
        totalGiven. clear();
    }
}