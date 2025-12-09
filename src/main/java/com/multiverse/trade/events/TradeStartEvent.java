package com.multiverse.trade.events;

import com.multiverse. trade.models.Trade;
import org.bukkit.entity.Player;
import org.bukkit. event. Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TradeStartEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Trade trade;
    private final Player player1;
    private final Player player2;
    private boolean cancelled;

    public TradeStartEvent(Trade trade, Player player1, Player player2) {
        this.trade = trade;
        this.player1 = player1;
        this. player2 = player2;
        this.cancelled = false;
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}