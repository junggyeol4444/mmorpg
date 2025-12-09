package com.multiverse.crafting;

import com.multiverse.crafting.api.CraftingAPI;
import com.multiverse.crafting.calculators.CraftingSuccessCalculator;
import com.multiverse.crafting.commands.CommandManager;
import com.multiverse.crafting.data.YAMLDataManager;
import com.multiverse.crafting.listeners.CraftingListener;
import com.multiverse.crafting.listeners.StationListener;
import com.multiverse.crafting.managers.*;
import com.multiverse.crafting.tasks.AutoSaveTask;
import com.multiverse.crafting.utils.ConfigUtil;
import com.multiverse.crafting.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * CraftingCore main plugin class.
 * - Loads config/resources
 * - Initializes managers
 * - Registers commands/listeners
 * - Schedules autosave
 */
public class CraftingCore extends JavaPlugin {

    private YAMLDataManager dataManager;
    private RecipeManager recipeManager;
    private CraftingStationManager stationManager;
    private CraftingSkillManager skillManager;
    private CraftingDataManager craftingDataManager;
    private CraftingSuccessCalculator successCalculator;
    private CraftingManager craftingManager;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Init utilities
        MessageUtil.init(this);
        ConfigUtil.init(this);

        // Init data + managers
        this.dataManager = new YAMLDataManager(this);
        this.recipeManager = new RecipeManager(this, dataManager);
        this.stationManager = new CraftingStationManager(this, dataManager);
        this.skillManager = new CraftingSkillManager(this, dataManager);
        this.craftingDataManager = new CraftingDataManager(this, dataManager, skillManager, recipeManager, stationManager);
        this.successCalculator = new CraftingSuccessCalculator(this, skillManager, stationManager);
        this.craftingManager = new CraftingManager(this, recipeManager, stationManager, skillManager, successCalculator, craftingDataManager);

        // API init
        CraftingAPI.init(this);

        try {
            // Load persistent data
            dataManager.loadAll();
            recipeManager.loadBuiltinRecipes();      // resources/recipes/**/*.yml
            stationManager.loadPlacedStations();     // resources/stations/placed_stations.yml
            craftingDataManager.loadOnlinePlayers(); // load player data if online
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Failed to load data", ex);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Commands
        this.commandManager = new CommandManager(this, craftingManager, recipeManager, stationManager, skillManager, craftingDataManager);
        if (getCommand("craft") != null) {
            getCommand("craft").setExecutor(commandManager);
            getCommand("craft").setTabCompleter(commandManager);
        } else {
            getLogger().severe("Command 'craft' not defined in plugin.yml");
        }

        // Listeners
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new CraftingListener(this, craftingManager, recipeManager, skillManager, craftingDataManager), this);
        pm.registerEvents(new StationListener(this, stationManager), this);

        // Auto-save task
        long intervalTicks = 20L * Math.max(60, getConfig().getLong("data.auto-save-interval", 300));
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AutoSaveTask(this, dataManager, craftingDataManager, stationManager), intervalTicks, intervalTicks);

        getLogger().info("CraftingCore enabled.");
    }

    @Override
    public void onDisable() {
        try {
            craftingManager.cancelAllSessions();
            dataManager.saveAll();
            stationManager.savePlacedStations();
            craftingDataManager.saveOnlinePlayers();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Error saving data on disable", ex);
        }
        getLogger().info("CraftingCore disabled.");
    }

    // Getters for managers (used by API and internal classes)
    public RecipeManager getRecipeManager() { return recipeManager; }
    public CraftingStationManager getStationManager() { return stationManager; }
    public CraftingSkillManager getSkillManager() { return skillManager; }
    public CraftingManager getCraftingManager() { return craftingManager; }
    public CraftingDataManager getCraftingDataManager() { return craftingDataManager; }
    public YAMLDataManager getDataManager() { return dataManager; }
    public CraftingSuccessCalculator getSuccessCalculator() { return successCalculator; }

    // Utility: reload config + dependent settings
    public void reloadAllConfigs() {
        reloadConfig();
        ConfigUtil.init(this);
        MessageUtil.init(this);
    }

    // Graceful player data save (used e.g., on quit listener)
    public void savePlayerData(Player player) {
        craftingDataManager.savePlayer(player.getUniqueId());
    }
}