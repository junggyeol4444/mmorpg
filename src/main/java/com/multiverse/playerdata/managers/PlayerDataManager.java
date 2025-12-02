package com.multiverse.playerdata.managers;

import com.multiverse.playerdata.PlayerDataCore;
import com.multiverse.playerdata.models.*;
import com.multiverse.playerdata.models.enums.StatType;
import com.multiverse.playerdata.data.DataManager;
import com.multiverse.playerdata.utils.ConfigUtil;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataManager {

    private final PlayerDataCore plugin;
    private final DataManager dataManager;
    private final ConfigUtil configUtil;
    private final Map<UUID, PlayerStats> playerStatsCache;
    private final Map<UUID, Race> playerRaceCache;
    private final Map<UUID, Evolution> evolutionConfirmationCache;

    public PlayerDataManager(PlayerDataCore plugin, DataManager dataManager, ConfigUtil configUtil) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.configUtil = configUtil;
        this.playerStatsCache = new HashMap<>();
        this.playerRaceCache = new HashMap<>();
        this.evolutionConfirmationCache = new HashMap<>();
    }

    // ========= 데이터 로드/저장 ===========
    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();

        if (!hasData(player)) {
            // 신규 플레이어: 데이터 생성/종족 배정
            Race race = plugin.getRaceManager().getRandomRace();
            setPlayerRace(player, race);
            PlayerStats stats = PlayerStats.createNew(uuid, race);
            playerStatsCache.put(uuid, stats);
            dataManager.savePlayerStats(uuid, stats);
            dataManager.setPlayerRaceId(uuid, race.getId());
        } else {
            playerStatsCache.put(uuid, dataManager.loadPlayerStats(uuid));
            String raceId = dataManager.getPlayerRaceId(uuid);
            playerRaceCache.put(uuid, plugin.getRaceManager().getRace(raceId));
        }
    }

    public void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        if (playerStatsCache.containsKey(uuid)) {
            dataManager.savePlayerStats(uuid, playerStatsCache.get(uuid));
        }
        if (playerRaceCache.containsKey(uuid)) {
            dataManager.setPlayerRaceId(uuid, playerRaceCache.get(uuid).getId());
        }
    }

    public void saveAllPlayerData() {
        for (UUID uuid : playerStatsCache.keySet()) {
            dataManager.savePlayerStats(uuid, playerStatsCache.get(uuid));
        }
        for (UUID uuid : playerRaceCache.keySet()) {
            dataManager.setPlayerRaceId(uuid, playerRaceCache.get(uuid).getId());
        }
    }

    public void unloadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        playerStatsCache.remove(uuid);
        playerRaceCache.remove(uuid);
        evolutionConfirmationCache.remove(uuid);
    }

    public boolean hasData(Player player) {
        UUID uuid = player.getUniqueId();
        return dataManager.playerDataExists(uuid);
    }

    // ========= 플레이어 데이터 관련 ===========
    public PlayerStats getPlayerStats(Player player) {
        UUID uuid = player.getUniqueId();
        return playerStatsCache.computeIfAbsent(uuid, k -> dataManager.loadPlayerStats(uuid));
    }

    public void setPlayerStats(Player player, PlayerStats stats) {
        UUID uuid = player.getUniqueId();
        playerStatsCache.put(uuid, stats);
        dataManager.savePlayerStats(uuid, stats);
    }

    public Race getPlayerRace(Player player) {
        UUID uuid = player.getUniqueId();
        return playerRaceCache.computeIfAbsent(uuid, k -> plugin.getRaceManager().getRace(dataManager.getPlayerRaceId(uuid)));
    }

    public void setPlayerRace(Player player, Race race) {
        UUID uuid = player.getUniqueId();
        playerRaceCache.put(uuid, race);
        dataManager.setPlayerRaceId(uuid, race.getId());
        plugin.getRaceManager().applyRaceEffects(player);
    }

    // ======= 진화 확인 관리 =========
    public void requestEvolutionConfirmation(Player player, Evolution evo) {
        evolutionConfirmationCache.put(player.getUniqueId(), evo);
    }

    public boolean isEvolutionConfirmationPending(Player player, Evolution evo) {
        return evolutionConfirmationCache.containsKey(player.getUniqueId()) &&
                evolutionConfirmationCache.get(player.getUniqueId()).equals(evo);
    }

    public void clearEvolutionConfirmation(Player player) {
        evolutionConfirmationCache.remove(player.getUniqueId());
    }

    // ======= 퀘스트 완료 관리 예시 =========
    public boolean hasPlayerCompletedQuest(Player player, String questId) {
        PlayerStats stats = getPlayerStats(player);
        return stats.getCompletedQuests().contains(questId);
    }

    // ======= 골드(경제) 처리 예시 ========
    public boolean chargeGold(Player player, int amount) {
        // Vault API 연동 예시
        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            // 실제 Economy 연동 로직
            // Economy economy = ...
            // if (economy.getBalance(player) < amount) return false;
            // economy.withdrawPlayer(player, amount);
            // return true;
            return true; // 데모용
        }
        return false;
    }

    // ======= 백업/복구 관리 예시 =======
    public void restorePlayerData(Player player, String backupFileName) {
        UUID uuid = player.getUniqueId();
        PlayerStats stats = dataManager.restorePlayerStats(uuid, backupFileName);
        setPlayerStats(player, stats);
        plugin.getRaceManager().applyRaceEffects(player);
        plugin.getStatsManager().applyStatEffects(player);
    }

    // === 마이그레이션 예시 ===
    public boolean migrate() {
        // YAML → MySQL 또는 반대쪽 방향 등 실제 마이그레이션 로직
        // ... 구현
        return true;
    }
}