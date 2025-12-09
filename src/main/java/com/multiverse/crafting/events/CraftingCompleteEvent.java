package com.multiverse.crafting.events;

import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.models.enums.CraftingResult;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CraftingCompleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final CraftingRecipe recipe;
    private final CraftingResult result;
    private final org.bukkit.inventory.ItemStack product;

    public CraftingCompleteEvent(Player player, CraftingRecipe recipe, CraftingResult result, org.bukkit.inventory.ItemStack product) {
        super(true); // async allowed if triggered asynchronously
        this.player = player;
        this.recipe = recipe;
        this.result = result;
        this.product = product;
    }

    public Player getPlayer() {
        return player;
    }

    public CraftingRecipe getRecipe() {
        return recipe;
    }

    public CraftingResult getResult() {
        return result;
    }

    public org.bukkit.inventory.ItemStack getProduct() {
        return product;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}