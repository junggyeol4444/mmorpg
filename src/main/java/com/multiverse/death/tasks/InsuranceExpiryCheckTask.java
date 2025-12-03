package com.multiverse.death.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import com.multiverse.death.DeathAndRebirthPlugin;

/**
 * 보험 만료를 주기적으로 확인하는 태스크
 */
public class InsuranceExpiryCheckTask extends BukkitRunnable {

    private final DeathAndRebirthPlugin plugin;

    public InsuranceExpiryCheckTask(DeathAndRebirthPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        // 모든 플레이어의 보험 만료 여부를 확인하여 만료 처리
        plugin.getPlayerManager().getAllPlayers().forEach(player -> {
            if (plugin.getInsuranceManager().isInsuranceExpired(player)) {
                plugin.getInsuranceManager().expireInsurance(player);
                plugin.getLogger().info("[DeathAndRebirth] " + player.getName() + "의 보험이 만료되었습니다.");
            }
        });
    }
}