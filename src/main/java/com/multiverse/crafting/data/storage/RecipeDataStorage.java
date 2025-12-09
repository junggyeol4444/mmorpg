package com.multiverse.crafting.data.storage;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.models.CraftingMaterial;
import com.multiverse.crafting.models.CraftingRecipe;
import com.multiverse.crafting.models.CraftingStation;
import com.multiverse.crafting.models.enums.CraftingType;
import com.multiverse.crafting.models.enums.RecipeCategory;
import com.multiverse.crafting.models.enums.CraftingStationType;
import com.multiverse.crafting.utils.FileUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Loads recipe definitions from resources/recipes/**/*.yml into memory.
 * Provides in-memory lookup map keyed by recipeId.
 */
public class RecipeDataStorage {

    private final CraftingCore plugin;
    private final Map<String, CraftingRecipe> recipeMap = new HashMap<>();

    public RecipeDataStorage(CraftingCore plugin) {
        this.plugin = plugin;
    }

    public Map<String, CraftingRecipe> getRecipeMap() {
        return recipeMap;
    }

    /**
     * Loads all recipe YAMLs under resources/recipes/*/*.yml into recipeMap.
     */
    public void loadAllRecipes() throws IOException, InvalidConfigurationException {
        recipeMap.clear();
        File recipesRoot = new File(plugin.getDataFolder(), "recipes");
        FileUtil.ensureResourceFolder(plugin, "recipes"); // copy defaults if not exist
        List<File> files = FileUtil.listYamlFiles(recipesRoot);
        for (File file : files) {
            YamlConfiguration cfg = new YamlConfiguration();
            cfg.load(file);
            ConfigurationSection recipesSec = cfg.getConfigurationSection("recipes");
            if (recipesSec == null) continue;
            for (String id : recipesSec.getKeys(false)) {
                ConfigurationSection sec = recipesSec.getConfigurationSection(id);
                if (sec == null) continue;
                CraftingRecipe recipe = parseRecipe(id, sec);
                if (recipe != null) {
                    recipeMap.put(recipe.getRecipeId(), recipe);
                }
            }
        }
    }

    private CraftingRecipe parseRecipe(String id, ConfigurationSection sec) {
        try {
            String name = sec.getString("name", id);
            CraftingType type = CraftingType.valueOf(sec.getString("type", "GENERAL"));
            RecipeCategory category = RecipeCategory.valueOf(sec.getString("category", "MISC"));
            String description = sec.getString("description", "");
            List<String> lore = sec.getStringList("lore");

            List<CraftingMaterial> materials = new ArrayList<>();
            if (sec.isConfigurationSection("materials")) {
                for (String key : sec.getConfigurationSection("materials").getKeys(false)) {
                    ConfigurationSection mSec = sec.getConfigurationSection("materials." + key);
                    if (mSec == null) continue;
                    ItemStack item = parseItem(mSec.getConfigurationSection("item"));
                    int amount = mSec.getInt("amount", 1);
                    boolean consumeOnFail = mSec.getBoolean("consume-on-fail", true);
                    materials.add(new CraftingMaterial(item, amount, consumeOnFail));
                }
            }

            ItemStack result = parseItem(sec.getConfigurationSection("result"));
            int minAmount = sec.getInt("min-amount", 1);
            int maxAmount = sec.getInt("max-amount", 1);

            int requiredLevel = sec.getInt("requirements.level", 0);
            int requiredSkillLevel = sec.getInt("requirements.skill-level", 0);
            String requiredQuest = sec.getString("requirements.required-quest", null);
            List<String> requiredRecipes = sec.getStringList("requirements.required-recipes");

            int craftingTime = sec.getInt("crafting-time", 0);
            double successRate = sec.getDouble("success-rate", 90.0);
            int experience = sec.getInt("experience", 0);

            CraftingStation requiredStation = null;
            if (sec.isConfigurationSection("required-station")) {
                ConfigurationSection st = sec.getConfigurationSection("required-station");
                CraftingStationType stType = CraftingStationType.valueOf(st.getString("type", "SMITHING_TABLE"));
                int tier = st.getInt("tier", 1);
                requiredStation = new CraftingStation(null, stType, tier, null, 0.0, 0.0, 0, 0, new HashMap<>());
            }

            boolean massCraftable = sec.getBoolean("mass-craftable", false);
            int maxMassAmount = sec.getInt("max-mass-amount", 1);

            return new CraftingRecipe(
                    id, name, type, category, description, lore,
                    materials, result, minAmount, maxAmount,
                    requiredLevel, requiredSkillLevel, requiredQuest, requiredRecipes,
                    craftingTime, successRate, experience,
                    requiredStation,
                    massCraftable, maxMassAmount
            );
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to parse recipe " + id + ": " + ex.getMessage());
            return null;
        }
    }

    private ItemStack parseItem(ConfigurationSection sec) {
        if (sec == null) return new ItemStack(Material.AIR);
        Material mat = Material.matchMaterial(sec.getString("material", "AIR"));
        if (mat == null) mat = Material.AIR;
        int amount = sec.getInt("amount", 1);
        ItemStack item = new ItemStack(mat, amount);
        // Display/lore customization could be applied via ItemMeta if desired
        return item;
    }
}