package com. multiverse.pvp.tasks;

import com.multiverse. pvp.PvPCore;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSaveTask extends BukkitRunnable {

    private final PvPCore plugin;
    private final long saveIntervalTicks;
    private int saveCount;

    public AutoSaveTask(PvPCore plugin) {
        this. plugin = plugin;
        
        // 설정에서 저장 간격 로드 (분 단위)
        int saveIntervalMinutes = plugin.getConfig().getInt("auto-save.interval", 5);
        this.saveIntervalTicks = saveIntervalMinutes * 60L * 20L; // 분 -> 틱
        this. saveCount = 0;
    }

    /**
     * 태스크 시작
     */
    public void start() {
        this.runTaskTimerAsynchronously(plugin, saveIntervalTicks, saveIntervalTicks);
        plugin.getLogger().info("자동 저장 태스크 시작 (간격: " + (saveIntervalTicks / 20 / 60) + "분)");
    }

    @Override
    public void run() {
        saveCount++;

        try {
            // 플레이어 데이터 저장
            plugin.getPlayerDataStorage().saveAllPlayers();

            // 아레나 데이터 저장
            plugin.getArenaStorage().saveAllArenas();

            // 지역 데이터 저장
            plugin.getZoneStorage().saveAllZones();

            // 시즌 데이터 저장
            plugin.getSeasonStorage().saveCurrentSeason();

            // 10번째 저장마다 백업
            if (saveCount % 10 == 0) {
                plugin. getDataManager().createBackup();
            }

            if (plugin.getConfig().getBoolean("auto-save.log", true)) {
                plugin.getLogger().info("자동 저장 완료 (#" + saveCount + ")");
            }

        } catch (Exception e) {
            plugin.getLogger().severe("자동 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 태스크 중지
     */
    public void stop() {
        if (! isCancelled()) {
            cancel();
        }

        // 마지막 저장
        try {
            plugin. getPlayerDataStorage().saveAllPlayers();
            plugin.getArenaStorage().saveAllArenas();
            plugin.getZoneStorage().saveAllZones();
            plugin.getSeasonStorage().saveCurrentSeason();
            plugin.getLogger().info("종료 전 최종 저장 완료");
        } catch (Exception e) {
            plugin.getLogger().severe("종료 전 저장 중 오류:  " + e.getMessage());
        }
    }

    /**
     * 수동 저장 트리거
     */
    public void saveNow() {
        run();
    }

    /**
     * 저장 횟수 조회
     */
    public int getSaveCount() {
        return saveCount;
    }
}