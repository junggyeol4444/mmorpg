package com.multiverse.crafting.events;

import com.multiverse.crafting.models.CraftingRecipe;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired before crafting begins; cancellable.
 */
public class CraftingStartEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final CraftingRecipe recipe;
    private final int amount;
    private boolean cancelled;

    public CraftingStartEvent(Player player, CraftingRecipe recipe, int amount) {
        super(true); // allow async
        this.player = player;
        this.recipe = recipe;
        this.amount = amount;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public CraftingRecipe getRecipe() {
        return recipe;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}