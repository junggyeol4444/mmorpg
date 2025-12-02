package com.multiverse.core.tasks;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.multiverse.core.data.DataManager;

/**
 * 일정 주기로 모든 데이터 저장을 자동 수행하는 작업
 */
public class AutoSaveTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final DataManager dataManager;

    public AutoSaveTask(JavaPlugin plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    @Override
    public void run() {
        dataManager.saveAll();
    }
}