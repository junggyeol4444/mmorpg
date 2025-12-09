package com.multiverse.crafting.data.storage;

import com.multiverse.crafting.CraftingCore;
import com.multiverse.crafting.models.CraftingStation;
import com.multiverse.crafting.models.enums.CraftingStationType;
import com.multiverse.crafting.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Stores placed stations in stations/placed_stations.yml
 */
public class StationDataStorage {

    private final CraftingCore plugin;
    private final File file;
    private final Map<Location, CraftingStation> stations = new HashMap<>();

    public StationDataStorage(CraftingCore plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "stations/placed_stations.yml");
        FileUtil.ensureFile(plugin, "stations/placed_stations.yml");
    }

    public Map<Location, CraftingStation> getStations() {
        return stations;
    }

    public void load() throws IOException, InvalidConfigurationException {
        stations.clear();
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.load(file);
        if (!cfg.isConfigurationSection("stations")) return;
        for (String key : cfg.getConfigurationSection("stations").getKeys(false)) {
            String path = "stations." + key;
            String worldName = cfg.getString(path + ".world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;
            double x = cfg.getDouble(path + ".x");
            double y = cfg.getDouble(path + ".y");
            double z = cfg.getDouble(path + ".z");
            float yaw = (float) cfg.getDouble(path + ".yaw", 0);
            float pitch = (float) cfg.getDouble(path + ".pitch", 0);
            Location loc = new Location(world, x, y, z, yaw, pitch);

            CraftingStationType type = CraftingStationType.valueOf(cfg.getString(path + ".type", "SMITHING_TABLE"));
            int tier = cfg.getInt(path + ".tier", 1);
            UUID owner = cfg.getString(path + ".owner") == null ? null : UUID.fromString(cfg.getString(path + ".owner"));
            double successBonus = cfg.getDouble(path + ".success-rate-bonus", 0.0);
            double speedBonus = cfg.getDouble(path + ".speed-bonus", 0.0);
            int maxMass = cfg.getInt(path + ".max-mass-craft", 0);
            int upgradeLevel = cfg.getInt(path + ".upgrade-level", 0);
            Map<String, Object> upgrades = new HashMap<>();
            if (cfg.isConfigurationSection(path + ".upgrades")) {
                upgrades.putAll(cfg.getConfigurationSection(path + ".upgrades").getValues(false));
            }

            CraftingStation station = new CraftingStation(loc, type, tier, owner, successBonus, speedBonus, maxMass, upgradeLevel, upgrades);
            stations.put(loc, station);
        }
    }

    public void save() throws IOException {
        YamlConfiguration cfg = new YamlConfiguration();
        int idx = 0;
        for (Map.Entry<Location, CraftingStation> e : stations.entrySet()) {
            Location loc = e.getKey();
            CraftingStation st = e.getValue();
            String path = "stations." + (idx++);
            cfg.set(path + ".world", loc.getWorld().getName());
            cfg.set(path + ".x", loc.getX());
            cfg.set(path + ".y", loc.getY());
            cfg.set(path + ".z", loc.getZ());
            cfg.set(path + ".yaw", loc.getYaw());
            cfg.set(path + ".pitch", loc.getPitch());
            cfg.set(path + ".type", st.getType().name());
            cfg.set(path + ".tier", st.getTier());
            cfg.set(path + ".owner", st.getOwner() == null ? null : st.getOwner().toString());
            cfg.set(path + ".success-rate-bonus", st.getSuccessRateBonus());
            cfg.set(path + ".speed-bonus", st.getSpeedBonus());
            cfg.set(path + ".max-mass-craft", st.getMaxMassCraft());
            cfg.set(path + ".upgrade-level", st.getUpgradeLevel());
            cfg.createSection(path + ".upgrades", st.getUpgrades());
        }
        cfg.save(file);
    }
}