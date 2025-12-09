package com.multiverse.crafting.events;

import com.multiverse.crafting.models.enums.CraftingType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player's crafting skill level increases.
 */
public class CraftingSkillLevelUpEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final CraftingType type;
    private final int oldLevel;
    private final int newLevel;

    public CraftingSkillLevelUpEvent(Player player, CraftingType type, int oldLevel, int newLevel) {
        super(true); // allow async
        this.player = player;
        this.type = type;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public Player getPlayer() {
        return player;
    }

    public CraftingType getType() {
        return type;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}