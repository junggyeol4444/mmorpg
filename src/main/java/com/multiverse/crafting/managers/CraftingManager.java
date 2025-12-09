package com.multiverse.crafting.managers;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.calculators.CraftingSuccessCalculator;
import com.multiverse.crafting.events.CraftingCompleteEvent;
import com.multiverse.crafting.events.CraftingStartEvent;
import com.multiverse.crafting.models.CraftingMaterial;
import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.models.enums.CraftingResult;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Handles crafting flow: validation, material consume, result generation.
 */
public class CraftingManager {

    private final CraftingCore plugin;
    private final RecipeManager recipeManager;
    private final CraftingStationManager stationManager;
    private final CraftingSkillManager skillManager;
    private final CraftingSuccessCalculator successCalculator;
    private final CraftingDataManager dataManager;

    private final Map<UUID, Integer> craftingTasks = new HashMap<>();

    public CraftingManager(CraftingCore plugin,
                           RecipeManager recipeManager,
                           CraftingStationManager stationManager,
                           CraftingSkillManager skillManager,
                           CraftingSuccessCalculator successCalculator,
                           CraftingDataManager dataManager) {
        this.plugin = plugin;
        this.recipeManager = recipeManager;
        this.stationManager = stationManager;
        this.skillManager = skillManager;
        this.successCalculator = successCalculator;
        this.dataManager = dataManager;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    /* ---------- Entry points ---------- */

    public boolean canCraft(Player player, CraftingRecipe recipe) {
        if (recipe == null) return false;
        // Level checks
        if (recipe.getRequiredLevel() > player.getLevel()) return false;
        if (recipe.getRequiredSkillLevel() > skillManager.getLevel(player, recipe.getType())) return false;
        // Known recipe check
        if (!dataManager.knowsRecipe(player.getUniqueId(), recipe.getRecipeId())) return false;
        // Station check
        if (recipe.getRequiredStation() != null &&
                stationManager.getNearbyOrOwnedStation(player, recipe.getRequiredStation().getType()) == null) {
            return false;
        }
        // Materials check
        return hasAllMaterials(player, recipe.getMaterials(), 1);
    }

    public void startCrafting(Player player, CraftingRecipe recipe, int amount) {
        if (amount <= 0) amount = 1;
        if (!canCraft(player, recipe)) {
            player.sendMessage("제작 조건을 충족하지 못했습니다.");
            return;
        }

        CraftingStartEvent startEvent = new CraftingStartEvent(player, recipe, amount);
        Bukkit.getPluginManager().callEvent(startEvent);
        if (startEvent.isCancelled()) return;

        double successRate = successCalculator.calculateSuccessRate(player, recipe);

        // Consume materials immediately
        if (!consumeMaterials(player, recipe.getMaterials(), amount)) {
            player.sendMessage("재료가 부족합니다.");
            return;
        }

        // Schedule crafting completion
        long delay = Math.max(0, recipe.getCraftingTime()) * 20L;
        int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            CraftingResult result = successCalculator.rollResult(successRate);
            ItemStack product = makeResult(recipe, result, amount);
            if (product != null && result != CraftingResult.FAIL && result != CraftingResult.CRITICAL_FAIL) {
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(product);
                if (!leftover.isEmpty()) {
                    leftover.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
                }
            }
            Bukkit.getPluginManager().callEvent(new CraftingCompleteEvent(player, recipe, result, product));
            craftingTasks.remove(player.getUniqueId());
        }, delay).getTaskId();

        craftingTasks.put(player.getUniqueId(), taskId);
    }

    public void cancelCrafting(Player player) {
        Integer id = craftingTasks.remove(player.getUniqueId());
        if (id != null) {
            Bukkit.getScheduler().cancelTask(id);
        }
    }

    public void cancelAllSessions() {
        for (Integer id : craftingTasks.values()) {
            Bukkit.getScheduler().cancelTask(id);
        }
        craftingTasks.clear();
    }

    /* ---------- Materials ---------- */

    private boolean hasAllMaterials(Player player, List<CraftingMaterial> materials, int amount) {
        for (CraftingMaterial mat : materials) {
            if (!hasMaterial(player, mat.getItem(), mat.getAmount() * amount)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasMaterial(Player player, ItemStack target, int required) {
        if (target == null || target.getType() == Material.AIR) return true;
        int count = 0;
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null && is.isSimilar(target)) {
                count += is.getAmount();
                if (count >= required) return true;
            }
        }
        return false;
    }

    private boolean consumeMaterials(Player player, List<CraftingMaterial> materials, int amount) {
        if (!hasAllMaterials(player, materials, amount)) return false;
        for (CraftingMaterial mat : materials) {
            int toRemove = mat.getAmount() * amount;
            if (mat.isConsumeOnFail()) {
                removeItems(player, mat.getItem(), toRemove);
            }
        }
        return true;
    }

    private void removeItems(Player player, ItemStack target, int amount) {
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack is = contents[i];
            if (is != null && is.isSimilar(target)) {
                int take = Math.min(is.getAmount(), remaining);
                is.setAmount(is.getAmount() - take);
                if (is.getAmount() <= 0) contents[i] = null;
                remaining -= take;
            }
        }
        player.getInventory().setContents(contents);
    }

    /* ---------- Result ---------- */

    private ItemStack makeResult(CraftingRecipe recipe, CraftingResult result, int amount) {
        if (result == CraftingResult.CRITICAL_FAIL) return null;
        if (result == CraftingResult.FAIL) return null;
        ItemStack base = recipe.getResult().clone();
        int min = recipe.getMinAmount();
        int max = recipe.getMaxAmount();
        int finalAmount = min;
        if (max > min) {
            finalAmount = min + new Random().nextInt(max - min + 1);
        }
        if (result == CraftingResult.GREAT_SUCCESS) {
            finalAmount = Math.min(max * 2, finalAmount + max); // bonus batch
        }
        base.setAmount(finalAmount * amount);
        return base;
    }

    /* ---------- GUI hooks ---------- */

    public void handleGuiClick(Player player, InventoryClickEvent e) {
        // Delegates to specific GUIs if needed; placeholder hook
    }

    /* ---------- Accessors ---------- */

    public CraftingDataManager getDataManager() {
        return dataManager;
    }

    public CraftingSuccessCalculator getSuccessCalculator() {
        return successCalculator;
    }
}