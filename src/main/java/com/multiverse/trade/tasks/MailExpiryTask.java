package com. multiverse.trade. tasks;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.managers.MailManager;
import org.bukkit.scheduler.BukkitRunnable;

public class MailExpiryTask extends BukkitRunnable {

    private final TradeCore plugin;
    private final MailManager mailManager;

    public MailExpiryTask(TradeCore plugin) {
        this.plugin = plugin;
        this. mailManager = plugin. getMailManager();
    }

    @Override
    public void run() {
        try {
            checkExpiredMails();
        } catch (Exception e) {
            plugin.getLogger().severe("우편 만료 확인 중 오류 발생:  " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkExpiredMails() {
        mailManager.checkExpiredMails();
    }

    public void start() {
        long intervalTicks = 20L * 60L * 60L;
        
        this.runTaskTimerAsynchronously(plugin, intervalTicks, intervalTicks);
        plugin.getLogger().info("우편 만료 확인 태스크 시작 (간격: 1시간)");
    }
}