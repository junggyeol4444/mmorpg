package com.multiverse.crafting.events;

import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.models.enums.LearnMethod;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when a player is about to learn a recipe; cancellable.
 */
public class RecipeLearnEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final CraftingRecipe recipe;
    private final LearnMethod method;
    private boolean cancelled;

    public RecipeLearnEvent(Player player, CraftingRecipe recipe, LearnMethod method) {
        super(true); // allow async
        this.player = player;
        this.recipe = recipe;
        this.method = method;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public CraftingRecipe getRecipe() {
        return recipe;
    }

    public LearnMethod getMethod() {
        return method;
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