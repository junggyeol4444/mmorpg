package com.multiverse.playerdata.tasks;

import com.multiverse.playerdata.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSaveTask extends BukkitRunnable {

    private final PlayerDataManager playerDataManager;
    private final long intervalTicks;

    public AutoSaveTask(PlayerDataManager playerDataManager, long intervalTicks) {
        this.playerDataManager = playerDataManager;
        this.intervalTicks = intervalTicks;
    }

    @Override
    public void run() {
        playerDataManager.saveAll();
        Bukkit.getLogger().info("[PlayerData] 모든 플레이어 데이터가 자동 저장되었습니다.");
    }

    public void start() {
        this.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("PlayerDataCore"), intervalTicks, intervalTicks);
    }

    public void stop() {
        this.cancel();
    }
}