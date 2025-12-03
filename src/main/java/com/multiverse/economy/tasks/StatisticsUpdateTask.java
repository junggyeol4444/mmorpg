package com.multiverse.economy.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import com.multiverse.economy.models.EconomyStatistics;
import com.multiverse.economy.utils.ConfigUtil;

public class StatisticsUpdateTask extends BukkitRunnable {

    private final EconomyStatistics statistics;

    public StatisticsUpdateTask(EconomyStatistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public void run() {
        statistics.setTransactionCount(ConfigUtil.getTransactionCount());
        statistics.setTotalBalance(ConfigUtil.getTotalBalance());
        statistics.setTotalLoans(ConfigUtil.getTotalLoans());
        statistics.setTotalBurned(ConfigUtil.getTotalBurned());
        statistics.setInflationRate(ConfigUtil.getInflationRate());
    }
}