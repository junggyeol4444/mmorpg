package com.multiverse.crafting.data;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.data.storage.PlayerDataStorage;
import com.multiverse.crafting.data.storage.RecipeDataStorage;
import com.multiverse.crafting.data.storage.StationDataStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * YAML-based DataManager implementation.
 */
public class YAMLDataManager implements DataManager {

    private final CraftingCore plugin;
    private final PlayerDataStorage playerDataStorage;
    private final RecipeDataStorage recipeDataStorage;
    private final StationDataStorage stationDataStorage;

    public YAMLDataManager(CraftingCore plugin) {
        this.plugin = plugin;
        this.playerDataStorage = new PlayerDataStorage(plugin);
        this.recipeDataStorage = new RecipeDataStorage(plugin);
        this.stationDataStorage = new StationDataStorage(plugin);
    }

    @Override
    public void loadAll() throws Exception {
        // Load static resources: recipes and stations
        recipeDataStorage.loadAllRecipes();
        stationDataStorage.load();
        // Load online players if any
        for (Player p : Bukkit.getOnlinePlayers()) {
            playerDataStorage.load(p.getUniqueId());
        }
    }

    @Override
    public void saveAll() throws Exception {
        // Save stations and online players
        stationDataStorage.save();
        for (Player p : Bukkit.getOnlinePlayers()) {
            playerDataStorage.save(p.getUniqueId());
        }
    }

    @Override
    public void savePlayer(UUID uuid) throws Exception {
        playerDataStorage.save(uuid);
    }

    @Override
    public void loadPlayer(UUID uuid) throws Exception {
        playerDataStorage.load(uuid);
    }

    @Override
    public PlayerDataStorage getPlayerDataStorage() {
        return playerDataStorage;
    }

    @Override
    public RecipeDataStorage getRecipeDataStorage() {
        return recipeDataStorage;
    }

    @Override
    public StationDataStorage getStationDataStorage() {
        return stationDataStorage;
    }
}