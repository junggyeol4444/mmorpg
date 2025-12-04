package com.multiverse.dungeon.tasks;

import com. multiverse.dungeon.DungeonCore;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 시간 경고 태스크
 * 1초마다 실행되어 시간 초과 경고 전송
 */
public class TimeWarningTask extends BukkitRunnable {

    private final DungeonCore plugin;
    private final java.util.Set<java.util.UUID> warnedInstances; // 경고 전송한 인스턴스

    /**
     * 생성자
     */
    public TimeWarningTask(DungeonCore plugin) {
        this.plugin = plugin;
        this.warnedInstances = new java. util.HashSet<>();
    }

    /**
     * 시작
     */
    public void start() {
        this.runTaskTimer(plugin, 20L, 20L); // 1초마다 실행
    }

    @Override
    public void run() {
        try {
            var instances = plugin.getInstanceManager().getAllInstances();

            for (var instance : instances) {
                if (! instance.isActive()) {
                    warnedInstances.remove(instance.getInstanceId());
                    continue;
                }

                long remainingTime = instance.getRemainingTime();

                // 1분 경고
                if (remainingTime == 60 && !warnedInstances.contains(instance.getInstanceId())) {
                    broadcastWarning(instance, "§e⏰ 1분이 남았습니다!");
                    warnedInstances.add(instance.getInstanceId());
                }

                // 30초 경고
                if (remainingTime == 30) {
                    broadcastWarning(instance, "§c⏰ 30초 남았습니다!");
                }

                // 10초 경고
                if (remainingTime == 10) {
                    broadcastWarning(instance, "§4⏰ 10초 남았습니다!");
                }

                // 5초 경고
                if (remainingTime == 5) {
                    broadcastWarning(instance, "§4⏰ 5초 남았습니다!");
                }

                // 3초 경고
                if (remainingTime == 3) {
                    broadcastWarning(instance, "§4⏰ 3초 남았습니다!");
                }

                // 2초 경고
                if (remainingTime == 2) {
                    broadcastWarning(instance, "§4⏰ 2초 남았습니다!");
                }

                // 1초 경고
                if (remainingTime == 1) {
                    broadcastWarning(instance, "§4⏰ 1초 남았습니다!");
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("❌ 시간 경고 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 모든 플레이어에게 경고 메시지 전송
     */
    private void broadcastWarning(com.multiverse.dungeon.data.model.DungeonInstance instance, String message) {
        for (var playerId : instance.getPlayers()) {
            var player = org.bukkit. Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendMessage(message);
                
                // 사운드 효과 (선택)
                player.playSound(player.getLocation(), 
                    org.bukkit.Sound.BLOCK_NOTE_BLOCK_BELL, 
                    1. 0f, 1.0f);
            }
        }
    }
}