package com.multiverse.party.tasks;

import com.multiverse.party.PartyCore;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 일정 간격마다 파티/플레이어 데이터 자동 저장
 */
public class AutoSaveTask extends BukkitRunnable {

    private final PartyCore plugin;

    public AutoSaveTask(PartyCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getDataManager().saveAllData();
        plugin.getLogger().info("[파티] 파티/플레이어 데이터 자동 저장 완료");
    }
}