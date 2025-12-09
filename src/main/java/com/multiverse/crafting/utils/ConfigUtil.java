package com.multiverse.crafting.utils;

import com.multiverse.crafting.CraftingCore;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Simple config accessor wrapper.
 */
public class ConfigUtil {

    private static CraftingCore plugin;

    private ConfigUtil() {}

    public static void init(CraftingCore instance) {
        plugin = instance;
    }

    public static FileConfiguration cfg() {
        if (plugin == null) throw new IllegalStateException("ConfigUtil not initialized");
        return plugin.getConfig();
    }

    public static double getDouble(String path, double def) {
        return cfg().getDouble(path, def);
    }

    public static int getInt(String path, int def) {
        return cfg().getInt(path, def);
    }

    public static boolean getBool(String path, boolean def) {
        return cfg().getBoolean(path, def);
    }

    public static String getString(String path, String def) {
        return cfg().getString(path, def);
    }
}