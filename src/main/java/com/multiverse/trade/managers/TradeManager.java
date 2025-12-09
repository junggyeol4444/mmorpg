package com.multiverse.trade.managers;

import com.multiverse.trade.TradeCore;
import com. multiverse.trade. events.TradeCancelEvent;
import com.multiverse.trade.events.TradeCompleteEvent;
import com.multiverse. trade.events.TradeStartEvent;
import com.multiverse.trade.models.Trade;
import com.multiverse. trade.models.TradeStatus;
import com. multiverse.trade. utils.MessageUtil;
import org.bukkit. Bukkit;
import org.bukkit. entity.Player;
import org.bukkit. inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TradeManager {

    private final TradeCore plugin;
    private final Map<UUID, Trade> activeTrades = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> playerToTrade = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> pendingRequests = new ConcurrentHashMap<>();
    private final Map<UUID, Long> requestTimestamps = new ConcurrentHashMap<>();
    private final List<Trade> tradeHistory = Collections.synchronizedList(new ArrayList<>());

    private static final long REQUEST_TIMEOUT = 60000;

    public TradeManager(TradeCore plugin) {
        this.plugin = plugin;
        startRequestCleanupTask();
    }

    private void startRequestCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Iterator<Map.Entry<UUID, Long>> iterator = requestTimestamps.entrySet().iterator();
                while (iterator. hasNext()) {
                    Map.Entry<UUID, Long> entry = iterator.next();
                    if (now - entry. getValue() > REQUEST_TIMEOUT) {
                        UUID receiver = entry.getKey();
                        pendingRequests.remove(receiver);
                        iterator.remove();
                    }
                }
            }
        }.runTaskTimer(plugin, 200L, 200L);
    }

    public void sendTradeRequest(Player from, Player to) {
        UUID fromId = from.getUniqueId();
        UUID toId = to.getUniqueId();

        pendingRequests.put(toId, fromId);
        requestTimestamps.put(toId, System.currentTimeMillis());

        new BukkitRunnable() {
            @Override
            public void run() {
                UUID pending = pendingRequests.get(toId);
                if (pending != null && pending. equals(fromId)) {
                    pendingRequests.remove(toId);
                    requestTimestamps.remove(toId);
                    
                    Player fromPlayer = Bukkit.getPlayer(fromId);
                    if (fromPlayer != null && fromPlayer.isOnline()) {
                        fromPlayer.sendMessage(MessageUtil.color("&c거래 요청이 만료되었습니다."));
                    }
                }
            }
        }.runTaskLater(plugin, REQUEST_TIMEOUT / 50);
    }

    public UUID getPendingRequest(UUID playerId) {
        return pendingRequests.get(playerId);
    }

    public void removePendingRequest(UUID playerId) {
        pendingRequests.remove(playerId);
        requestTimestamps.remove(playerId);
    }

    public Trade acceptTradeRequest(Player player, UUID requesterId) {
        pendingRequests.remove(player.getUniqueId());
        requestTimestamps.remove(player.getUniqueId());

        Player requester = Bukkit.getPlayer(requesterId);
        if (requester == null || !requester.isOnline()) {
            return null;
        }

        Trade trade = new Trade(UUID.randomUUID(), requesterId, player.getUniqueId());
        trade.setStatus(TradeStatus.ACTIVE);
        trade.setStartTime(System.currentTimeMillis());

        activeTrades.put(trade.getTradeId(), trade);
        playerToTrade.put(requesterId, trade.getTradeId());
        playerToTrade.put(player.getUniqueId(), trade.getTradeId());

        TradeStartEvent event = new TradeStartEvent(trade, requester, player);
        Bukkit.getPluginManager().callEvent(event);

        if (event. isCancelled()) {
            activeTrades.remove(trade.getTradeId());
            playerToTrade.remove(requesterId);
            playerToTrade.remove(player. getUniqueId());
            return null;
        }

        return trade;
    }

    public void declineTradeRequest(Player player, UUID requesterId) {
        pendingRequests. remove(player.getUniqueId());
        requestTimestamps.remove(player.getUniqueId());
    }

    public Trade getTrade(Player player) {
        UUID tradeId = playerToTrade.get(player.getUniqueId());
        if (tradeId == null) {
            return null;
        }
        return activeTrades.get(tradeId);
    }

    public Trade getTrade(UUID tradeId) {
        return activeTrades.get(tradeId);
    }

    public boolean isTrading(Player player) {
        return playerToTrade.containsKey(player. getUniqueId());
    }

    public void addItem(Trade trade, Player player, ItemStack item) {
        if (trade == null || trade.getStatus() != TradeStatus. ACTIVE) {
            return;
        }

        List<ItemStack> items;
        if (trade.getPlayer1().equals(player. getUniqueId())) {
            items = trade.getPlayer1Items();
            trade.setPlayer1Ready(false);
        } else if (trade.getPlayer2().equals(player.getUniqueId())) {
            items = trade.getPlayer2Items();
            trade.setPlayer2Ready(false);
        } else {
            return;
        }

        if (items.size() < 24) {
            items.add(item. clone());
            player.getInventory().removeItem(item);
            MessageUtil.send(player, "trade.item-added");
            
            resetOtherPlayerReady(trade, player);
            updateTradeGUI(trade);
        }
    }

    public void removeItem(Trade trade, Player player, int slot) {
        if (trade == null || trade.getStatus() != TradeStatus. ACTIVE) {
            return;
        }

        List<ItemStack> items;
        if (trade.getPlayer1().equals(player.getUniqueId())) {
            items = trade.getPlayer1Items();
            trade.setPlayer1Ready(false);
        } else if (trade.getPlayer2().equals(player.getUniqueId())) {
            items = trade.getPlayer2Items();
            trade. setPlayer2Ready(false);
        } else {
            return;
        }

        if (slot >= 0 && slot < items.size()) {
            ItemStack removed = items.remove(slot);
            player.getInventory().addItem(removed);
            MessageUtil.send(player, "trade.item-removed");
            
            resetOtherPlayerReady(trade, player);
            updateTradeGUI(trade);
        }
    }

    public void setMoney(Trade trade, Player player, String currency, double amount) {
        if (trade == null || trade.getStatus() != TradeStatus.ACTIVE) {
            return;
        }

        if (amount < 0) {
            return;
        }

        if (! plugin.getEconomy().has(player, amount)) {
            MessageUtil.send(player, "shop.not-enough-money");
            return;
        }

        trade.setCurrency(currency);

        if (trade. getPlayer1().equals(player.getUniqueId())) {
            trade. setPlayer1Money(amount);
            trade. setPlayer1Ready(false);
        } else if (trade.getPlayer2().equals(player.getUniqueId())) {
            trade.setPlayer2Money(amount);
            trade. setPlayer2Ready(false);
        }

        MessageUtil. send(player, "trade.money-set", "amount", String.valueOf(amount));
        resetOtherPlayerReady(trade, player);
        updateTradeGUI(trade);
    }

    public void setReady(Trade trade, Player player, boolean ready) {
        if (trade == null || trade.getStatus() != TradeStatus. ACTIVE) {
            return;
        }

        if (trade.getPlayer1().equals(player.getUniqueId())) {
            trade.setPlayer1Ready(ready);
        } else if (trade.getPlayer2().equals(player.getUniqueId())) {
            trade.setPlayer2Ready(ready);
        }

        if (ready) {
            MessageUtil.send(player, "trade.ready");
        } else {
            MessageUtil.send(player, "trade.unready");
        }

        if (trade.isPlayer1Ready() && trade.isPlayer2Ready()) {
            startConfirmation(trade);
        }

        updateTradeGUI(trade);
    }

    private void startConfirmation(Trade trade) {
        trade.setStatus(TradeStatus. CONFIRMING);
        trade.setConfirmStartTime(System.currentTimeMillis());

        Player player1 = Bukkit.getPlayer(trade.getPlayer1());
        Player player2 = Bukkit.getPlayer(trade.getPlayer2());

        int confirmDelay = plugin.getConfig().getInt("direct-trade.confirmation.delay", 5);

        if (player1 != null) {
            MessageUtil.send(player1, "trade. confirming", "seconds", String.valueOf(confirmDelay));
        }
        if (player2 != null) {
            MessageUtil. send(player2, "trade.confirming", "seconds", String.valueOf(confirmDelay));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (trade.getStatus() == TradeStatus.CONFIRMING &&
                    trade.isPlayer1Ready() && trade.isPlayer2Ready()) {
                    completeTrade(trade);
                }
            }
        }.runTaskLater(plugin, confirmDelay * 20L);
    }

    private void resetOtherPlayerReady(Trade trade, Player player) {
        if (trade. getPlayer1().equals(player.getUniqueId())) {
            trade. setPlayer2Ready(false);
        } else {
            trade. setPlayer1Ready(false);
        }

        if (trade. getStatus() == TradeStatus.CONFIRMING) {
            trade. setStatus(TradeStatus. ACTIVE);
        }
    }

    public void confirmTrade(Trade trade) {
        if (trade. getStatus() == TradeStatus. CONFIRMING) {
            completeTrade(trade);
        }
    }

    public void cancelTrade(Trade trade) {
        if (trade == null) {
            return;
        }

        trade.setStatus(TradeStatus.CANCELLED);
        trade.setEndTime(System.currentTimeMillis());

        Player player1 = Bukkit.getPlayer(trade.getPlayer1());
        Player player2 = Bukkit.getPlayer(trade.getPlayer2());

        if (player1 != null) {
            for (ItemStack item : trade. getPlayer1Items()) {
                player1.getInventory().addItem(item);
            }
            player1.closeInventory();
        }

        if (player2 != null) {
            for (ItemStack item :  trade.getPlayer2Items()) {
                player2.getInventory().addItem(item);
            }
            player2.closeInventory();
        }

        TradeCancelEvent event = new TradeCancelEvent(trade, player1, player2);
        Bukkit.getPluginManager().callEvent(event);

        cleanupTrade(trade);
        tradeHistory.add(trade);
    }

    public void completeTrade(Trade trade) {
        if (trade == null) {
            return;
        }

        Player player1 = Bukkit.getPlayer(trade.getPlayer1());
        Player player2 = Bukkit.getPlayer(trade.getPlayer2());

        if (player1 == null || player2 == null || !player1.isOnline() || !player2.isOnline()) {
            cancelTrade(trade);
            return;
        }

        double money1 = trade. getPlayer1Money();
        double money2 = trade.getPlayer2Money();

        if (money1 > 0 && ! plugin.getEconomy().has(player1, money1)) {
            cancelTrade(trade);
            return;
        }
        if (money2 > 0 && !plugin.getEconomy().has(player2, money2)) {
            cancelTrade(trade);
            return;
        }

        double taxRate = plugin.getConfig().getDouble("direct-trade.tax. rate", 1.0) / 100.0;
        boolean taxEnabled = plugin.getConfig().getBoolean("direct-trade.tax.enabled", true);
        double totalTax = 0;

        if (money1 > 0) {
            plugin.getEconomy().withdrawPlayer(player1, money1);
            double tax = taxEnabled ? money1 * taxRate : 0;
            totalTax += tax;
            plugin.getEconomy().depositPlayer(player2, money1 - tax);
        }

        if (money2 > 0) {
            plugin. getEconomy().withdrawPlayer(player2, money2);
            double tax = taxEnabled ? money2 * taxRate : 0;
            totalTax += tax;
            plugin.getEconomy().depositPlayer(player1, money2 - tax);
        }

        for (ItemStack item :  trade.getPlayer1Items()) {
            player2.getInventory().addItem(item);
        }

        for (ItemStack item :  trade.getPlayer2Items()) {
            player1.getInventory().addItem(item);
        }

        trade.setStatus(TradeStatus. COMPLETED);
        trade.setEndTime(System.currentTimeMillis());
        trade.setTaxAmount(totalTax);

        if (totalTax > 0) {
            plugin.getTransactionFeeManager().distributeFee(totalTax);
            MessageUtil.send(player1, "trade.tax-paid", "amount", String.valueOf(totalTax / 2));
            MessageUtil.send(player2, "trade.tax-paid", "amount", String.valueOf(totalTax / 2));
        }

        MessageUtil. send(player1, "trade.completed");
        MessageUtil. send(player2, "trade.completed");

        player1.closeInventory();
        player2.closeInventory();

        for (ItemStack item : trade. getPlayer1Items()) {
            plugin.getPriceTracker().recordTransaction(item, 0, item.getAmount());
        }
        for (ItemStack item : trade.getPlayer2Items()) {
            plugin.getPriceTracker().recordTransaction(item, 0, item. getAmount());
        }

        TradeCompleteEvent event = new TradeCompleteEvent(trade, player1, player2);
        Bukkit.getPluginManager().callEvent(event);

        cleanupTrade(trade);
        tradeHistory.add(trade);

        plugin.getPlayerTradeDataManager().recordTrade(trade);
    }

    private void cleanupTrade(Trade trade) {
        activeTrades.remove(trade.getTradeId());
        playerToTrade.remove(trade.getPlayer1());
        playerToTrade.remove(trade.getPlayer2());
    }

    private void updateTradeGUI(Trade trade) {
        Player player1 = Bukkit.getPlayer(trade.getPlayer1());
        Player player2 = Bukkit.getPlayer(trade.getPlayer2());

        if (player1 != null) {
            plugin.getGuiManager().updateTradeGUI(player1, trade);
        }
        if (player2 != null) {
            plugin.getGuiManager().updateTradeGUI(player2, trade);
        }
    }

    public void cancelAllTrades() {
        List<Trade> tradesToCancel = new ArrayList<>(activeTrades.values());
        for (Trade trade : tradesToCancel) {
            cancelTrade(trade);
        }
    }

    public List<Trade> getTradeHistory() {
        return new ArrayList<>(tradeHistory);
    }

    public List<Trade> getPlayerTradeHistory(UUID playerId, int days) {
        long cutoff = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L);
        return tradeHistory.stream()
                .filter(t -> t.getPlayer1().equals(playerId) || t.getPlayer2().equals(playerId))
                .filter(t -> t. getStartTime() >= cutoff)
                .collect(java.util.stream. Collectors.toList());
    }
}