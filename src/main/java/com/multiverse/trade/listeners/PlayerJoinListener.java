package com.multiverse.trade.listeners;

import com. multiverse.trade. TradeCore;
import com.multiverse.trade.managers. MailManager;
import com.multiverse.trade.models.PlayerTradeData;
import com.multiverse. trade.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit. event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. player.PlayerJoinEvent;
import org.bukkit.event.player. PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoinListener implements Listener {

    private final TradeCore plugin;

    public PlayerJoinListener(TradeCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (! player.isOnline()) {
                    return;
                }

                loadPlayerData(player);
                checkUnreadMails(player);
            }
        }. runTaskLater(plugin, 20L);
    }

    private void loadPlayerData(Player player) {
        PlayerTradeData data = plugin.getPlayerTradeDataManager()
                .getOrCreatePlayerData(player.getUniqueId(), player.getName());

        if (data. getName() == null || ! data.getName().equals(player.getName())) {
            data.setName(player.getName());
            plugin.getPlayerTradeDataManager().savePlayerData(data);
        }

        plugin.getTradeSecurityManager().loadPlayerBlacklist(player. getUniqueId());

        plugin.getMailManager().loadPlayerMails(player.getUniqueId());

        int shopCount = plugin.getPlayerShopManager().getPlayerShops(player).size();
        if (data.getShopCount() != shopCount) {
            plugin.getPlayerTradeDataManager().updateShopCount(player.getUniqueId(), shopCount);
        }
    }

    private void checkUnreadMails(Player player) {
        MailManager mailManager = plugin.getMailManager();
        int unreadCount = mailManager.getUnreadCount(player);

        if (unreadCount > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (! player.isOnline()) {
                        return;
                    }

                    MessageUtil.send(player, "mail.unread-count", "count", String.valueOf(unreadCount));
                }
            }.runTaskLater(plugin, 60L);
        }
    }

    @EventHandler(priority = EventPriority. MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event. getPlayer();

        PlayerTradeData data = plugin.getPlayerTradeDataManager().getPlayerData(player.getUniqueId());
        if (data != null) {
            plugin. getPlayerTradeDataManager().savePlayerData(data);
        }
    }
}