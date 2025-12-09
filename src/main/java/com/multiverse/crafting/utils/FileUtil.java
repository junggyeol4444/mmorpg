package com.multiverse.crafting.utils;

import com.multiverse.crafting.CraftingCore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * File utility helpers for resource copying and YAML listing.
 */
public class FileUtil {

    private FileUtil() {}

    /** Ensure a resource folder exists in data folder; copy defaults if missing. */
    public static void ensureResourceFolder(CraftingCore plugin, String folderName) throws IOException {
        File targetDir = new File(plugin.getDataFolder(), folderName);
        if (!targetDir.exists()) {
            targetDir.mkdirs();
            // copy bundled resources recursively (only top-level files shipped in jar path)
            copyBundledFolder(plugin, folderName, targetDir);
        }
    }

    /** Ensure a single file exists; copy from jar if present. */
    public static void ensureFile(CraftingCore plugin, String path) {
        File target = new File(plugin.getDataFolder(), path);
        if (target.exists()) return;
        target.getParentFile().mkdirs();
        try (InputStream in = plugin.getResource(path)) {
            if (in != null) {
                Files.copy(in, target.toPath());
            } else {
                target.createNewFile();
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to create file " + path + ": " + e.getMessage());
        }
    }

    /** List all .yml files under a directory recursively. */
    public static List<File> listYamlFiles(File root) {
        List<File> list = new ArrayList<>();
        if (root == null || !root.exists()) return list;
        File[] files = root.listFiles();
        if (files == null) return list;
        for (File f : files) {
            if (f.isDirectory()) {
                list.addAll(listYamlFiles(f));
            } else if (f.getName().toLowerCase().endsWith(".yml")) {
                list.add(f);
            }
        }
        return list;
    }

    private static void copyBundledFolder(CraftingCore plugin, String jarFolder, File targetDir) throws IOException {
        // Bukkit doesn't provide folder list from jar; require explicit files or skip.
        // Here we copy known defaults if they exist.
        String[] defaults = new String[]{
                jarFolder + "/smithing/weapons.yml",
                jarFolder + "/smithing/armors.yml",
                jarFolder + "/smithing/tools.yml",
                jarFolder + "/alchemy/potions.yml",
                jarFolder + "/alchemy/elixirs.yml",
                jarFolder + "/cooking/foods.yml",
                jarFolder + "/cooking/drinks.yml",
                jarFolder + "/enchanting/enchants.yml",
                jarFolder + "/enchanting/scrolls.yml",
                jarFolder + "/jewelcrafting/jewels.yml",
                jarFolder + "/jewelcrafting/accessories.yml",
                jarFolder + "/tailoring/armors.yml",
                jarFolder + "/tailoring/bags.yml"
        };
        for (String path : defaults) {
            try (InputStream in = plugin.getResource(path)) {
                if (in == null) continue;
                File dest = new File(targetDir, path.substring(jarFolder.length() + 1));
                dest.getParentFile().mkdirs();
                Files.copy(in, dest.toPath());
            }
        }
    }
}