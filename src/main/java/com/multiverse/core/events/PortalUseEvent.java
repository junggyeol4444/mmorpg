package com.multiverse.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.multiverse.core.models.Portal;

public class PortalUseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String playerName;
    private final Portal portal;

    public PortalUseEvent(String playerName, Portal portal) {
        this.playerName = playerName;
        this.portal = portal;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Portal getPortal() {
        return portal;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}