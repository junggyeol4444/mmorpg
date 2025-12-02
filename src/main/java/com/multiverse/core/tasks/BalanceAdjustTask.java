package com.multiverse.core.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.multiverse.core.manager.BalanceManager;

public class BalanceAdjustTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final BalanceManager balanceManager;

    public BalanceAdjustTask(JavaPlugin plugin, BalanceManager balanceManager) {
        this.plugin = plugin;
        this.balanceManager = balanceManager;
    }

    /**
     * 주기적으로 모든 플레이어의 밸런스를 조정합니다.
     */
    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            double newBalance = balanceManager.calculateAdjustedBalance(player);
            balanceManager.setBalance(player, newBalance);
        });
    }
}