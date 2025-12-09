package com.multiverse.crafting.listeners;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.events.CraftingCompleteEvent;
import com.multiverse.crafting.events.CraftingStartEvent;
import com.multiverse.crafting.events.RecipeLearnEvent;
import com.multiverse.crafting.managers.CraftingDataManager;
import com.multiverse.crafting.managers.CraftingManager;
import com.multiverse.crafting.managers.CraftingSkillManager;
import com.multiverse.crafting.managers.RecipeManager;
import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.models.enums.CraftingResult;
import com.multiverse.crafting.models.enums.LearnMethod;
import com.multiverse.crafting.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CraftingListener implements Listener {

    private final CraftingCore plugin;
    private final CraftingManager craftingManager;
    private final RecipeManager recipeManager;
    private final CraftingSkillManager skillManager;
    private final CraftingDataManager dataManager;

    public CraftingListener(CraftingCore plugin,
                            CraftingManager craftingManager,
                            RecipeManager recipeManager,
                            CraftingSkillManager skillManager,
                            CraftingDataManager dataManager) {
        this.plugin = plugin;
        this.craftingManager = craftingManager;
        this.recipeManager = recipeManager;
        this.skillManager = skillManager;
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        // Pre-load player data
        try {
            dataManager.loadPlayer(e.getUniqueId());
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to load player data for " + e.getName() + ": " + ex.getMessage());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        dataManager.ensurePlayerDefaults(p.getUniqueId(), p.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        craftingManager.cancelCrafting(p);
        plugin.savePlayerData(p);
    }

    // Example hook: announce crafting start
    @EventHandler
    public void onCraftingStart(CraftingStartEvent e) {
        e.getPlayer().sendMessage(MessageUtil.color("&a제작을 시작합니다: " + e.getRecipe().getName() + " x" + e.getAmount()));
    }

    // Example hook: on learn recipe
    @EventHandler
    public void onRecipeLearn(RecipeLearnEvent e) {
        e.getPlayer().sendMessage(MessageUtil.color("&a레시피 습득: " + e.getRecipe().getName() + " (" + e.getMethod() + ")"));
    }

    // Example hook: complete crafting -> reward exp
    @EventHandler
    public void onCraftingComplete(CraftingCompleteEvent e) {
        CraftingRecipe recipe = e.getRecipe();
        Player player = e.getPlayer();
        if (e.getResult() == CraftingResult.SUCCESS || e.getResult() == CraftingResult.GREAT_SUCCESS) {
            skillManager.addExperience(player, recipe.getType(), recipe.getExperience());
        } else {
            skillManager.addExperience(player, recipe.getType(), Math.round(recipe.getExperience() * 0.5));
        }
    }
}