package com.multiverse.playerdata.listeners;

import com.multiverse.playerdata.PlayerDataCore;
import com.multiverse.playerdata.managers.*;
import com.multiverse.playerdata.models.Race;
import com.multiverse.playerdata.models.enums.RaceType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDataListener implements Listener {

    private final PlayerDataCore plugin;
    private final RaceManager raceManager;
    private final StatsManager statsManager;
    private final PlayerDataManager playerDataManager;

    public PlayerDataListener(PlayerDataCore plugin) {
        this.plugin = plugin;
        this.raceManager = plugin.getRaceManager();
        this.statsManager = plugin.getStatsManager();
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 플레이어 데이터 로드
        playerDataManager.loadPlayerData(player);

        // 신규 플레이어면 종족 배정
        if (!playerDataManager.hasData(player)) {
            Race race = raceManager.getRandomRace();
            raceManager.setPlayerRace(player, race);
            player.sendMessage(plugin.getConfigUtil().replaceVariables("race.assigned", race.getName()));

            if (race.getType() == RaceType.SPECIAL) {
                Bukkit.broadcastMessage(plugin.getConfigUtil().replaceVariables("race.special", player.getName(), race.getName()));
            }
        }

        // 종족 효과 적용
        raceManager.applyRaceEffects(player);

        // 스탯 효과 적용
        statsManager.applyStatEffects(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // 플레이어 데이터 저장
        playerDataManager.savePlayerData(player);

        // 캐시 정리
        playerDataManager.unloadPlayerData(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // 영혼체 종족은 아이템 드롭 없음
        Race race = raceManager.getPlayerRace(player);
        if (race != null && race.getId().equals("spirit")) {
            event.setKeepInventory(true);
            event.getDrops().clear();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        // 스탯 효과 재적용 (HP 등)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            statsManager.applyStatEffects(player);
        }, 5L);
    }
}