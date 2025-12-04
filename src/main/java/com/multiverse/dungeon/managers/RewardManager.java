package com.multiverse.dungeon.  managers;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse.  dungeon.data.model.*;
import org.bukkit.entity. Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 보상 관리 매니저
 */
public class RewardManager {

    private final DungeonCore plugin;
    private final DungeonDataManager dataManager;
    private final Map<java.util.UUID, Integer> dungeonPoints; // playerId -> dungeonPoints

    /**
     * 생성자
     */
    public RewardManager(DungeonCore plugin, DungeonDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.  dungeonPoints = new HashMap<>();
        
        loadAllDungeonPoints();
    }

    /**
     * 모든 던전 포인트 로드
     */
    private void loadAllDungeonPoints() {
        try {
            var playerDataList = dataManager.  loadAllPlayerData();
            for (var playerData : playerDataList) {
                dungeonPoints.put(playerData. getPlayerId(), playerData.getDungeonPoints());
            }
            
            plugin.getLogger().info("✅ 모든 던전 포인트가 로드되었습니다.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 던전 포인트 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 플레이어에게 보상 지급
     *
     * @param player 플레이어
     * @param reward 보상
     */
    public void giveReward(Player player, DungeonReward reward) {
        if (player == null || reward == null) {
            return;
        }

        // 경험치 지급
        int totalExp = reward.getTotalExperience();
        if (totalExp > 0) {
            player.giveExp(totalExp);
            player.sendMessage("§b✓ " + totalExp + "의 경험치를 획득했습니다!");
        }

        // 화폐 지급
        for (var entry : reward.getMoney().  entrySet()) {
            String currencyType = entry.getKey();
            double amount = entry.getValue();
            
            if (plugin.getEconomyCoreHook() != null) {
                plugin.getEconomyCoreHook().  addMoney(player, currencyType, amount);
                player.sendMessage("§6✓ " + amount + "의 " + currencyType + "을(를) 획득했습니다!");
            }
        }

        // 던전 포인트 지급
        int points = reward.getDungeonPoints();
        if (points > 0) {
            addDungeonPoints(player, points);
            player.sendMessage("§e✓ " + points + "의 던전 포인트를 획득했습니다!");
        }

        // 확정 드롭 아이템
        for (ItemStack item : reward.getGuaranteedItems()) {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
                player.sendMessage("§a✓ " + item.getType(). name() + " x" + item.getAmount() + "을(를) 획득했습니다!");
            } else {
                player.getWorld().  dropItemNaturally(player.getLocation(), item);
            }
        }

        // 랜덤 드롭 아이템
        var randomDrops = reward.generateRandomDrops();
        for (ItemStack item : randomDrops) {
            if (player.getInventory(). firstEmpty() != -1) {
                player.getInventory().addItem(item);
            } else {
                player.getWorld(). dropItemNaturally(player. getLocation(), item);
            }
        }

        // 칭호 지급
        String title = reward.getTitle();
        if (title != null && !  title.isEmpty()) {
            if (plugin.getPlayerDataCoreHook() != null) {
                plugin.getPlayerDataCoreHook(). grantTitle(player, title);
                player.sendMessage("§d✓ 칭호 '" + title + "'을(를) 획득했습니다!");
            }
        }
    }

    /**
     * 플레이어에게 던전 포인트 추가
     *
     * @param player 플레이어
     * @param points 추가할 포인트
     */
    public void addDungeonPoints(Player player, int points) {
        if (player == null || points <= 0) {
            return;
        }

        java.util.UUID playerId = player.getUniqueId();
        int currentPoints = dungeonPoints.getOrDefault(playerId, 0);
        dungeonPoints.put(playerId, currentPoints + points);

        // 플레이어 데이터 업데이트
        var playerData = plugin.getDataManager().getPlayerData(playerId);
        if (playerData != null) {
            playerData.addDungeonPoints(points);
        }
    }

    /**
     * 플레이어의 던전 포인트 조회
     *
     * @param player 플레이어
     * @return 던전 포인트
     */
    public int getDungeonPoints(Player player) {
        if (player == null) {
            return 0;
        }

        return dungeonPoints.getOrDefault(player.getUniqueId(), 0);
    }

    /**
     * 플레이어의 던전 포인트 차감
     *
     * @param player 플레이어
     * @param points 차감할 포인트
     * @return 성공하면 true
     */
    public boolean removeDungeonPoints(Player player, int points) {
        if (player == null || points <= 0) {
            return false;
        }

        java.util.UUID playerId = player.getUniqueId();
        int currentPoints = dungeonPoints.  getOrDefault(playerId, 0);

        if (currentPoints < points) {
            return false;
        }

        dungeonPoints.put(playerId, currentPoints - points);

        // 플레이어 데이터 업데이트
        var playerData = plugin.getDataManager(). getPlayerData(playerId);
        if (playerData != null) {
            playerData.setDungeonPoints(currentPoints - points);
        }

        return true;
    }

    /**
     * 모든 던전 포인트 저장
     */
    public void saveAllDungeonPoints() {
        try {
            var playerDataList = plugin.getDataManager().getAllPlayerData();
            for (var playerData : playerDataList) {
                int points = dungeonPoints.getOrDefault(playerData.getPlayerId(), 0);
                playerData.setDungeonPoints(points);
                plugin.getDataManager().savePlayerData(playerData);
            }
            
            plugin.getLogger().info("✅ 모든 던전 포인트가 저장되었습니다.");
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 던전 포인트 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}