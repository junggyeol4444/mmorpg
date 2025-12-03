package com.multiverse.economy.data;

import com.multiverse.economy.EconomyCore;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class YAMLDataManager implements DataManager {

    private final EconomyCore plugin;
    private boolean autoSaveRunning = false;
    private int autoSaveTaskId = -1;

    public YAMLDataManager(EconomyCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public Map<String, Object> loadYaml(String path) {
        File file = new File(plugin.getDataFolder(), path);
        if (!file.exists()) return null;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        return yaml.getValues(true);
    }

    @Override
    public void saveYaml(String path, Map<String, Object> data) {
        File file = new File(plugin.getDataFolder(), path);
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            yaml.set(entry.getKey(), entry.getValue());
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("YAML 저장 실패: " + path + " (" + e.getMessage() + ")");
        }
    }

    @Override
    public void backup(String folder) {
        // 백업 폴더에 전체 .yml 파일 복제
        File data = plugin.getDataFolder();
        File backup = new File(data, folder);
        if (!backup.exists()) backup.mkdirs();
        for (File f : data.listFiles()) {
            if (f.getName().endsWith(".yml")) {
                try {
                    java.nio.file.Files.copy(f.toPath(), new File(backup, f.getName()).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    plugin.getLogger().warning("[백업] 실패: " + f.getName());
                }
            }
        }
    }

    @Override
    public void autoSave() {
        // 여기에 전체 데이터 저장 루틴 수행 (실제로는 각 매니저 saveAll() 호출)
    }

    @Override
    public void startAutoSaveTask(int intervalSeconds) {
        if (autoSaveRunning) return;
        autoSaveRunning = true;
        autoSaveTaskId = plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                plugin, this::autoSave, intervalSeconds * 20L, intervalSeconds * 20L).getTaskId();
    }

    @Override
    public void stopAutoSaveTask() {
        if (!autoSaveRunning) return;
        plugin.getServer().getScheduler().cancelTask(autoSaveTaskId);
        autoSaveRunning = false;
        autoSaveTaskId = -1;
    }
}