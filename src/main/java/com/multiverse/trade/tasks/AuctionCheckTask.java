package com. multiverse.trade. tasks;

import com.multiverse.trade.TradeCore;
import com.multiverse. trade.managers.AuctionManager;
import com.multiverse.trade.models.Auction;
import com.multiverse.trade.models.AuctionStatus;
import org. bukkit.scheduler. BukkitRunnable;

import java.util.List;

public class AuctionCheckTask extends BukkitRunnable {

    private final TradeCore plugin;
    private final AuctionManager auctionManager;

    public AuctionCheckTask(TradeCore plugin) {
        this. plugin = plugin;
        this.auctionManager = plugin. getAuctionManager();
    }

    @Override
    public void run() {
        checkExpiredAuctions();
    }

    private void checkExpiredAuctions() {
        try {
            List<Auction> activeAuctions = auctionManager.getActiveAuctions();
            long now = System.currentTimeMillis();
            int processed = 0;

            for (Auction auction : activeAuctions) {
                if (auction.getStatus() == AuctionStatus.ACTIVE && auction.getEndTime() <= now) {
                    auctionManager.endAuction(auction);
                    processed++;
                }
            }

            if (processed > 0) {
                plugin.getLogger().info("만료된 경매 " + processed + "개 처리됨");
            }
        } catch (Exception e) {
            plugin. getLogger().severe("경매 확인 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void start() {
        int intervalSeconds = plugin.getConfig().getInt("auction.check-interval", 60);
        long intervalTicks = intervalSeconds * 20L;
        
        this.runTaskTimer(plugin, intervalTicks, intervalTicks);
        plugin.getLogger().info("경매 확인 태스크 시작 (간격: " + intervalSeconds + "초)");
    }
}