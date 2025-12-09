package com.multiverse.crafting.models;

import org.bukkit.inventory.ItemStack;

/**
 * Represents a required material for crafting.
 */
public class CraftingMaterial {

    private final ItemStack item;
    private final int amount;
    private final boolean consumeOnFail;

    public CraftingMaterial(ItemStack item, int amount, boolean consumeOnFail) {
        this.item = item;
        this.amount = amount;
        this.consumeOnFail = consumeOnFail;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isConsumeOnFail() {
        return consumeOnFail;
    }
}