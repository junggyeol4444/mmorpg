package com.multiverse. trade.events;

import com.multiverse.trade.models. Trade;
import com.multiverse.trade.models.TradeStatus;
import org. bukkit.entity. Player;
import org.bukkit.event.Event;
import org.bukkit. event.HandlerList;
import org. bukkit.inventory. ItemStack;

import java.util.List;

public class TradeCancelEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Trade trade;
    private final Player player1;
    private final Player player2;
    private final CancelReason reason;

    public TradeCancelEvent(Trade trade, Player player1, Player player2) {
        this(trade, player1, player2, CancelReason.MANUAL);
    }

    public TradeCancelEvent(Trade trade, Player player1, Player player2, CancelReason reason) {
        this. trade = trade;
        this.player1 = player1;
        this.player2 = player2;
        this. reason = reason;
    }

    public Trade getTrade() {
        return trade;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public CancelReason getReason() {
        return reason;
    }

    public List<ItemStack> getPlayer1Items() {
        return trade. getPlayer1Items();
    }

    public List<ItemStack> getPlayer2Items() {
        return trade.getPlayer2Items();
    }

    public double getPlayer1Money() {
        return trade. getPlayer1Money();
    }

    public double getPlayer2Money() {
        return trade.getPlayer2Money();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public enum CancelReason {
        MANUAL,
        PLAYER_QUIT,
        PLAYER_DEATH,
        PLAYER_TELEPORT,
        TOO_FAR,
        TIMEOUT,
        SERVER_SHUTDOWN,
        ADMIN
    }
}