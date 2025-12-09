package com.multiverse.party.tasks;

import com.multiverse.party.PartyCore;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 파티 모집 공고 정리/만료 처리 Task
 */
public class ListingCleanupTask extends BukkitRunnable {

    private final PartyCore plugin;

    public ListingCleanupTask(PartyCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getPartyFinder().cleanupExpiredListings();
    }
}