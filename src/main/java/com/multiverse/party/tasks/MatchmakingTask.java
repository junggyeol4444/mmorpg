package com.multiverse.party.tasks;

import com.multiverse.party.PartyCore;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 자동 매칭 Task (매칭 대기열 처리)
 */
public class MatchmakingTask extends BukkitRunnable {

    private final PartyCore plugin;

    public MatchmakingTask(PartyCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getPartyFinder().processMatchingQueue();
    }
}