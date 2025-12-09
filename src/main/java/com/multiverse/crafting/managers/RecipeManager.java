package com.multiverse.crafting.managers;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.data.DataManager;
import com.multiverse.crafting.data.storage.RecipeDataStorage;
import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.models.enums.LearnMethod;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages recipes: loading, lookup, and player knowledge.
 */
public class RecipeManager {

    private final CraftingCore plugin;
    private final RecipeDataStorage storage;
    private final CraftingDataManager dataManager;

    public RecipeManager(CraftingCore plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.storage = dataManager.getRecipeDataStorage();
        this.dataManager = new CraftingDataManager(plugin, dataManager);
    }

    public void loadBuiltinRecipes() {
        try {
            storage.loadAllRecipes();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load recipes: " + e.getMessage());
        }
    }

    public CraftingRecipe getRecipe(String recipeId) {
        if (recipeId == null) return null;
        return storage.getRecipeMap().get(recipeId);
    }

    public void registerRecipe(CraftingRecipe recipe) {
        if (recipe == null) return;
        storage.getRecipeMap().put(recipe.getRecipeId(), recipe);
    }

    public List<CraftingRecipe> getKnownRecipes(Player player) {
        List<String> ids = dataManager.getKnownRecipeIds(player.getUniqueId());
        List<CraftingRecipe> list = new ArrayList<>();
        for (String id : ids) {
            CraftingRecipe r = getRecipe(id);
            if (r != null) list.add(r);
        }
        return list;
    }

    public void learnRecipe(Player player, String recipeId, boolean silent) {
        dataManager.learnRecipe(player.getUniqueId(), recipeId, LearnMethod.ADMIN);
        if (!silent) {
            player.sendMessage("레시피를 습득했습니다: " + recipeId);
        }
    }

    public Map<String, CraftingRecipe> getAllRecipes() {
        return storage.getRecipeMap();
    }
}