package com.multiverse.quest.rewards;

import com.multiverse.  quest.models.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.  Bukkit;
import java.util.*;

/**
 * 아이템 보상 핸들러
 * 플레이어에게 아이템을 지급합니다.
 */
public class ItemReward implements RewardHandler {
    private QuestReward reward;
    private List<ItemStack> items;                  // 지급할 아이템 목록
    private boolean dropIfInventoryFull;            // 인벤토리 가득 시 드롭 여부
    private boolean showParticles;                  // 파티클 표시 여부
    private Map<UUID, List<Long>> rewardHistory;   // 보상 지급 이력
    private Map<UUID, Integer> totalGiven;         // 플레이어별 총 지급 아이템 수
    private boolean enabled;

    /**
     * 생성자
     */
    public ItemReward() {
        this.items = new ArrayList<>();
        this. rewardHistory = new HashMap<>();
        this.totalGiven = new HashMap<>();
        this.dropIfInventoryFull = true;
        this.showParticles = true;
        this. enabled = true;
    }

    // ============ Reward Distribution ============

    @Override
    public boolean giveReward(Player player, UUID playerUUID) {
        return giveReward(player, playerUUID, 1);
    }

    @Override
    public boolean giveReward(Player player, UUID playerUUID, int amount) {
        if (!enabled || ! canGiveReward(player, playerUUID)) {
            return false;
        }

        try {
            onBeforeGive(player, playerUUID);

            int itemsGiven = 0;

            for (int i = 0; i < amount; i++) {
                for (ItemStack item : items) {
                    if (item == null || item.getType() == Material.AIR) {
                        continue;
                    }

                    ItemStack itemClone = item.clone();
                    
                    // 인벤토리에 공간이 있으면 추가, 없으면 드롭
                    if (player.getInventory().firstEmpty() != -1) {
                        player.getInventory().addItem(itemClone);
                        itemsGiven++;
                    } else if (dropIfInventoryFull) {
                        player.getWorld().dropItem(player.getLocation(), itemClone);
                        itemsGiven++;
                    }
                }
            }

            if (itemsGiven > 0) {
                // 이력 기록
                rewardHistory.computeIfAbsent(playerUUID, k -> new ArrayList<>())
                    .add(System. currentTimeMillis());
                totalGiven.put(playerUUID, totalGiven.getOrDefault(playerUUID, 0) + itemsGiven);

                if (showParticles) {
                    showRewardParticles(player);
                }

                onAfterGive(player, playerUUID);
                return true;
            }

            return false;
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

        StringBuilder sb = new StringBuilder();
        sb.append("§6아이템 보상:\n");
        
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material. AIR) {
                sb. append(String.format("§f- %s x%d\n", 
                    item.getType(). name(), item.getAmount()));
            }
        }
        
        player.sendMessage(sb.toString());
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
        return !  items.isEmpty();
    }

    @Override
    public boolean validateRewardData() {
        if (reward == null || items. isEmpty()) {
            return false;
        }

        for (ItemStack item : items) {
            if (item == null || item.getType() == Material. AIR) {
                return false;
            }
        }

        return true;
    }

    // ============ Information ============

    @Override
    public String getRewardType() {
        return "ITEM";
    }

    @Override
    public String getDescription() {
        if (items.isEmpty()) {
            return "아이템 없음";
        }

        ItemStack firstItem = items.get(0);
        if (items.size() == 1) {
            return String.format("%s x%d 획득",
                firstItem.getType(). name(), firstItem.getAmount());
        } else {
            return String.format("%s 외 %d개 아이템 획득",
                firstItem.getType().name(), items.size() - 1);
        }
    }

    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 아이템 보상 ===§r\n");
        sb.append("§7보상 타입: §f아이템\n");
        sb.append("§7아이템 수: §f").append(items. size()).append("\n");
        
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            if (item != null && item.getType() != Material. AIR) {
                sb. append(String.format("§7  %d. §f%s x%d\n",
                    i + 1, item.getType().name(), item.getAmount()));
            }
        }
        
        sb.append("§7드롭: ").append(dropIfInventoryFull ? "§a활성화" : "§c비활성화").append("\n");
        sb.append("§7파티클: ").append(showParticles ? "§a표시" : "§c미표시").append("\n");
        sb.append("§7상태: ").append(enabled ?  "§a활성화" : "§c비활성화").append("\n");
        return sb.toString();
    }

    @Override
    public double getRewardValue() {
        // 아이템 가치 계산 (아이템 타입과 개수 기반)
        double value = 0;
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                value += item.getAmount() * 0.5;
            }
        }
        return value;
    }

    // ============ Configuration ============

    @Override
    public void initialize(QuestReward reward) {
        this.reward = reward;
        // 아이템 목록은 QuestReward에서 추출 (커스텀 구현 필요)
        // this.items = reward.getItems();
    }

    @Override
    public void cleanup() {
        items.clear();
        rewardHistory.clear();
        totalGiven.clear();
    }

    // ============ Events ============

    @Override
    public void onBeforeGive(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage(String.format("§a아이템을 획득했습니다: §f%s", getDescription()));
        }
    }

    @Override
    public void onAfterGive(Player player, UUID playerUUID) {
        if (player != null) {
            player.sendMessage("§7아이템이 인벤토리에 추가되었습니다.");
        }
    }

    @Override
    public void onGiveFailed(Player player, UUID playerUUID, String reason) {
        if (player != null) {
            player.sendMessage(String. format("§c아이템 지급 실패: %s", reason));
        }
    }

    // ============ Data Management ============

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("type", getRewardType());
        data.put("itemCount", items.size());
        data.put("dropIfInventoryFull", dropIfInventoryFull);
        data.put("showParticles", showParticles);
        data.put("enabled", enabled);
        return data;
    }

    @Override
    public void deserialize(Map<String, Object> data) {
        if (data.containsKey("dropIfInventoryFull")) {
            this.dropIfInventoryFull = (Boolean) data.get("dropIfInventoryFull");
        }
        if (data.containsKey("showParticles")) {
            this.showParticles = (Boolean) data.get("showParticles");
        }
        if (data.containsKey("enabled")) {
            this. enabled = (Boolean) data.get("enabled");
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
            case "has_inventory_space":
                return player.getInventory().firstEmpty() != -1;
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
            // 플레이어 주변에 파티클 표시
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
        stats.put("type", getRewardType());
        stats.put("itemTypes", items.size());
        stats.put("totalItemsDistributed", totalGiven. values().stream(). mapToInt(Integer::intValue). sum());
        stats.put("playersRewarded", totalGiven.size());
        stats.put("dropIfInventoryFull", dropIfInventoryFull);
        stats.put("showParticles", showParticles);
        stats.put("enabled", enabled);
        return stats;
    }

    @Override
    public Map<String, Object> getPlayerStatistics(UUID playerUUID) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats. put("playerUUID", playerUUID);
        stats.put("totalItemsGiven", totalGiven.getOrDefault(playerUUID, 0));
        
        List<Long> history = rewardHistory.getOrDefault(playerUUID, new ArrayList<>());
        stats. put("timesRewarded", history.size());
        
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
            entry. put("timestamp", time);
            entry.put("itemCount", items.size());
            entry.put("type", getRewardType());
            history.add(entry);
        }

        return history;
    }

    // ============ Item Management ============

    /**
     * 아이템 추가
     */
    public void addItem(ItemStack item) {
        if (item != null && item.getType() != Material. AIR) {
            items. add(item);
        }
    }

    /**
     * 아이템 제거
     */
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items. remove(index);
        }
    }

    /**
     * 모든 아이템 반환
     */
    public List<ItemStack> getItems() {
        return new ArrayList<>(items);
    }

    /**
     * 아이템 목록 설정
     */
    public void setItems(List<ItemStack> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    /**
     * 아이템 개수 반환
     */
    public int getItemCount() {
        return items.size();
    }

    // ============ Getters & Setters ============

    /**
     * 인벤토리 가득 시 드롭 여부 설정
     */
    public void setDropIfInventoryFull(boolean drop) {
        this.dropIfInventoryFull = drop;
    }

    /**
     * 인벤토리 가득 시 드롭 여부 조회
     */
    public boolean isDropIfInventoryFull() {
        return dropIfInventoryFull;
    }

    /**
     * 파티클 표시 여부 설정
     */
    public void setShowParticles(boolean show) {
        this. showParticles = show;
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
        this. enabled = enabled;
    }

    /**
     * 활성화 여부 조회
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 플레이어 총 지급 아이템 수 반환
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