package com.multiverse.dungeon.tasks;

import com.multiverse.dungeon.DungeonCore;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 리더보드 업데이트 태스크
 * 10분마다 실행되어 리더보드 정렬 및 저장
 */
public class LeaderboardUpdateTask extends BukkitRunnable {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public LeaderboardUpdateTask(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 시작
     */
    public void start() {
        this.runTaskTimer(plugin, 12000L, 12000L); // 10분마다 실행 (600초)
    }

    @Override
    public void run() {
        try {
            long startTime = System.currentTimeMillis();

            // 리더보드 저장
            plugin.getLeaderboardManager().saveAllRecords();

            long elapsedTime = System.currentTimeMillis() - startTime;

            if (elapsedTime > 100) {
                plugin.getLogger().info("✅ 리더보드 업데이트 완료 (" + elapsedTime + "ms)");
            }
        } catch (Exception e) {
            plugin.getLogger(). severe("❌ 리더보드 업데이트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}