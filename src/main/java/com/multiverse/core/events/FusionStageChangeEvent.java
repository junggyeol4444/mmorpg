package com.multiverse.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FusionStageChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final int oldStage;
    private final int newStage;

    public FusionStageChangeEvent(int oldStage, int newStage) {
        this.oldStage = oldStage;
        this.newStage = newStage;
    }

    public int getOldStage() {
        return oldStage;
    }

    public int getNewStage() {
        return newStage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}