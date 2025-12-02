package com.multiverse.core.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.multiverse.core.manager.FusionManager;
import com.multiverse.core.models.FusionStatus;

/**
 * 퓨전 스테이지 진행 및 상태 변화를 주기적으로 관리하는 작업
 */
public class FusionStageTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final FusionManager fusionManager;

    public FusionStageTask(JavaPlugin plugin, FusionManager fusionManager) {
        this.plugin = plugin;
        this.fusionManager = fusionManager;
    }

    @Override
    public void run() {
        for (FusionStatus status : fusionManager.getActiveFusions()) {
            if (fusionManager.shouldAdvanceStage(status)) {
                fusionManager.advanceFusionStage(status);
            }
        }
    }
}