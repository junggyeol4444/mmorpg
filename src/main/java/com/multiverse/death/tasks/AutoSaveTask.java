package com.multiverse.death.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import com.multiverse.death.DeathAndRebirthPlugin;

/**
 * 주요 데이터 자동 저장을 담당하는 태스크
 */
public class AutoSaveTask extends BukkitRunnable {

    private final DeathAndRebirthPlugin plugin;

    public AutoSaveTask(DeathAndRebirthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // TODO: 플레이어 데이터, 퀘스트, 소울 코인 등 주요 데이터 저장 로직 구현
        plugin.saveAllData();
    }
}