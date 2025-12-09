package com.multiverse.trade.tasks;

import com.multiverse. trade.TradeCore;
import com. multiverse.trade. managers.PriceTracker;
import org. bukkit.scheduler. BukkitRunnable;

public class PriceUpdateTask extends BukkitRunnable {

    private final TradeCore plugin;
    private final PriceTracker priceTracker;

    public PriceUpdateTask(TradeCore plugin) {
        this. plugin = plugin;
        this.priceTracker = plugin. getPriceTracker();
    }

    @Override
    public void run() {
        try {
            updatePrices();
        } catch (Exception e) {
            plugin.getLogger().severe("가격 업데이트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePrices() {
        priceTracker.updateAllPrices();
    }

    public void start() {
        int intervalMinutes = plugin.getConfig().getInt("price-tracking.update-interval", 5);
        long intervalTicks = intervalMinutes * 60L * 20L;
        
        this.runTaskTimerAsynchronously(plugin, intervalTicks, intervalTicks);
        plugin.getLogger().info("가격 업데이트 태스크 시작 (간격: " + intervalMinutes + "분)");
    }
}