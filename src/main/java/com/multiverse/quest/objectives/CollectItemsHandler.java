package com.multiverse.quest.objectives;

import com.multiverse. quest.models.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * 아이템 수집 목표 핸들러
 * 플레이어가 특정 아이템을 수집하도록 하는 목표를 관리합니다.
 */
public class CollectItemsHandler implements ObjectiveHandler {
    private QuestObjective objective;
    private String itemName;                        // 수집할 아이템 이름
    private Material itemMaterial;                  // 아이템 재료
    private int requiredCount;                      // 필요한 개수
    private Map<UUID, Integer> playerProgress;     // 플레이어 진행도
    private Map<UUID, Long> lastUpdateTime;        // 마지막 업데이트 시간
    private boolean consumeItem;                    // 아이템 소비 여부
    private boolean enabled;

    /**
     * 생성자
     */
    public CollectItemsHandler() {
        this.playerProgress = new HashMap<>();
        this.lastUpdateTime = new HashMap<>();
        this.consumeItem = true;
        this.enabled = true;
    }

    // ============ Initialization ============

    @Override
    public void initialize(QuestObjective objective) {
        this.objective = objective;
        this.itemName = objective.getDescription();
        this.requiredCount = objective.getRequired();
        
        try {
            this.itemMaterial = Material.valueOf(itemName.toUpperCase());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("알 수 없는 아이템: " + itemName);
            this.itemMaterial = Material. STONE;
        }
    }

    @Override
    public void cleanup() {
        playerProgress.clear();
        lastUpdateTime.clear();
    }

    // ============ Progress Tracking ============

    @Override
    public boolean updateProgress(Player player, UUID playerUUID, int amount) {
        if (!enabled || ! canProgress(player, playerUUID)) {
            return false;
        }

        int currentProgress = playerProgress.getOrDefault(playerUUID, 0);
        int newProgress = Math.min(currentProgress + amount, requiredCount);

        if (newProgress == currentProgress) {
            return false;
        }

        playerProgress.put(playerUUID, newProgress);
        lastUpdateTime. put(playerUUID, System. currentTimeMillis());

        onProgress(player, playerUUID, amount);

        if (newProgress >= requiredCount) {
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
        return getProgress(playerUUID) >= requiredCount;
    }

    @Override
    public void resetProgress(UUID playerUUID) {
        playerProgress.remove(playerUUID);
        lastUpdateTime. remove(playerUUID);
    }

    // ============ Validation ============

    @Override
    public boolean canProgress(Player player, UUID playerUUID) {
        if (player == null || ! enabled) {
            return false;
        }

        if (! checkConditions(player)) {
            return false;
        }

        // 플레이어가 아이템을 가지고 있는지 확인
        if (! hasRequiredItem(player)) {
            return false;
        }

        return getProgress(playerUUID) < requiredCount;
    }

    @Override
    public boolean isValid() {
        return objective != null && 
               itemName != null && ! itemName.isEmpty() &&
               itemMaterial != null &&
               requiredCount > 0;
    }

    // ============ Information ============

    @Override
    public String getObjectiveType() {
        return "COLLECT_ITEMS";
    }

    @Override
    public String getDescription() {
        return objective != null ? objective.getDescription() : "";
    }

    @Override
    public String getProgressString(UUID playerUUID) {
        return String.format("%d/%d", getProgress(playerUUID), requiredCount);
    }

    @Override
    public String getDetailedInfo(UUID playerUUID) {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 아이템 수집 목표 ===§r\n");
        sb.append("§7아이템: §f"). append(itemName).append("\n");
        sb.append("§7진행도: §f").append(getProgressString(playerUUID)).append("\n");
        sb.append("§7소비: ").append(consumeItem ?  "§a예" : "§c아니오").append("\n");
        sb.append("§7완료: ").append(isCompleted(playerUUID) ? "§a완료" : "§c진행중").append("\n");
        return sb.toString();
    }

    // ============ Events ============

    @Override
    public void onStart(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage("§a목표: " + itemName + " " + requiredCount + "개 수집하기");
        }
    }

    @Override
    public void onProgress(Player player, UUID playerUUID, int amount) {
        if (player != null) {
            int current = getProgress(playerUUID);
            player.sendMessage(String.format("§7[§a진행§7] %s: §f%d/%d", 
                itemName, current, requiredCount));
        }
    }

    @Override
    public void onComplete(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage("§a✓ 아이템 수집 목표 완료!");
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
        data. put("itemName", itemName);
        data.put("requiredCount", requiredCount);
        data.put("consumeItem", consumeItem);
        data.put("enabled", enabled);
        return data;
    }

    @Override
    public void deserialize(Map<String, Object> data) {
        if (data.containsKey("itemName")) {
            this.itemName = (String) data.get("itemName");
            try {
                this.itemMaterial = Material.valueOf(itemName.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.itemMaterial = Material. STONE;
            }
        }
        if (data.containsKey("requiredCount")) {
            this.requiredCount = (Integer) data.get("requiredCount");
        }
        if (data.containsKey("consumeItem")) {
            this.consumeItem = (Boolean) data.get("consumeItem");
        }
        if (data.containsKey("enabled")) {
            this.enabled = (Boolean) data. get("enabled");
        }
    }

    // ============ Conditions ============

    @Override
    public boolean checkConditions(Player player) {
        if (player == null) {
            return false;
        }

        // 플레이어가 생존 중인지 확인
        if (player.isDead()) {
            return false;
        }

        // 플레이어가 온라인인지 확인
        return player.isOnline();
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
                return player. isOnline();
            case "has_item":
                return hasRequiredItem(player);
            case "has_inventory_space":
                return player.getInventory().firstEmpty() != -1;
            default:
                return true;
        }
    }

    // ============ Item Management ============

    /**
     * 플레이어가 필요한 아이템을 가지고 있는지 확인
     */
    private boolean hasRequiredItem(Player player) {
        if (player == null || itemMaterial == null) {
            return false;
        }

        int count = 0;
        for (ItemStack item : player.getInventory(). getContents()) {
            if (item != null && item.getType() == itemMaterial) {
                count += item.getAmount();
            }
        }

        return count > 0;
    }

    /**
     * 플레이어의 아이템 개수 반환
     */
    public int getItemCount(Player player) {
        if (player == null || itemMaterial == null) {
            return 0;
        }

        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == itemMaterial) {
                count += item.getAmount();
            }
        }

        return count;
    }

    /**
     * 플레이어의 아이템 제거
     */
    public boolean removeItem(Player player, int amount) {
        if (player == null || itemMaterial == null) {
            return false;
        }

        int toRemove = amount;
        ItemStack[] contents = player.getInventory().getContents();

        for (ItemStack item : contents) {
            if (item != null && item. getType() == itemMaterial && toRemove > 0) {
                if (item.getAmount() <= toRemove) {
                    toRemove -= item.getAmount();
                    item.setAmount(0);
                } else {
                    item.setAmount(item.getAmount() - toRemove);
                    toRemove = 0;
                }
            }
        }

        return toRemove == 0;
    }

    // ============ Statistics ============

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("type", getObjectiveType());
        stats.put("itemName", itemName);
        stats.put("requiredCount", requiredCount);
        stats.put("totalPlayers", playerProgress.size());
        stats.put("consumeItem", consumeItem);
        stats.put("enabled", enabled);
        return stats;
    }

    @Override
    public Map<String, Object> getPlayerStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats. put("playerUUID", playerUUID);
        stats.put("progress", getProgress(playerUUID));
        stats.put("required", requiredCount);
        stats.put("percentage", (getProgress(playerUUID) * 100) / requiredCount);
        stats.put("completed", isCompleted(playerUUID));
        
        Long lastUpdate = lastUpdateTime.get(playerUUID);
        if (lastUpdate != null) {
            stats.put("lastUpdateTime", lastUpdate);
        }
        
        return stats;
    }

    // ============ Getters & Setters ============

    /**
     * 아이템 이름 반환
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * 아이템 이름 설정
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
        try {
            this.itemMaterial = Material.valueOf(itemName. toUpperCase());
        } catch (IllegalArgumentException e) {
            this.itemMaterial = Material.STONE;
        }
    }

    /**
     * 필요한 개수 반환
     */
    public int getRequiredCount() {
        return requiredCount;
    }

    /**
     * 필요한 개수 설정
     */
    public void setRequiredCount(int requiredCount) {
        this.requiredCount = Math.max(requiredCount, 1);
    }

    /**
     * 아이템 소비 여부 설정
     */
    public void setConsumeItem(boolean consumeItem) {
        this.consumeItem = consumeItem;
    }

    /**
     * 아이템 소비 여부 조회
     */
    public boolean isConsumeItem() {
        return consumeItem;
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
     * 모든 플레이어 진행도 초기화
     */
    public void resetAllProgress() {
        playerProgress.clear();
        lastUpdateTime.clear();
    }
}