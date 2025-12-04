package com.multiverse. dungeon.tasks;

import com.multiverse.dungeon.DungeonCore;
import org. bukkit.scheduler.BukkitRunnable;

/**
 * 자동 저장 태스크
 * 5분마다 실행되어 모든 데이터 저장
 */
public class AutoSaveTask extends BukkitRunnable {

    private final DungeonCore plugin;
    private int saveCount = 0;

    /**
     * 생성자
     */
    public AutoSaveTask(DungeonCore plugin) {
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
            saveCount++;

            // 모든 데이터 저장
            plugin.getDataManager(). saveAllData();

            long elapsedTime = System.currentTimeMillis() - startTime;

            plugin.getLogger().info("✅ 자동 저장 완료 #" + saveCount + " (" + elapsedTime + "ms)");
        } catch (Exception e) {
            plugin. getLogger().severe("❌ 자동 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 저장 횟수 조회
     */
    public int getSaveCount() {
        return saveCount;
    }
}