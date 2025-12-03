package com.multiverse.death.events;

import com.multiverse.death.models.Insurance;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 플레이어가 보험을 구매할 때 발생하는 이벤트
 */
public class InsurancePurchaseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Insurance insurance;

    public InsurancePurchaseEvent(Player player, Insurance insurance) {
        this.player = player;
        this.insurance = insurance;
    }

    public Player getPlayer() {
        return player;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}