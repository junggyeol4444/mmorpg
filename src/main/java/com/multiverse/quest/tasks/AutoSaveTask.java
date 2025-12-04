package com.multiverse.quest.tasks;

import com.multiverse. quest.managers.QuestDataManager;
import org.bukkit. Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import java.util.*;

/**
 * 자동 저장 태스크
 * 일정 시간마다 모든 퀨스트 데이터를 자동으로 저장합니다. 
 */
public class AutoSaveTask {
    private final QuestDataManager questDataManager;
    private final JavaPlugin plugin;
    private BukkitTask taskId;
    
    private static final long SAVE_INTERVAL = 20 * 60 * 20; // 20분 (틱 단위)
    private long lastSaveTime;
    private int saveCount;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     * @param plugin 플러그인 인스턴스
     */
    public AutoSaveTask(QuestDataManager questDataManager, JavaPlugin plugin) {
        this. questDataManager = questDataManager;
        this.plugin = plugin;
        this.lastSaveTime = System.currentTimeMillis();
        this.saveCount = 0;
    }

    // ============ Task Control ============

    /**
     * 자동 저장 태스크 시작
     */
    public void start() {
        if (taskId != null) {
            Bukkit.getLogger().warning("자동 저장 태스크가 이미 실행 중입니다.");
            return;
        }

        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                performSave();
            } catch (Exception e) {
                Bukkit.getLogger().warning("자동 저장 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }, SAVE_INTERVAL, SAVE_INTERVAL);

        Bukkit.getLogger(). info("✓ 자동 저장 태스크 시작됨 (간격: 20분)");
    }

    /**
     * 자동 저장 태스크 중지
     */
    public void stop() {
        if (taskId != null) {
            taskId.cancel();
            taskId = null;
            Bukkit.getLogger().info("✓ 자동 저장 태스크 중지됨");
        }
    }

    /**
     * 저장 수행
     */
    private void performSave() {
        long startTime = System.currentTimeMillis();

        try {
            // 모든 데이터 저장
            questDataManager.saveAll();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            saveCount++;
            lastSaveTime = endTime;

            Bukkit.getLogger(). info("§a✓ 자동 저장 완료 (" + saveCount + "회, " + duration + "ms)");

        } catch (Exception e) {
            Bukkit.getLogger().severe("자동 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============ Manual Save ============

    /**
     * 수동 저장 (즉시 저장)
     */
    public boolean saveNow() {
        try {
            long startTime = System.currentTimeMillis();

            questDataManager.saveAll();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            lastSaveTime = endTime;

            Bukkit.getLogger(). info("✓ 수동 저장 완료 (" + duration + "ms)");
            return true;

        } catch (Exception e) {
            Bukkit.getLogger().warning("수동 저장 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * 비동기 저장
     */
    public void saveAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                questDataManager.saveAll();
                Bukkit.getLogger().info("✓ 비동기 저장 완료");
            } catch (Exception e) {
                Bukkit.getLogger().warning("비동기 저장 실패: " + e.getMessage());
            }
        });
    }

    // ============ Statistics ============

    /**
     * 저장 횟수 반환
     */
    public int getSaveCount() {
        return saveCount;
    }

    /**
     * 마지막 저장 시간 반환
     */
    public long getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * 마지막 저장 이후 경과 시간 (밀리초)
     */
    public long getTimeSinceLastSave() {
        return System.currentTimeMillis() - lastSaveTime;
    }

    /**
     * 마지막 저장 이후 경과 시간 (초)
     */
    public long getSecondsSinceLastSave() {
        return getTimeSinceLastSave() / 1000;
    }

    /**
     * 저장 간격 (밀리초)
     */
    public long getSaveInterval() {
        return SAVE_INTERVAL * 50; // 틱을 밀리초로 변환 (1틱 = 50ms)
    }

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 자동 저장 태스크 상태 ===§r\n");
        sb.append("§7상태: ").append(isRunning() ? "§a실행 중" : "§c중지됨").append("\n");
        sb.append("§7저장 횟수: §f").append(saveCount).append("\n");
        sb.append("§7마지막 저장: §f"). append(getSecondsSinceLastSave()). append("초 전\n");
        sb.append("§7저장 간격: §f20분\n");

        return sb.toString();
    }

    // ============ Status Check ============

    /**
     * 태스크 실행 여부 확인
     */
    public boolean isRunning() {
        return taskId != null && ! taskId.isCancelled();
    }

    /**
     * 데이터 관리자 반환
     */
    public QuestDataManager getQuestDataManager() {
        return questDataManager;
    }

    /**
     * 플러그인 인스턴스 반환
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }
}