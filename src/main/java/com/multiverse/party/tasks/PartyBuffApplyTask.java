package com.multiverse.party.tasks;

import com.multiverse.party.PartyCore;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 파티 버프 정기 적용 Task
 */
public class PartyBuffApplyTask extends BukkitRunnable {

    private final PartyCore plugin;

    public PartyBuffApplyTask(PartyCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getPartyBuffManager().applyScheduledBuffs();
    }
}