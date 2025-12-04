package com.multiverse.combat.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.multiverse.combat.CombatCore;
import java.io.File;
import java.io.IOException;

public class ConfigUtil {
    
    private final CombatCore plugin;
    private FileConfiguration config;
    private File configFile;
    
    public ConfigUtil(CombatCore plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        loadConfig();
    }
    
    private void loadConfig() {
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        this.config = YamlConfiguration. loadConfiguration(configFile);
    }
    
    public String getString(String path, String defaultValue) {
        if (config == null) loadConfig();
        return config.getString(path, defaultValue);
    }
    
    public int getInt(String path, int defaultValue) {
        if (config == null) loadConfig();
        return config.getInt(path, defaultValue);
    }
    
    public boolean getBoolean(String path, boolean defaultValue) {
        if (config == null) loadConfig();
        return config.getBoolean(path, defaultValue);
    }
    
    public double getDouble(String path, double defaultValue) {
        if (config == null) loadConfig();
        return config.getDouble(path, defaultValue);
    }
    
    public long getLong(String path, long defaultValue) {
        if (config == null) loadConfig();
        return config.getLong(path, defaultValue);
    }
    
    public void set(String path, Object value) {
        if (config == null) loadConfig();
        config.set(path, value);
        saveConfig();
    }
    
    public void saveConfig() {
        try {
            if (config != null) {
                config.save(configFile);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("설정 파일 저장 실패: " + e.getMessage());
        }
    }
    
    public void reloadConfig() {
        loadConfig();
    }
    
    public FileConfiguration getConfig() {
        if (config == null) loadConfig();
        return config;
    }
}