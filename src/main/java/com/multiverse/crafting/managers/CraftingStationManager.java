package com.multiverse.crafting.managers;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.data.DataManager;
import com.multiverse.crafting.data.storage.StationDataStorage;
import com.multiverse.crafting.models.CraftingStation;
import com.multiverse.crafting.models.enums.CraftingStationType;
import com.multiverse.crafting.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Manages placed crafting stations and lookup utilities.
 */
public class CraftingStationManager {

    private final CraftingCore plugin;
    private final StationDataStorage storage;

    public CraftingStationManager(CraftingCore plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.storage = dataManager.getStationDataStorage();
    }

    public void loadPlacedStations() throws Exception {
        storage.load();
    }

    public void savePlacedStations() throws Exception {
        storage.save();
    }

    public boolean placeStation(Player player, Location loc, String typeName) {
        CraftingStationType type;
        try {
            type = CraftingStationType.valueOf(typeName.toUpperCase());
        } catch (IllegalArgumentException ex) {
            player.sendMessage(MessageUtil.color("&c알 수 없는 스테이션 타입입니다."));
            return false;
        }
        CraftingStation station = new CraftingStation(
                loc,
                type,
                1,
                player.getUniqueId(),
                plugin.getConfig().getDouble("stations.default.success-rate-bonus", 0.0),
                plugin.getConfig().getDouble("stations.default.speed-bonus", 0.0),
                plugin.getConfig().getInt("stations.default.max-mass-craft", 0),
                0,
                new java.util.HashMap<>()
        );
        storage.getStations().put(loc, station);
        try {
            storage.save();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save placed station: " + e.getMessage());
        }
        return true;
    }

    /**
     * Get a nearby station of given type owned by player, or any nearby if ownership not required.
     */
    public CraftingStation getNearbyOrOwnedStation(Player player, CraftingStationType type) {
        double maxDistance = plugin.getConfig().getDouble("stations.use-radius", 5.0);
        UUID uuid = player.getUniqueId();
        CraftingStation best = null;
        double bestDist = Double.MAX_VALUE;
        for (Map.Entry<Location, CraftingStation> e : storage.getStations().entrySet()) {
            CraftingStation st = e.getValue();
            if (st.getType() != type) continue;
            if (st.getOwner() != null && !st.getOwner().equals(uuid)) continue;
            double dist = e.getKey().getWorld().equals(player.getWorld()) ? e.getKey().distance(player.getLocation()) : Double.MAX_VALUE;
            if (dist <= maxDistance && dist < bestDist) {
                best = st;
                bestDist = dist;
            }
        }
        return best;
    }
}