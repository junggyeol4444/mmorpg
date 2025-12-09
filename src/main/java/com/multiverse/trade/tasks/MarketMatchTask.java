package com.multiverse.trade.tasks;

import com. multiverse.trade. TradeCore;
import com.multiverse.trade.managers.MarketManager;
import org.bukkit.scheduler.BukkitRunnable;

public class MarketMatchTask extends BukkitRunnable {

    private final TradeCore plugin;
    private final MarketManager marketManager;

    public MarketMatchTask(TradeCore plugin) {
        this. plugin = plugin;
        this.marketManager = plugin.getMarketManager();
    }

    @Override
    public void run() {
        try {
            matchOrders();
            checkExpiredOrders();
        } catch (Exception e) {
            plugin. getLogger().severe("거래소 매칭 중 오류 발생:  " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void matchOrders() {
        if (! plugin.getConfig().getBoolean("market. matching.auto-match", true)) {
            return;
        }

        marketManager.matchOrders();
    }

    private void checkExpiredOrders() {
        marketManager.checkExpiredOrders();
    }

    public void start() {
        int intervalSeconds = plugin.getConfig().getInt("market.matching.interval", 30);
        long intervalTicks = intervalSeconds * 20L;
        
        this.runTaskTimer(plugin, intervalTicks, intervalTicks);
        plugin.getLogger().info("거래소 매칭 태스크 시작 (간격: " + intervalSeconds + "초)");
    }
}