package com.multiverse.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.multiverse.core.models.enums.BalanceChangeReason;

public class BalanceChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String playerName;
    private final double oldBalance;
    private final double newBalance;
    private final BalanceChangeReason reason;

    public BalanceChangeEvent(String playerName, double oldBalance, double newBalance, BalanceChangeReason reason) {
        this.playerName = playerName;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.reason = reason;
    }

    public String getPlayerName() {
        return playerName;
    }

    public double getOldBalance() {
        return oldBalance;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public BalanceChangeReason getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}