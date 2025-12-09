package com.multiverse.trade.events;

import com.multiverse.trade.models.MarketOrder;
import com.multiverse.trade.models.OrderType;
import org.bukkit.entity.Player;
import org.bukkit. event. Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit. inventory.ItemStack;

import java.util.UUID;

public class MarketOrderCreateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final MarketOrder order;
    private final Player player;
    private boolean cancelled;

    public MarketOrderCreateEvent(MarketOrder order, Player player) {
        this.order = order;
        this.player = player;
        this.cancelled = false;
    }

    public MarketOrder getOrder() {
        return order;
    }

    public UUID getOrderId() {
        return order.getOrderId();
    }

    public Player getPlayer() {
        return player;
    }

    public OrderType getOrderType() {
        return order.getType();
    }

    public boolean isSellOrder() {
        return order.getType() == OrderType.SELL;
    }

    public boolean isBuyOrder() {
        return order. getType() == OrderType.BUY;
    }

    public ItemStack getItem() {
        return order.getItem();
    }

    public int getAmount() {
        return order.getAmount();
    }

    public double getPricePerUnit() {
        return order. getPricePerUnit();
    }

    public double getTotalValue() {
        return order.getPricePerUnit() * order.getAmount();
    }

    public long getExpiryTime() {
        return order.getExpiryTime();
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