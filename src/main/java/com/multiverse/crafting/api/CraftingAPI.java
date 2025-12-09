package com.multiverse.crafting.api;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.managers.CraftingManager;
import com.multiverse.crafting.managers.CraftingSkillManager;
import com.multiverse.crafting.managers.RecipeManager;
import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.models.enums.CraftingType;
import org.bukkit.entity.Player;

/**
 * Public-facing static API for other plugins.
 */
public final class CraftingAPI {

    private static CraftingCore plugin;

    private CraftingAPI() {}

    public static void init(CraftingCore instance) {
        plugin = instance;
    }

    private static void requireInit() {
        if (plugin == null) {
            throw new IllegalStateException("CraftingAPI not initialized");
        }
    }

    // === Recipe related ===

    public static CraftingRecipe getRecipe(String recipeId) {
        requireInit();
        return plugin.getRecipeManager().getRecipe(recipeId);
    }

    public static void registerRecipe(CraftingRecipe recipe) {
        requireInit();
        plugin.getRecipeManager().registerRecipe(recipe);
    }

    // === Crafting related ===

    public static void startCrafting(Player player, String recipeId, int amount) {
        requireInit();
        RecipeManager rm = plugin.getRecipeManager();
        CraftingManager cm = plugin.getCraftingManager();
        CraftingRecipe recipe = rm.getRecipe(recipeId);
        if (recipe != null) {
            cm.startCrafting(player, recipe, amount);
        }
    }

    public static boolean canCraft(Player player, String recipeId) {
        requireInit();
        RecipeManager rm = plugin.getRecipeManager();
        CraftingManager cm = plugin.getCraftingManager();
        CraftingRecipe recipe = rm.getRecipe(recipeId);
        return recipe != null && cm.canCraft(player, recipe);
    }

    // === Skill related ===

    public static int getCraftingLevel(Player player, CraftingType type) {
        requireInit();
        CraftingSkillManager sm = plugin.getSkillManager();
        return sm.getLevel(player, type);
    }

    public static void addCraftingExp(Player player, CraftingType type, long amount) {
        requireInit();
        CraftingSkillManager sm = plugin.getSkillManager();
        sm.addExperience(player, type, amount);
    }
}