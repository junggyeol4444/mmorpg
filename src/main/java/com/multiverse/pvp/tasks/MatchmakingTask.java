package com.multiverse.pvp.tasks;

import com. multiverse.pvp.PvPCore;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchmakingTask extends BukkitRunnable {

    private final PvPCore plugin;
    private final long matchmakingIntervalTicks;

    public MatchmakingTask(PvPCore plugin) {
        this. plugin = plugin;

        // 설정에서 매칭 간격 로드 (초 단위)
        int matchmakingIntervalSeconds = plugin.getConfig().getInt("arenas.matchmaking.check-interval", 5);
        this.matchmakingIntervalTicks = matchmakingIntervalSeconds * 20L;
    }

    /**
     * 태스크 시작
     */
    public void start() {
        this.runTaskTimer(plugin, 40L, matchmakingIntervalTicks);
        plugin.getLogger().info("매칭메이킹 태스크 시작 (간격:  " + (matchmakingIntervalTicks / 20) + "초)");
    }

    @Override
    public void run() {
        try {
            // 매칭 처리
            plugin. getArenaManager().processMatchmaking();

        } catch (Exception e) {
            plugin.getLogger().severe("매칭 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 태스크 중지
     */
    public void stop() {
        if (!isCancelled()) {
            cancel();
        }
    }
}