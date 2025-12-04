package com.multiverse.dungeon.tasks;

import com.multiverse.dungeon. DungeonCore;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 인스턴스 정리 태스크
 * 5분마다 실행되어 종료된 인스턴스 정리
 */
public class InstanceCleanupTask extends BukkitRunnable {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public InstanceCleanupTask(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 시작
     */
    public void start() {
        this.runTaskTimer(plugin, 6000L, 6000L); // 5분마다 실행 (300초)
    }

    @Override
    public void run() {
        try {
            long startTime = System.currentTimeMillis();

            // 만료된 인스턴스 정리
            plugin.getInstanceManager().cleanupExpiredInstances();

            long elapsedTime = System.currentTimeMillis() - startTime;

            if (elapsedTime > 100) {
                plugin.getLogger().info("✅ 인스턴스 정리 완료 (" + elapsedTime + "ms)");
            }
        } catch (Exception e) {
            plugin.getLogger(). severe("❌ 인스턴스 정리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}