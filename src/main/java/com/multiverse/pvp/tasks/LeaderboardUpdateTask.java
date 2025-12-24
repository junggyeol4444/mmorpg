package com. multiverse.pvp.tasks;

import com.multiverse. pvp.PvPCore;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaderboardUpdateTask extends BukkitRunnable {

    private final PvPCore plugin;
    private final long updateIntervalTicks;

    public LeaderboardUpdateTask(PvPCore plugin) {
        this. plugin = plugin;

        // 설정에서 업데이트 간격 로드 (분 단위)
        int updateIntervalMinutes = plugin.getConfig().getInt("leaderboard.update-interval", 1);
        this.updateIntervalTicks = updateIntervalMinutes * 60L * 20L;
    }

    /**
     * 태스크 시작
     */
    public void start() {
        // 비동기로 실행
        this.runTaskTimerAsynchronously(plugin, 20L, updateIntervalTicks);
        plugin.getLogger().info("리더보드 업데이트 태스크 시작 (간격:  " + (updateIntervalTicks / 20 / 60) + "분)");
    }

    @Override
    public void run() {
        try {
            // 리더보드 업데이트
            plugin.getLeaderboardManager().updateLeaderboard();

            // 시즌 종료 체크
            plugin.getSeasonManager().checkSeasonEnd();

        } catch (Exception e) {
            plugin.getLogger().severe("리더보드 업데이트 중 오류:  " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 수동 업데이트
     */
    public void updateNow() {
        run();
    }

    /**
     * 태스크 중지
     */
    public void stop() {
        if (! isCancelled()) {
            cancel();
        }
    }
}