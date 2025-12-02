package com.multiverse.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RegionDiscoverEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String playerName;
    private final String regionName;

    public RegionDiscoverEvent(String playerName, String regionName) {
        this.playerName = playerName;
        this.regionName = regionName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getRegionName() {
        return regionName;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}