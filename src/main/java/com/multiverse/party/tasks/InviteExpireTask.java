package com.multiverse.party.tasks;

import com.multiverse.party.PartyCore;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 파티 초대 만료 처리 Task
 */
public class InviteExpireTask extends BukkitRunnable {

    private final PartyCore plugin;

    public InviteExpireTask(PartyCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getPartyInviteManager().expireInvites();
    }
}