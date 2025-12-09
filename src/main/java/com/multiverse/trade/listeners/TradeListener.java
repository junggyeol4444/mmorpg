package com.multiverse.trade.listeners;

import com.multiverse.trade.TradeCore;
import com.multiverse.trade.events.TradeCancelEvent;
import com.multiverse. trade.events.TradeCompleteEvent;
import com.multiverse. trade.events.TradeStartEvent;
import com.multiverse.trade.managers.TradeManager;
import com.multiverse.trade.models.Trade;
import com.multiverse. trade.models.TradeStatus;
import com. multiverse.trade. utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit. event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org. bukkit.event. entity.PlayerDeathEvent;
import org. bukkit.event. player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class TradeListener implements Listener {

    private final TradeCore plugin;
    private final TradeManager tradeManager;

    public TradeListener(TradeCore plugin) {
        this. plugin = plugin;
        this.tradeManager = plugin.getTradeManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTradeStart(TradeStartEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Trade trade = event.getTrade();
        Player player1 = event.getPlayer1();
        Player player2 = event.getPlayer2();

        plugin.getTradeSecurityManager().logTrade(trade);

        plugin.getLogger().info("거래 시작:  " + player1.getName() + " <-> " + player2.getName());
    }

    @EventHandler(priority = EventPriority. MONITOR)
    public void onTradeComplete(TradeCompleteEvent event) {
        Trade trade = event.getTrade();
        Player player1 = event.getPlayer1();
        Player player2 = event.getPlayer2();

        plugin.getTradeSecurityManager().recordTradeTime(player1);
        plugin.getTradeSecurityManager().recordTradeTime(player2);

        plugin.getTradeSecurityManager().logTrade(trade);

        plugin.getLogger().info("거래 완료: " + player1.getName() + " <-> " + player2.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTradeCancel(TradeCancelEvent event) {
        Trade trade = event.getTrade();

        plugin.getTradeSecurityManager().logTrade(trade);

        plugin.getLogger().info("거래 취소: " + trade. getTradeId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Trade trade = tradeManager.getTrade(player);
        if (trade != null && trade.getStatus() == TradeStatus. ACTIVE) {
            tradeManager.cancelTrade(trade);

            Player other = trade.getOtherPlayer(player);
            if (other != null && other.isOnline()) {
                MessageUtil.send(other, "trade. cancelled");
                other.sendMessage(MessageUtil.color("&c상대방이 접속을 종료하여 거래가 취소되었습니다."));
            }
        }

        tradeManager.removePendingRequest(player. getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Trade trade = tradeManager.getTrade(player);
        if (trade != null && trade.getStatus() == TradeStatus.ACTIVE) {
            tradeManager.cancelTrade(trade);

            Player other = trade.getOtherPlayer(player);
            if (other != null && other.isOnline()) {
                MessageUtil.send(other, "trade.cancelled");
                other. sendMessage(MessageUtil.color("&c상대방이 사망하여 거래가 취소되었습니다."));
            }

            player.sendMessage(MessageUtil.color("&c사망하여 거래가 취소되었습니다."));
        }
    }

    @EventHandler(priority = EventPriority. HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        Trade trade = tradeManager.getTrade(player);
        if (trade != null && trade.getStatus() == TradeStatus.ACTIVE) {
            if (event.getFrom().getWorld() != event.getTo().getWorld() ||
                event.getFrom().distance(event.getTo()) > 10) {

                tradeManager. cancelTrade(trade);

                Player other = trade.getOtherPlayer(player);
                if (other != null && other.isOnline()) {
                    MessageUtil.send(other, "trade.cancelled");
                    other.sendMessage(MessageUtil.color("&c상대방이 텔레포트하여 거래가 취소되었습니다."));
                }

                player.sendMessage(MessageUtil.color("&c텔레포트하여 거래가 취소되었습니다."));
            }
        }
    }

    @EventHandler(priority = EventPriority. HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
            event.getFrom().getBlockY() == event.getTo().getBlockY() &&
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();

        Trade trade = tradeManager.getTrade(player);
        if (trade != null && trade.getStatus() == TradeStatus.ACTIVE) {
            Player other = trade.getOtherPlayer(player);
            if (other != null && other.isOnline()) {
                if (player.getWorld() != other.getWorld() ||
                    player.getLocation().distance(other.getLocation()) > 50) {

                    tradeManager.cancelTrade(trade);

                    MessageUtil.send(player, "trade.too-far");
                    MessageUtil.send(other, "trade.too-far");
                }
            }
        }
    }
}