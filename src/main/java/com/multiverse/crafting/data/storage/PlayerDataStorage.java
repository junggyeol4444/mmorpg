package com.multiverse.crafting.data.storage;

import com.multiverse.crafting.CraftingCore;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles per-player YAML data (players/<UUID>.yml).
 */
public class PlayerDataStorage {

    private final CraftingCore plugin;
    private final File folder;
    private final Map<UUID, YamlConfiguration> cache = new HashMap<>();

    public PlayerDataStorage(CraftingCore plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "players");
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private File file(UUID uuid) {
        return new File(folder, uuid.toString() + ".yml");
    }

    public YamlConfiguration getCached(UUID uuid) {
        return cache.get(uuid);
    }

    public void load(UUID uuid) throws IOException, InvalidConfigurationException {
        File f = file(uuid);
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            f.createNewFile();
        }
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.load(f);
        cache.put(uuid, cfg);
    }

    public void save(UUID uuid) throws IOException {
        YamlConfiguration cfg = cache.get(uuid);
        if (cfg == null) return;
        cfg.save(file(uuid));
    }

    /**
     * Convenience setter for simple key/value pairs.
     */
    public void set(UUID uuid, String path, Object value) throws IOException {
        YamlConfiguration cfg = cache.computeIfAbsent(uuid, k -> new YamlConfiguration());
        cfg.set(path, value);
        save(uuid);
    }

    /**
     * Convenience getter; returns null if not present or not loaded.
     */
    public Object get(UUID uuid, String path) {
        YamlConfiguration cfg = cache.get(uuid);
        if (cfg == null) return null;
        return cfg.get(path);
    }
}