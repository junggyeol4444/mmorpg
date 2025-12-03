package com.multiverse.economy.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TaxCollectionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String currency;
    private final double amount;
    private final double taxAmount;
    private boolean cancelled = false;

    public TaxCollectionEvent(Player player, String currency, double amount, double taxAmount) {
        this.player = player;
        this.currency = currency;
        this.amount = amount;
        this.taxAmount = taxAmount;
    }

    public Player getPlayer() {
        return player;
    }

    public String getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}