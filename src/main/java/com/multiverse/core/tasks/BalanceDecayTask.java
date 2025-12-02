package com.multiverse.core.tasks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.multiverse.core.manager.BalanceManager;

/**
 * 일정 주기로 모든 플레이어의 밸런스를 감소(디케이)시키는 작업
 */
public class BalanceDecayTask extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final BalanceManager balanceManager;
    private final double decayRate;

    public BalanceDecayTask(JavaPlugin plugin, BalanceManager balanceManager, double decayRate) {
        this.plugin = plugin;
        this.balanceManager = balanceManager;
        this.decayRate = decayRate;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            double current = balanceManager.getBalance(player);
            double decayed = current * (1.0 - decayRate);
            balanceManager.setBalance(player, decayed);
        });
    }
}