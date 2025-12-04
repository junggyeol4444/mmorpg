package com.multiverse.quest.tasks;

import com.multiverse. quest.managers.QuestDataManager;
import org.bukkit.  Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler. BukkitTask;
import java.util.*;

/**
 * 시간 제한 확인 태스크
 * 시간 제한이 있는 퀨스트의 시간을 체크하고 만료된 퀨스트를 처리합니다.
 */
public class TimeLimitCheckTask {
    private final QuestDataManager questDataManager;
    private final JavaPlugin plugin;
    private BukkitTask taskId;
    
    private static final long CHECK_INTERVAL = 20 * 60; // 1분 (틱 단위)
    private long lastCheckTime;
    private int checkCount;
    private int expiredQuestCount;

    /**
     * 생성자
     * @param questDataManager 퀨스트 데이터 관리자
     * @param plugin 플러그인 인스턴스
     */
    public TimeLimitCheckTask(QuestDataManager questDataManager, JavaPlugin plugin) {
        this.questDataManager = questDataManager;
        this.plugin = plugin;
        this.lastCheckTime = System.currentTimeMillis();
        this.checkCount = 0;
        this. expiredQuestCount = 0;
    }

    // ============ Task Control ============

    /**
     * 시간 제한 확인 태스크 시작
     */
    public void start() {
        if (taskId != null) {
            Bukkit.getLogger().warning("시간 제한 확인 태스크가 이미 실행 중입니다.");
            return;
        }

        taskId = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            try {
                performCheck();
            } catch (Exception e) {
                Bukkit.getLogger().warning("시간 제한 확인 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }, CHECK_INTERVAL, CHECK_INTERVAL);

        Bukkit.getLogger(). info("✓ 시간 제한 확인 태스크 시작됨 (간격: 1분)");
    }

    /**
     * 시간 제한 확인 태스크 중지
     */
    public void stop() {
        if (taskId != null) {
            taskId. cancel();
            taskId = null;
            Bukkit.getLogger().info("✓ 시간 제한 확인 태스크 중지됨");
        }
    }

    /**
     * 시간 제한 확인 수행
     */
    private void performCheck() {
        checkCount++;
        lastCheckTime = System.currentTimeMillis();

        int localExpiredCount = 0;

        // 모든 온라인 플레이어에 대해 확인
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                int playerExpiredCount = checkPlayerQuests(player);
                localExpiredCount += playerExpiredCount;
            } catch (Exception e) {
                Bukkit. getLogger().warning("플레이어 " + player.getName() + " 시간 제한 확인 실패: " + e.getMessage());
            }
        }

        if (localExpiredCount > 0) {
            expiredQuestCount += localExpiredCount;
            Bukkit.getLogger().info("§e⏰ 시간 제한 만료된 퀨스트: " + localExpiredCount + "개");
        }
    }

    /**
     * 플레이어 퀨스트 시간 제한 확인
     */
    private int checkPlayerQuests(Player player) {
        UUID playerUUID = player.getUniqueId();
        List<com.multiverse.quest.models.PlayerQuest> playerQuests = questDataManager.getPlayerInProgressQuests(playerUUID);
        
        int expiredCount = 0;

        for (com.multiverse. quest.models.PlayerQuest pq : playerQuests) {
            com.multiverse.quest.models. Quest quest = questDataManager.getQuest(pq.getQuestId());
            
            if (quest == null) continue;

            // 시간 제한이 있는지 확인
            if (quest.getTimeLimit() > 0) {
                // 경과 시간 확인
                long elapsedTime = System.currentTimeMillis() - pq.getAcceptedTime();
                long timeLimitMillis = quest.getTimeLimit() * 1000; // 초 단위를 밀리초로 변환

                if (elapsedTime >= timeLimitMillis) {
                    // 시간 초과
                    handleTimeExpired(player, quest, pq);
                    expiredCount++;
                } else {
                    // 시간 제한 경고 표시
                    long remainingTime = timeLimitMillis - elapsedTime;
                    long remainingSeconds = remainingTime / 1000;

                    if (remainingSeconds <= 300) { // 5분 이내 남았을 때 경고
                        showTimeWarning(player, quest, remainingSeconds);
                    }
                }
            }
        }

        return expiredCount;
    }

    /**
     * 시간 초과 처리
     */
    private void handleTimeExpired(Player player, com.multiverse. quest.models.Quest quest, com.multiverse.quest.models. PlayerQuest pq) {
        player.sendMessage("§c⏰ 퀨스트 시간 초과: " + quest.getName());
        
        // 퀨스트 자동 포기 처리
        questDataManager.getQuestProgressManager().abandonQuest(player. getUniqueId(), pq.getQuestId());
        
        player.sendMessage("§7해당 퀨스트가 자동으로 포기되었습니다.");
    }

    /**
     * 시간 제한 경고 표시
     */
    private void showTimeWarning(Player player, com.multiverse.quest.models.Quest quest, long remainingSeconds) {
        String timeWarning;
        
        if (remainingSeconds <= 60) {
            timeWarning = "§c" + remainingSeconds + "초";
        } else if (remainingSeconds <= 300) {
            long minutes = remainingSeconds / 60;
            long seconds = remainingSeconds % 60;
            timeWarning = "§e" + minutes + "분 " + seconds + "초";
        } else {
            return;
        }

        player. sendMessage("§6⏰ [" + quest.getName() + "] 제한 시간: " + timeWarning + " 남음");
    }

    // ============ Manual Check ============

    /**
     * 특정 플레이어 시간 제한 확인
     */
    public void checkPlayer(Player player) {
        try {
            checkPlayerQuests(player);
        } catch (Exception e) {
            Bukkit.getLogger(). warning("플레이어 " + player.getName() + " 시간 제한 확인 실패: " + e. getMessage());
        }
    }

    /**
     * 모든 플레이어 시간 제한 강제 확인
     */
    public void forceCheckAll() {
        for (Player player : Bukkit. getOnlinePlayers()) {
            checkPlayer(player);
        }
    }

    // ============ Statistics ============

    /**
     * 확인 횟수 반환
     */
    public int getCheckCount() {
        return checkCount;
    }

    /**
     * 마지막 확인 시간 반환
     */
    public long getLastCheckTime() {
        return lastCheckTime;
    }

    /**
     * 마지막 확인 이후 경과 시간 (밀리초)
     */
    public long getTimeSinceLastCheck() {
        return System.currentTimeMillis() - lastCheckTime;
    }

    /**
     * 확인 간격 (밀리초)
     */
    public long getCheckInterval() {
        return CHECK_INTERVAL * 50; // 틱을 밀리초로 변환
    }

    /**
     * 만료된 퀨스트 총 개수 반환
     */
    public int getTotalExpiredQuests() {
        return expiredQuestCount;
    }

    /**
     * 상태 정보 반환
     */
    public String getStatusInfo() {
        StringBuilder sb = new StringBuilder();
        sb. append("§6=== 시간 제한 확인 태스크 상태 ===§r\n");
        sb.append("§7상태: ").append(isRunning() ? "§a실행 중" : "§c중지됨").append("\n");
        sb.append("§7확인 횟수: §f").append(checkCount).append("\n");
        sb.append("§7마지막 확인: §f").append(getTimeSinceLastCheck()).append("ms 전\n");
        sb.append("§7확인 간격: §f1분\n");
        sb.append("§7만료된 퀨스트: §f").append(expiredQuestCount).  append("개\n");

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