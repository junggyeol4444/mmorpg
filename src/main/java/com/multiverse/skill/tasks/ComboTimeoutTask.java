package com.multiverse.skill. tasks;

import com.multiverse.skill.SkillCore;
import com.multiverse.skill.managers.ComboManager;
import com.multiverse.skill.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * 콤보 타임아웃 작업
 */
public class ComboTimeoutTask extends BukkitRunnable {

    private final SkillCore plugin;
    private final ComboManager comboManager;
    private final UUID playerUUID;
    private final Player player;
    private final long timeoutDuration;
    private long startTime;
    private int taskId;

    public ComboTimeoutTask(SkillCore plugin, ComboManager comboManager, Player player, 
                           long timeoutDuration) {
        this.plugin = plugin;
        this.comboManager = comboManager;
        this.player = player;
        this.playerUUID = player.getUniqueId();
        this.timeoutDuration = timeoutDuration;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (player == null || player.isOffline()) {
            cancel();
            return;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        if (elapsedTime >= timeoutDuration) {
            // 콤보 타임아웃
            timeoutCombo();
            cancel();
        }
    }

    /**
     * 콤보 타임아웃 처리
     */
    private void timeoutCombo() {
        if (player == null || ! player.isOnline()) {
            return;
        }

        // 콤보 상태 리셋
        comboManager. resetPlayerCombo(playerUUID);

        MessageUtils.sendMessage(player, "§c콤보가 끊겼습니다!");

        plugin.getLogger().info("콤보 타임아웃: " + player.getName());
    }

    /**
     * 작업 ID 설정
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * 플레이어 조회
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 경과 시간 조회
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 남은 시간 조회
     */
    public long getRemainingTime() {
        return Math.max(0, timeoutDuration - getElapsedTime());
    }
}