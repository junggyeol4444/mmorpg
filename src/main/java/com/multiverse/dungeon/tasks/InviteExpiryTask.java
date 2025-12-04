package com.multiverse.dungeon.tasks;

import com. multiverse.dungeon.DungeonCore;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 초대 만료 정리 태스크
 * 1분마다 실행되어 만료된 초대 정리
 */
public class InviteExpiryTask extends BukkitRunnable {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public InviteExpiryTask(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 시작
     */
    public void start() {
        this.runTaskTimer(plugin, 1200L, 1200L); // 1분마다 실행 (60초)
    }

    @Override
    public void run() {
        try {
            long startTime = System.currentTimeMillis();

            // 만료된 초대 정리
            plugin.getInviteManager().cleanupExpiredInvites();

            long elapsedTime = System.currentTimeMillis() - startTime;

            if (elapsedTime > 50) {
                plugin.getLogger().info("✅ 초대 만료 정리 완료 (" + elapsedTime + "ms)");
            }
        } catch (Exception e) {
            plugin.getLogger(). severe("❌ 초대 만료 정리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}