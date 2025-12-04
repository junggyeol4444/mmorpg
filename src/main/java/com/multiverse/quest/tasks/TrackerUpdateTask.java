package com.multiverse.quest.tasks;

import com.multiverse.  quest.managers.QuestDataManager;
import org.bukkit.  Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import java.util.*;

/**
 * 추적기 업데이트 태스크
 * 플레이어의 추적 중인 퀨스트 정보를 주기적으로 업데이트합니다.
 */
public class TrackerUpdateTask {
    private final QuestDataManager questDataManager;
    private final JavaPlugin plugin;
    private BukkitTask taskId;
    
    private static final long UPDATE_INTERVAL = 20; // 1초 (틱 단위)
    private long lastUpdateTime;
    private int updateCount;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     * @param plugin 플러그인 인스턴스
     */
    public TrackerUpdateTask(QuestDataManager questDataManager, JavaPlugin plugin) {
        this.  questDataManager = questDataManager;
        this. plugin = plugin;
        this. lastUpdateTime = System.currentTimeMillis();
        this.updateCount = 0;
    }

    // ============ Task Control ============

    /**
     * 추적기 업데이트 태스크 시작
     */
    public void start() {
        if (taskId != null) {
            Bukkit.getLogger().warning("추적기 업데이트 태스크가 이미 실행 중입니다.");
            return;
        }

        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                performUpdate();
            } catch (Exception e) {
                Bukkit.getLogger().warning("추적기 업데이트 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }, UPDATE_INTERVAL, UPDATE_INTERVAL);

        Bukkit.getLogger(). info("✓ 추적기 업데이트 태스크 시작됨 (간격: 1초)");
    }

    /**
     * 추적기 업데이트 태스크 중지
     */
    public void stop() {
        if (taskId != null) {
            taskId. cancel();
            taskId = null;
            Bukkit.getLogger().info("✓ 추적기 업데이트 태스크 중지됨");
        }
    }

    /**
     * 업데이트 수행
     */
    private void performUpdate() {
        updateCount++;
        lastUpdateTime = System.currentTimeMillis();

        // 모든 온라인 플레이어에 대해 업데이트
        for (Player player : Bukkit. getOnlinePlayers()) {
            try {
                updatePlayerTracker(player);
            } catch (Exception e) {
                Bukkit.getLogger().warning("플레이어 " + player.getName() + " 추적기 업데이트 실패: " + e.getMessage());
            }
        }
    }

    /**
     * 플레이어 추적기 업데이트
     */
    private void updatePlayerTracker(Player player) {
        UUID playerUUID = player.getUniqueId();

        // 추적 중인 퀨스트 조회
        String trackedQuestId = questDataManager.getTrackerManager().getTrackedQuestId(playerUUID);

        if (trackedQuestId == null || trackedQuestId.isEmpty()) {
            return; // 추적 중인 퀨스트 없음
        }

        // 퀨스트 정보 조회
        com.multiverse.quest.models.Quest quest = questDataManager. getQuest(trackedQuestId);
        if (quest == null) {
            return;
        }

        // 플레이어 퀨스트 정보 조회
        com.multiverse.quest.models. PlayerQuest playerQuest = questDataManager.getPlayerQuest(playerUUID, trackedQuestId);
        if (playerQuest == null) {
            return;
        }

        // 액션바에 진행도 표시
        displayTrackerToActionbar(player, quest, playerQuest);
    }

    /**
     * 액션바에 추적기 정보 표시
     */
    private void displayTrackerToActionbar(Player player, com.multiverse.quest.models.Quest quest, com.multiverse.quest. models.PlayerQuest playerQuest) {
        StringBuilder actionbar = new StringBuilder();
        actionbar.append("§6【 퀨스트: ").append(quest.getName()). append(" 】§r ");

        // 목표 진행도 표시
        if (quest.getObjectives() != null && ! quest.getObjectives().isEmpty()) {
            int completedObjectives = 0;
            for (com. multiverse.quest.models.QuestObjective objective : quest.getObjectives()) {
                int progress = playerQuest.getObjectiveProgress(objective. getObjectiveId());
                if (progress >= objective.getTargetProgress()) {
                    completedObjectives++;
                }
            }

            int totalObjectives = quest.getObjectives().size();
            double progressPercent = (completedObjectives * 100.0) / totalObjectives;

            actionbar.append("§7[");
            
            // 진행 바 표시
            int filledBars = (int) (progressPercent / 10);
            for (int i = 0; i < 10; i++) {
                if (i < filledBars) {
                    actionbar.append("§a█");
                } else {
                    actionbar.append("§7█");
                }
            }

            actionbar.append("§7] ");
            actionbar.append("§f").append(completedObjectives). append("/").append(totalObjectives);
        }

        // 액션바 전송
        player.sendActionBar(actionbar.toString());
    }

    // ============ Manual Update ============

    /**
     * 특정 플레이어 추적기 업데이트
     */
    public void updatePlayer(Player player) {
        try {
            updatePlayerTracker(player);
        } catch (Exception e) {
            Bukkit.getLogger().warning("플레이어 " + player.getName() + " 추적기 업데이트 실패: " + e. getMessage());
        }
    }

    /**
     * 모든 플레이어 추적기 강제 업데이트
     */
    public void forceUpdateAll() {
        for (Player player : Bukkit. getOnlinePlayers()) {
            updatePlayer(player);
        }
    }

    // ============ Statistics ============

    /**
     * 업데이트 횟수 반환
     */
    public int getUpdateCount() {
        return updateCount;
    }

    /**
     * 마지막 업데이트 시간 반환
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * 마지막 업데이트 이후 경과 시간 (밀리초)
     */
    public long getTimeSinceLastUpdate() {
        return System.currentTimeMillis() - lastUpdateTime;
    }

    /**
     * 업데이트 간격 (밀리초)
     */
    public long getUpdateInterval() {
        return UPDATE_INTERVAL * 50; // 틱을 밀리초로 변환
    }

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 추적기 업데이트 태스크 상태 ===§r\n");
        sb. append("§7상태: ").append(isRunning() ? "§a실행 중" : "§c중지됨").append("\n");
        sb.append("§7업데이트 횟수: §f").append(updateCount).append("\n");
        sb.append("§7마지막 업데이트: §f").  append(getTimeSinceLastUpdate()).  append("ms 전\n");
        sb.append("§7업데이트 간격: §f1초\n");

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