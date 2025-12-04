package com.multiverse. dungeon.listeners;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse. dungeon.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit. event.EventPriority;
import org.bukkit.event.Listener;

/**
 * 던전 이벤트 리스너
 */
public class DungeonListener implements Listener {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public DungeonListener(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 던전 입장 이벤트 핸들러
     */
    @EventHandler(priority = EventPriority. NORMAL)
    public void onDungeonEnter(DungeonEnterEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var party = event.getParty();
        var dungeon = event.getDungeon();
        var difficulty = event.getDifficulty();

        // 로그 기록
        plugin.getLogger(). info("파티 " + party.getPartyId() + "가 던전 " + dungeon.getDungeonId() 
            + " (" + difficulty.getDisplayName() + ")에 입장했습니다.");

        // 데이터 기록
        for (var playerId : party.getMembers()) {
            var playerData = plugin.getDataManager().getPlayerData(playerId);
            if (playerData != null) {
                playerData.incrementTotalRuns();
                playerData.recordDailyEntry(dungeon.getDungeonId());
                playerData.recordWeeklyEntry(dungeon.getDungeonId());
            }
        }
    }

    /**
     * 던전 완료 이벤트 핸들러
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onDungeonComplete(DungeonCompleteEvent event) {
        var instance = event.getInstance();
        var clearTime = event.getClearTime();
        var score = event.getScore();

        plugin.getLogger().info("던전 인스턴스 " + instance.getInstanceId() 
            + "가 완료되었습니다. (클리어 시간: " + event.getClearTimeFormatted() + ", 점수: " + score + ")");

        // 플레이어별 기록 저장
        for (var playerId : instance.getPlayers()) {
            var playerData = plugin. getDataManager().getPlayerData(playerId);
            if (playerData != null) {
                playerData.incrementTotalClears();
                playerData.recordBestTime(instance.getDungeonId(), 
                    instance.getDifficulty().name(), clearTime);
                playerData.addDungeonPoints(score / instance.getPlayers().size());
            }
        }

        // 리더보드 업데이트
        plugin.getLeaderboardManager().updateRecord(instance, clearTime, score);
    }

    /**
     * 던전 실패 이벤트 핸들러
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onDungeonFail(DungeonFailEvent event) {
        var instance = event. getInstance();
        var reason = event.getReason();

        plugin.getLogger().warning("던전 인스턴스 " + instance. getInstanceId() 
            + "가 실패했습니다. (사유: " + reason + ")");

        // 플레이어별 데이터 기록
        for (var playerId : instance.getPlayers()) {
            var playerData = plugin.getDataManager(). getPlayerData(playerId);
            if (playerData != null) {
                // 사망 횟수는 PlayerListener에서 처리
            }
        }
    }

    /**
     * 보스 스폰 이벤트 핸들러
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBossSpawn(DungeonBossSpawnEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var boss = event.getBoss();
        var location = event.getSpawnLocation();

        plugin.getLogger().info("보스 '" + boss.getName() + "'가 스폰되었습니다.  위치: " 
            + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
    }

    /**
     * 보스 처치 이벤트 핸들러
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBossDefeated(DungeonBossDefeatedEvent event) {
        var boss = event.getBoss();
        var lastDamager = event.getLastDamager();
        var instance = event.getInstance();

        plugin.getLogger().info("보스 '" + boss.getName() + "'가 처치되었습니다. " 
            + (lastDamager != null ? "최후 일격: " + lastDamager.getName() : ""));

        // 진행도 업데이트
        instance.getProgress().incrementBossesKilled();

        // 보상 지급
        if (lastDamager != null) {
            var reward = boss.getReward();
            if (reward != null) {
                plugin.getRewardManager().giveReward(lastDamager, 
                    new com.multiverse.dungeon.data.model.DungeonReward());
            }
        }
    }
}