package com.multiverse.economy.events;

import com.multiverse.economy.models.BankAccount;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BalanceChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final BankAccount bankAccount;
    private final double oldBalance;
    private final double newBalance;
    private boolean cancelled = false;

    public BalanceChangeEvent(Player player, BankAccount bankAccount, double oldBalance, double newBalance) {
        this.player = player;
        this.bankAccount = bankAccount;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }

    public Player getPlayer() {
        return player;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public double getOldBalance() {
        return oldBalance;
    }

    public double getNewBalance() {
        return newBalance;
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