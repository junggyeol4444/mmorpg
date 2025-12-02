package com.multiverse.playerdata.listeners;

import com.multiverse.playerdata.PlayerDataCore;
import com.multiverse.playerdata.managers.StatsManager;
import com.multiverse.playerdata.managers.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class LevelListener implements Listener {

    private final PlayerDataCore plugin;
    private final StatsManager statsManager;
    private final PlayerDataManager playerDataManager;

    public LevelListener(PlayerDataCore plugin) {
        this.plugin = plugin;
        this.statsManager = plugin.getStatsManager();
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        int oldLevel = event.getOldLevel();
        int newLevel = event.getNewLevel();

        // 서버 내의 레벨 시스템에 맞게 동작하도록 커스텀
        if (newLevel > oldLevel) {
            statsManager.onLevelUp(player, newLevel);
            player.sendMessage("§b레벨 업! [" + oldLevel + " → " + newLevel + "]");
        }
        // 필요시 레벨 다운도 처리 가능
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        // 레벨과 관계 없이 추가 경험치에 따른 이벤트 로직 가능
        // 예: 특정 경험치 획득 시 보너스 지급 등
    }
}