package com.multiverse.economy.events;

import com.multiverse.economy.models.ExchangeRate;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CurrencyExchangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String fromCurrency;
    private final String toCurrency;
    private final double amount;
    private final double exchangedAmount;
    private final ExchangeRate exchangeRate;
    private boolean cancelled = false;

    public CurrencyExchangeEvent(Player player, String fromCurrency, String toCurrency, double amount, double exchangedAmount, ExchangeRate exchangeRate) {
        this.player = player;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.exchangedAmount = exchangedAmount;
        this.exchangeRate = exchangeRate;
    }

    public Player getPlayer() {
        return player;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public double getAmount() {
        return amount;
    }

    public double getExchangedAmount() {
        return exchangedAmount;
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
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