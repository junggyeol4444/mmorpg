package com.multiverse.death.events;

import com.multiverse.death.models.DeathRecord;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 플레이어 사망 데이터가 기록되고,
 * 사망 처리 로직을 커스텀으로 끼워넣고 싶을 때 활용하는 이벤트
 */
public class PlayerDeathProcessEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final DeathRecord record;
    private boolean cancelled = false;

    public PlayerDeathProcessEvent(Player player, DeathRecord record) {
        this.player = player;
        this.record = record;
    }

    public Player getPlayer() {
        return player;
    }

    public DeathRecord getRecord() {
        return record;
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