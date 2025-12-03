package com.multiverse.death.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 설정 파일 로드 및 저장 유틸리티
 */
public class ConfigUtil {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public void set(String path, Object value) {
        config.set(path, value);
        plugin.saveConfig();
    }
}