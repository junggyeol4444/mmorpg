package com.multiverse.dungeon.tasks;

import com.multiverse.dungeon.DungeonCore;
import com.multiverse.  dungeon.data.  enums. InstanceStatus;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 인스턴스 틱 태스크
 * 매 초마다 실행되어 인스턴스 상태 업데이트
 */
public class InstanceTickTask extends BukkitRunnable {

    private final DungeonCore plugin;

    /**
     * 생성자
     */
    public InstanceTickTask(DungeonCore plugin) {
        this.plugin = plugin;
    }

    /**
     * 시작
     */
    public void start() {
        this.runTaskTimer(plugin, 20L, 20L); // 1초마다 실행
    }

    @Override
    public void run() {
        var instances = plugin.getInstanceManager().getAllInstances();

        for (var instance : instances) {
            if (! instance.isActive()) {
                continue;
            }

            // 시간 제한 확인
            if (instance.isTimeLimitExceeded()) {
                plugin.getLogger().info("⏰ 인스턴스 " + instance.getInstanceId() 
                    + "의 시간이 초과되었습니다.");
                
                instance.setStatus(InstanceStatus. EXPIRED);
                
                var failEvent = new com.multiverse.dungeon.events.DungeonFailEvent(instance, 
                    com.multiverse.dungeon.events.DungeonFailEvent.  FailReason.TIME_EXPIRED);
                org.bukkit. Bukkit.  getPluginManager().callEvent(failEvent);

                // 모든 파티원에게 알림
                for (var playerId : instance.  getPlayers()) {
                    var player = org.bukkit.Bukkit. getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        player.sendMessage("§c⏰ 시간이 초과되어 던전에서 방출되었습니다!");
                    }
                }
            }

            // 시간 경고 (30초 전)
            long remainingTime = instance.getRemainingTime();
            if (remainingTime == 30) {
                for (var playerId : instance.getPlayers()) {
                    var player = org.bukkit. Bukkit.  getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        player.sendMessage("§e⚠️ 30초 남았습니다!");
                    }
                }
            }

            // 시간 경고 (10초 전)
            if (remainingTime == 10) {
                for (var playerId : instance.getPlayers()) {
                    var player = org.  bukkit.Bukkit.getPlayer(playerId);
                    if (player != null && player. isOnline()) {
                        player.sendMessage("§c⚠️ 10초 남았습니다!");
                    }
                }
            }

            // 진행도 업데이트
            var progress = instance.getProgress();
            
            // 플레이어 HUD 업데이트 (필요시)
            for (var playerId : instance.getPlayers()) {
                var player = org.bukkit.  Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    // ActionBar에 시간 표시
                    player.sendActionBar("§b⏱️ " + instance.getRemainingTimeFormatted() 
                        + " | §6📊 진행도: " + String.format("%.1f", progress.getProgress()) + "%");
                }
            }
        }
    }

    /**
     * 모든 인스턴스 조회
     */
    private java.util.List<com.multiverse.dungeon.data.model.DungeonInstance> getAllInstances() {
        var allInstances = new java.util. ArrayList<com.multiverse.dungeon.data.model.DungeonInstance>();
        // 여기서는 간단하게 빈 리스트 반환
        // 실제로는 InstanceManager에서 모든 인스턴스를 가져와야 함
        return allInstances;
    }
}