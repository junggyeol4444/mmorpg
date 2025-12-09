package com.multiverse.crafting.tasks;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.data.DataManager;
import com.multiverse.crafting.managers.CraftingDataManager;
import com.multiverse.crafting.managers.CraftingStationManager;

/**
 * Periodic autosave task for player/station data.
 */
public class AutoSaveTask implements Runnable {

    private final DataManager dataManager;
    private final CraftingDataManager craftingDataManager;
    private final CraftingStationManager stationManager;
    private final CraftingCore plugin;

    public AutoSaveTask(CraftingCore plugin,
                        DataManager dataManager,
                        CraftingDataManager craftingDataManager,
                        CraftingStationManager stationManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.craftingDataManager = craftingDataManager;
        this.stationManager = stationManager;
    }

    @Override
    public void run() {
        try {
            craftingDataManager.saveOnlinePlayers();
            stationManager.savePlacedStations();
            dataManager.saveAll();
        } catch (Exception ex) {
            plugin.getLogger().warning("Autosave failed: " + ex.getMessage());
        }
    }
}