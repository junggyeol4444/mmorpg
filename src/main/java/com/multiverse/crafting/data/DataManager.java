package com.multiverse.crafting.data;

import com.multiverse.crafting.data.storage.PlayerDataStorage;
import com.multiverse.crafting.data.storage.RecipeDataStorage;
import com.multiverse.crafting.data.storage.StationDataStorage;

import java.util.UUID;

/**
 * DataManager abstraction for loading/saving plugin data.
 */
public interface DataManager {

    void loadAll() throws Exception;

    void saveAll() throws Exception;

    void savePlayer(UUID uuid) throws Exception;

    void loadPlayer(UUID uuid) throws Exception;

    PlayerDataStorage getPlayerDataStorage();

    RecipeDataStorage getRecipeDataStorage();

    StationDataStorage getStationDataStorage();
}