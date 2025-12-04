package com.multiverse.quest.rewards;

import com.multiverse. quest.models.*;
import org.bukkit.entity.Player;
import org.bukkit. Bukkit;
import java.util.*;

/**
 * 경험치 보상 핸들러
 * 플레이어에게 경험치를 지급합니다.
 */
public class ExperienceReward implements RewardHandler {
    private QuestReward reward;
    private int experienceAmount;                   // 지급할 경험치
    private boolean showParticles;                  // 파티클 표시 여부
    private Map<UUID, List<Long>> rewardHistory;   // 보상 지급 이력
    private Map<UUID, Integer> totalGiven;         // 플레이어별 총 지급량
    private boolean enabled;

    /**
     * 생성자
     */
    public ExperienceReward() {
        this.rewardHistory = new HashMap<>();
        this.totalGiven = new HashMap<>();
        this.showParticles = true;
        this.enabled = true;
    }

    // ============ Reward Distribution ============

    @Override
    public boolean giveReward(Player player, UUID playerUUID) {
        return giveReward(player, playerUUID, experienceAmount);
    }

    @Override
    public boolean giveReward(Player player, UUID playerUUID, int amount) {
        if (!enabled || ! canGiveReward(player, playerUUID)) {
            return false;
        }

        try {
            onBeforeGive(player, playerUUID);

            player.giveExp(amount);
            
            // 이력 기록
            rewardHistory.computeIfAbsent(playerUUID, k -> new ArrayList<>())
                . add(System.currentTimeMillis());
            totalGiven.put(playerUUID, totalGiven.getOrDefault(playerUUID, 0) + amount);

            if (showParticles) {
                showRewardParticles(player);
            }

            onAfterGive(player, playerUUID);
            return true;
        } catch (Exception e) {
            onGiveFailed(player, playerUUID, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean previewReward(Player player, UUID playerUUID) {
        if (player == null) {
            return false;
        }

        player.sendMessage(String.format("§6경험치 보상: §f%d", experienceAmount));
        return true;
    }

    // ============ Validation ============

    @Override
    public boolean canGiveReward(Player player, UUID playerUUID) {
        if (player == null || ! enabled) {
            return false;
        }

        if (player.isDead()) {
            return false;
        }

        return checkConditions(player);
    }

    @Override
    public boolean isValid() {
        return experienceAmount > 0;
    }

    @Override
    public boolean validateRewardData() {
        return reward != null && experienceAmount > 0;
    }

    // ============ Information ============

    @Override
    public String getRewardType() {
        return "EXPERIENCE";
    }

    @Override
    public String getDescription() {
        return String.format("경험치 %d 획득", experienceAmount);
    }

    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 경험치 보상 ===§r\n");
        sb.append("§7보상 타입: §f경험치\n");
        sb.append("§7경험치량: §f"). append(experienceAmount).append("\n");
        sb.append("§7파티클: ").append(showParticles ?  "§a표시" : "§c미표시").append("\n");
        sb.append("§7상태: ").append(enabled ? "§a활성화" : "§c비활성화").append("\n");
        return sb.toString();
    }

    @Override
    public double getRewardValue() {
        // 경험치 가치 계산 (1 경험치 = 0.1 포인트)
        return experienceAmount * 0.1;
    }

    // ============ Configuration ============

    @Override
    public void initialize(QuestReward reward) {
        this.reward = reward;
        this.experienceAmount = reward.getExperience();
    }

    @Override
    public void cleanup() {
        rewardHistory.clear();
        totalGiven.clear();
    }

    // ============ Events ============

    @Override
    public void onBeforeGive(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage(String.format("§a경험치를 획득했습니다: §f+%d", experienceAmount));
        }
    }

    @Override
    public void onAfterGive(Player player, UUID playerUUID) {
        if (player != null) {
            int currentLevel = player.getLevel();
            player.sendMessage(String.format("§7현재 레벨: §f%d", currentLevel));
        }
    }

    @Override
    public void onGiveFailed(Player player, UUID playerUUID, String reason) {
        if (player != null) {
            player.sendMessage(String. format("§c경험치 지급 실패: %s", reason));
        }
    }

    // ============ Data Management ============

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("type", getRewardType());
        data. put("experience", experienceAmount);
        data.put("showParticles", showParticles);
        data.put("enabled", enabled);
        return data;
    }

    @Override
    public void deserialize(Map<String, Object> data) {
        if (data. containsKey("experience")) {
            this.experienceAmount = (Integer) data.get("experience");
        }
        if (data. containsKey("showParticles")) {
            this.showParticles = (Boolean) data. get("showParticles");
        }
        if (data. containsKey("enabled")) {
            this.enabled = (Boolean) data.get("enabled");
        }
    }

    // ============ Conditions ============

    @Override
    public boolean checkConditions(Player player) {
        if (player == null) {
            return false;
        }

        return player.isOnline() && ! player.isDead();
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
            case "valid_level":
                return player.getLevel() >= 0;
            default:
                return true;
        }
    }

    // ============ Particle Effects ============

    /**
     * 보상 파티클 표시
     */
    private void showRewardParticles(Player player) {
        if (player == null || !showParticles) {
            return;
        }

        try {
            // 플레이어 주변에 파티클 표시 (실제 구현은 플러그인 버전에 따라 다름)
            player.getWorld().spigot().playEffect(
                player.getLocation(),
                org.bukkit.potion.PotionEffectType.GLOWING. id,
                1,
                1,
                0. 5f,
                0.5f,
                0.5f,
                0.1f,
                5,
                32
            );
        } catch (Exception e) {
            // 파티클 표시 실패 (무시)
        }
    }

    // ============ Statistics ============

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats. put("type", getRewardType());
        stats.put("experienceAmount", experienceAmount);
        stats.put("totalDistributed", totalGiven.values().stream().mapToInt(Integer::intValue).sum());
        stats.put("playersRewarded", totalGiven.size());
        stats.put("showParticles", showParticles);
        stats.put("enabled", enabled);
        return stats;
    }

    @Override
    public Map<String, Object> getPlayerStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats. put("playerUUID", playerUUID);
        stats.put("totalExperienceGiven", totalGiven.getOrDefault(playerUUID, 0));
        
        List<Long> history = rewardHistory.getOrDefault(playerUUID, new ArrayList<>());
        stats.put("timesRewarded", history.size());
        
        if (! history.isEmpty()) {
            stats.put("lastRewardTime", history.get(history.size() - 1));
        }
        
        return stats;
    }

    @Override
    public int getTotalRewardsGiven() {
        return totalGiven.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public List<Map<String, Object>> getRewardHistory(UUID playerUUID) {
        List<Map<String, Object>> history = new ArrayList<>();
        List<Long> times = rewardHistory.getOrDefault(playerUUID, new ArrayList<>());

        for (long time : times) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("timestamp", time);
            entry.put("amount", experienceAmount);
            entry.put("type", getRewardType());
            history.add(entry);
        }

        return history;
    }

    // ============ Getters & Setters ============

    /**
     * 경험치량 반환
     */
    public int getExperienceAmount() {
        return experienceAmount;
    }

    /**
     * 경험치량 설정
     */
    public void setExperienceAmount(int amount) {
        this.experienceAmount = Math.max(amount, 0);
    }

    /**
     * 파티클 표시 여부 설정
     */
    public void setShowParticles(boolean show) {
        this.showParticles = show;
    }

    /**
     * 파티클 표시 여부 조회
     */
    public boolean isShowParticles() {
        return showParticles;
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
     * 플레이어 총 지급량 반환
     */
    public int getPlayerTotalGiven(UUID playerUUID) {
        return totalGiven.getOrDefault(playerUUID, 0);
    }

    /**
     * 모든 보상 이력 초기화
     */
    public void resetHistory() {
        rewardHistory.clear();
        totalGiven.clear();
    }
}