package com.multiverse.party.tasks;

import com.multiverse.party.PartyCore;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 버프 업데이트/만료 체크 및 적용 Task
 */
public class BuffUpdateTask extends BukkitRunnable {

    private final PartyCore plugin;

    public BuffUpdateTask(PartyCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getPartyBuffManager().updateAllBuffs();
    }
}