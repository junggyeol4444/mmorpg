package com.multiverse.party.tasks;

import com.multiverse.party.PartyCore;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 파티/멤버 통계 업데이트 Task
 */
public class StatisticsUpdateTask extends BukkitRunnable {

    private final PartyCore plugin;

    public StatisticsUpdateTask(PartyCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getPartyStatisticsManager().updateAllStatistics();
    }
}