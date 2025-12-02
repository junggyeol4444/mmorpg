package com.multiverse.playerdata.tasks;

import com.multiverse.playerdata.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PenaltyCheckTask extends BukkitRunnable {

    private final PlayerDataManager playerDataManager;
    private final long intervalTicks;

    public PenaltyCheckTask(PlayerDataManager playerDataManager, long intervalTicks) {
        this.playerDataManager = playerDataManager;
        this.intervalTicks = intervalTicks;
    }

    @Override
    public void run() {
        for (UUID uuid : playerDataManager.getOnlinePlayerUuids()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && playerDataManager.hasActivePenalty(player)) {
                player.sendMessage("§c경고: 현재 패널티 효과가 적용 중입니다.");
                // 패널티 지속시간 관리 등 확장 가능
            }
        }
    }

    public void start() {
        this.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("PlayerDataCore"), intervalTicks, intervalTicks);
    }

    public void stop() {
        this.cancel();
    }
}