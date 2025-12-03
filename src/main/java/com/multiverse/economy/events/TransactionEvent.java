package com.multiverse.economy.events;

import com.multiverse.economy.models.Transaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TransactionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player sender;
    private final Player receiver;
    private final Transaction transaction;
    private boolean cancelled = false;

    public TransactionEvent(Player sender, Player receiver, Transaction transaction) {
        this.sender = sender;
        this.receiver = receiver;
        this.transaction = transaction;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public Transaction getTransaction() {
        return transaction;
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