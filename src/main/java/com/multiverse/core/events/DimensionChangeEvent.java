package com.multiverse.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DimensionChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String fromDimension;
    private final String toDimension;

    public DimensionChangeEvent(Player player, String fromDimension, String toDimension) {
        this.player = player;
        this.fromDimension = fromDimension;
        this.toDimension = toDimension;
    }

    public Player getPlayer() {
        return player;
    }

    public String getFromDimension() {
        return fromDimension;
    }

    public String getToDimension() {
        return toDimension;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}