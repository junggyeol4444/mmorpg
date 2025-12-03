package com.multiverse.death.events;

import com.multiverse.death.models.SoulCoinTransaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 플레이어가 소울 코인 거래를 할 때 발생하는 이벤트
 */
public class SoulCoinTransactionEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final SoulCoinTransaction transaction;

    public SoulCoinTransactionEvent(Player player, SoulCoinTransaction transaction) {
        this.player = player;
        this.transaction = transaction;
    }

    public Player getPlayer() {
        return player;
    }

    public SoulCoinTransaction getTransaction() {
        return transaction;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}