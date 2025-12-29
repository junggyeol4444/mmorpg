package com.multiverse.pet. task;

import com.multiverse.pet.PetCore;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * 자동 저장 태스크
 * 주기적으로 플레이어 데이터 저장
 */
public class AutoSaveTask extends BukkitRunnable {

    private final PetCore plugin;

    // 설정
    private long saveIntervalTicks;
    private boolean saveOnlyOnline;
    private boolean logSaves;

    // 통계
    private int totalSaves;
    private long lastSaveTime;
    private long lastSaveDuration;

    public AutoSaveTask(PetCore plugin) {
        this. plugin = plugin;
        this.totalSaves = 0;
        this. lastSaveTime = 0;
        this.lastSaveDuration = 0;
        loadSettings();
    }

    /**
     * 설정 로드
     */
    private void loadSettings() {
        var config = plugin.getConfigManager();
        this.saveIntervalTicks = config.getAutoSaveInterval() * 20L * 60L; // 분 -> 틱
        this.saveOnlyOnline = config. isSaveOnlyOnline();
        this.logSaves = config.isLogAutoSaves();
    }

    @Override
    public void run() {
        performAutoSave();
    }

    /**
     * 자동 저장 실행
     */
    private void performAutoSave() {
        long startTime = System.currentTimeMillis();

        int savedCount = 0;

        // 온라인 플레이어 저장
        for (Player player :  Bukkit.getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();

            try {
                plugin.getPetDataManager().savePlayerData(playerId);
                savedCount++;
            } catch (Exception e) {
                plugin.getLogger().warning("자동 저장 실패 (" + player.getName() + "): " + e.getMessage());
            }
        }

        // 대기 중인 저장도 처리
        plugin.getPetDataManager().savePendingData();

        long endTime = System.currentTimeMillis();
        lastSaveDuration = endTime - startTime;
        lastSaveTime = endTime;
        totalSaves++;

        // 로그
        if (logSaves) {
            plugin.getLogger().info("[AutoSave] " + savedCount + "명 저장 완료 (" + lastSaveDuration + "ms)");
        }

        if (plugin.isDebugMode()) {
            plugin.getLogger().info("[DEBUG] 자동 저장 #" + totalSaves + 
                    " - " + savedCount + "명, " + lastSaveDuration + "ms");
        }
    }

    /**
     * 즉시 저장 (수동 호출용)
     */
    public void saveNow() {
        performAutoSave();
    }

    /**
     * 특정 플레이어 저장
     */
    public void savePlayer(UUID playerId) {
        try {
            plugin. getPetDataManager().savePlayerData(playerId);

            if (plugin. isDebugMode()) {
                plugin. getLogger().info("[DEBUG] 플레이어 저장:  " + playerId);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("플레이어 저장 실패 (" + playerId + "): " + e.getMessage());
        }
    }

    /**
     * 모든 데이터 저장 (종료 시)
     */
    public void saveAll() {
        plugin. getLogger().info("모든 펫 데이터 저장 중...");

        long startTime = System. currentTimeMillis();

        try {
            plugin. getPetDataManager().saveAllData();

            long duration = System.currentTimeMillis() - startTime;
            plugin.getLogger().info("펫 데이터 저장 완료 (" + duration + "ms)");

        } catch (Exception e) {
            plugin.getLogger().severe("펫 데이터 저장 실패:  " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 비동기 저장
     */
    public void saveAsync(UUID playerId) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            savePlayer(playerId);
        });
    }

    /**
     * 비동기 전체 저장
     */
    public void saveAllAsync(Runnable callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            saveAll();
            if (callback != null) {
                Bukkit.getScheduler().runTask(plugin, callback);
            }
        });
    }

    // ===== 통계 =====

    /**
     * 총 저장 횟수
     */
    public int getTotalSaves() {
        return totalSaves;
    }

    /**
     * 마지막 저장 시간
     */
    public long getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * 마지막 저장 소요 시간
     */
    public long getLastSaveDuration() {
        return lastSaveDuration;
    }

    /**
     * 다음 저장까지 남은 시간 (밀리초)
     */
    public long getTimeUntilNextSave() {
        if (lastSaveTime == 0) {
            return saveIntervalTicks * 50; // 틱 -> ms
        }

        long interval = saveIntervalTicks * 50;
        long elapsed = System.currentTimeMillis() - lastSaveTime;
        return Math.max(0, interval - elapsed);
    }

    /**
     * 통계 문자열
     */
    public String getStatsString() {
        return String.format(
                "저장 횟수: %d, 마지막 저장: %dms 소요, 다음 저장:  %d초 후",
                totalSaves,
                lastSaveDuration,
                getTimeUntilNextSave() / 1000
        );
    }

    /**
     * 설정 리로드
     */
    public void reloadSettings() {
        loadSettings();
    }

    /**
     * 태스크 시작
     */
    public void start() {
        // 설정된 간격으로 실행 (기본 5분)
        this.runTaskTimerAsynchronously(plugin, saveIntervalTicks, saveIntervalTicks);

        plugin.getLogger().info("자동 저장 태스크 시작 (간격: " + (saveIntervalTicks / 20 / 60) + "분)");
    }

    /**
     * 태스크 중지
     */
    public void stop() {
        try {
            this.cancel();
        } catch (IllegalStateException ignored) {
        }

        // 종료 전 최종 저장
        saveAll();
    }
}