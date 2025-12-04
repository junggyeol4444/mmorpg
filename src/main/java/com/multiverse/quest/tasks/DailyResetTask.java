package com.multiverse.quest.tasks;

import com.multiverse.  quest.managers.QuestDataManager;
import org.bukkit.  Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.  BukkitTask;
import java.util.*;
import java.time.LocalDateTime;
import java. time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * 일일 퀨스트 초기화 태스크
 * 매일 자정에 일일 퀨스트 데이터를 초기화합니다.
 */
public class DailyResetTask {
    private final QuestDataManager questDataManager;
    private final JavaPlugin plugin;
    private BukkitTask taskId;
    
    private long lastResetTime;
    private int resetCount;
    private int playersAffected;

    private static final LocalTime RESET_TIME = LocalTime.of(0, 0, 0); // 자정

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     * @param plugin 플러그인 인스턴스
     */
    public DailyResetTask(QuestDataManager questDataManager, JavaPlugin plugin) {
        this.  questDataManager = questDataManager;
        this. plugin = plugin;
        this. lastResetTime = System.currentTimeMillis();
        this. resetCount = 0;
        this.  playersAffected = 0;
    }

    // ============ Task Control ============

    /**
     * 일일 초기화 태스크 시작
     */
    public void start() {
        if (taskId != null) {
            Bukkit.getLogger().warning("일일 초기화 태스크가 이미 실행 중입니다.");
            return;
        }

        // 다음 자정까지의 시간 계산
        long delayUntilMidnight = calculateDelayUntilMidnight();

        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                performReset();
            } catch (Exception e) {
                Bukkit.getLogger().warning("일일 초기화 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }, delayUntilMidnight, 24 * 60 * 20); // 24시간마다 반복 (틱 단위)

        Bukkit. getLogger().  info("✓ 일일 초기화 태스크 시작됨 (다음 초기화: " + delayUntilMidnight + "틱 후)");
    }

    /**
     * 일일 초기화 태스크 중지
     */
    public void stop() {
        if (taskId != null) {
            taskId.  cancel();
            taskId = null;
            Bukkit.getLogger().info("✓ 일일 초기화 태스크 중지됨");
        }
    }

    /**
     * 초기화 수행
     */
    private void performReset() {
        long startTime = System.currentTimeMillis();

        try {
            resetCount++;
            int affectedPlayers = 0;

            // 모든 온라인 플레이어 초기화
            for (Player player : Bukkit.  getOnlinePlayers()) {
                try {
                    resetPlayerDaily(player);
                    affectedPlayers++;
                } catch (Exception e) {
                    Bukkit.  getLogger().warning("플레이어 " + player.getName() + " 일일 초기화 실패: " + e.getMessage());
                }
            }

            playersAffected += affectedPlayers;

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            Bukkit.getLogger(). info("§a✓ 일일 초기화 완료 (" + resetCount + "회, " + affectedPlayers + "명, " + duration + "ms)");

            // 모든 플레이어에게 알림
            Bukkit.  broadcastMessage("§6【 일일 퀨스트가 초기화되었습니다 】");

        } catch (Exception e) {
            Bukkit.getLogger(). severe("일일 초기화 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 플레이어 일일 퀨스트 초기화
     */
    private void resetPlayerDaily(Player player) {
        UUID playerUUID = player.getUniqueId();

        // 플레이어의 일일 퀨스트 진행도 초기화
        questDataManager.getDailyWeeklyManager().resetPlayerDaily(playerUUID);

        // 플레이어에게 알림
        player.sendMessage("§a【 일일 퀨스트가 초기화되었습니다 】");
        player.sendMessage("§7/quest list daily를 통해 새로운 일일 퀨스트를 확인하세요.");
    }

    // ============ Time Calculation ============

    /**
     * 다음 자정까지의 시간 계산 (틱 단위)
     */
    private long calculateDelayUntilMidnight() {
        LocalDateTime now = LocalDateTime. now();
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();

        long secondsUntilMidnight = ChronoUnit.SECONDS.between(now, nextMidnight);
        long ticksUntilMidnight = secondsUntilMidnight * 20; // 1초 = 20틱

        return ticksUntilMidnight;
    }

    /**
     * 다음 자정까지의 시간 (초 단위)
     */
    public long getSecondsUntilNextReset() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();

        return ChronoUnit.SECONDS. between(now, nextMidnight);
    }

    // ============ Manual Reset ============

    /**
     * 즉시 초기화 수행
     */
    public void resetNow() {
        try {
            performReset();
        } catch (Exception e) {
            Bukkit.getLogger().warning("즉시 초기화 실패: " + e.getMessage());
        }
    }

    /**
     * 특정 플레이어 초기화
     */
    public void resetPlayer(Player player) {
        try {
            resetPlayerDaily(player);
        } catch (Exception e) {
            Bukkit.getLogger(). warning("플레이어 " + player.getName() + " 초기화 실패: " + e.  getMessage());
        }
    }

    /**
     * 모든 플레이어 강제 초기화
     */
    public void forceResetAll() {
        for (Player player : Bukkit.  getOnlinePlayers()) {
            resetPlayer(player);
        }
    }

    // ============ Statistics ============

    /**
     * 초기화 횟수 반환
     */
    public int getResetCount() {
        return resetCount;
    }

    /**
     * 마지막 초기화 시간 반환
     */
    public long getLastResetTime() {
        return lastResetTime;
    }

    /**
     * 영향받은 플레이어 총 수
     */
    public int getTotalPlayersAffected() {
        return playersAffected;
    }

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== 일일 초기화 태스크 상태 ===§r\n");
        sb.append("§7상태: ").append(isRunning() ? "§a실행 중" : "§c중지됨").append("\n");
        sb.append("§7초기화 횟수: §f").append(resetCount).append("\n");
        sb.  append("§7영향받은 플레이어: §f").append(playersAffected).  append("명\n");
        sb.  append("§7다음 초기화: §f").append(getSecondsUntilNextReset()). append("초 후\n");
        sb. append("§7초기화 시간: §f매일 자정 (00:00)\n");

        return sb.toString();
    }

    // ============ Status Check ============

    /**
     * 태스크 실행 여부 확인
     */
    public boolean isRunning() {
        return taskId != null && !  taskId.isCancelled();
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