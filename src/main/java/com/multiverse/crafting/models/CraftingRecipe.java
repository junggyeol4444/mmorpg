package com.multiverse.crafting.models;

import com.multiverse.crafting.models.enums.CraftingType;
import com.multiverse.crafting.models.enums.RecipeCategory;

import java.util.List;

/**
 * Immutable crafting recipe definition.
 */
public class CraftingRecipe {

    private final String recipeId;
    private final String name;
    private final CraftingType type;
    private final RecipeCategory category;
    private final String description;
    private final List<String> lore;
    private final List<CraftingMaterial> materials;
    private final org.bukkit.inventory.ItemStack result;
    private final int minAmount;
    private final int maxAmount;
    private final int requiredLevel;
    private final int requiredSkillLevel;
    private final String requiredQuest;
    private final List<String> requiredRecipes;
    private final int craftingTime;      // seconds
    private final double successRate;    // %
    private final int experience;
    private final CraftingStation requiredStation; // may be null
    private final boolean massCraftable;
    private final int maxMassAmount;

    public CraftingRecipe(String recipeId,
                          String name,
                          CraftingType type,
                          RecipeCategory category,
                          String description,
                          List<String> lore,
                          List<CraftingMaterial> materials,
                          org.bukkit.inventory.ItemStack result,
                          int minAmount,
                          int maxAmount,
                          int requiredLevel,
                          int requiredSkillLevel,
                          String requiredQuest,
                          List<String> requiredRecipes,
                          int craftingTime,
                          double successRate,
                          int experience,
                          CraftingStation requiredStation,
                          boolean massCraftable,
                          int maxMassAmount) {
        this.recipeId = recipeId;
        this.name = name;
        this.type = type;
        this.category = category;
        this.description = description;
        this.lore = lore;
        this.materials = materials;
        this.result = result;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.requiredLevel = requiredLevel;
        this.requiredSkillLevel = requiredSkillLevel;
        this.requiredQuest = requiredQuest;
        this.requiredRecipes = requiredRecipes;
        this.craftingTime = craftingTime;
        this.successRate = successRate;
        this.experience = experience;
        this.requiredStation = requiredStation;
        this.massCraftable = massCraftable;
        this.maxMassAmount = maxMassAmount;
    }

    public String getRecipeId() { return recipeId; }
    public String getName() { return name; }
    public CraftingType getType() { return type; }
    public RecipeCategory getCategory() { return category; }
    public String getDescription() { return description; }
    public List<String> getLore() { return lore; }
    public List<CraftingMaterial> getMaterials() { return materials; }
    public org.bukkit.inventory.ItemStack getResult() { return result; }
    public int getMinAmount() { return minAmount; }
    public int getMaxAmount() { return maxAmount; }
    public int getRequiredLevel() { return requiredLevel; }
    public int getRequiredSkillLevel() { return requiredSkillLevel; }
    public String getRequiredQuest() { return requiredQuest; }
    public List<String> getRequiredRecipes() { return requiredRecipes; }
    public int getCraftingTime() { return craftingTime; }
    public double getSuccessRate() { return successRate; }
    public int getExperience() { return experience; }
    public CraftingStation getRequiredStation() { return requiredStation; }
    public boolean isMassCraftable() { return massCraftable; }
    public int getMaxMassAmount() { return maxMassAmount; }
}