package com.multiverse.trade.tasks;

import com.multiverse. trade.TradeCore;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSaveTask extends BukkitRunnable {

    private final TradeCore plugin;

    public AutoSaveTask(TradeCore plugin) {
        this. plugin = plugin;
    }

    @Override
    public void run() {
        saveAll();
    }

    public void saveAll() {
        try {
            plugin.getShopDataManager().saveAll();
            plugin.getAuctionDataManager().saveAll();
            plugin.getMarketDataManager().saveAll();
            plugin.getMailManager().saveAll();
            plugin.getPlayerTradeDataManager().saveAll();
            plugin.getPriceTracker().saveAll();
            
            plugin.getLogger().info("자동 저장 완료");
        } catch (Exception e) {
            plugin.getLogger().severe("자동 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void start() {
        int intervalMinutes = plugin.getConfig().getInt("storage.auto-save-interval", 5);
        long intervalTicks = intervalMinutes * 60L * 20L;
        
        this.runTaskTimerAsynchronously(plugin, intervalTicks, intervalTicks);
        plugin.getLogger().info("자동 저장 태스크 시작 (간격: " + intervalMinutes + "분)");
    }
}