package com.multiverse. pvp.storage;

import com.multiverse.pvp. PvPCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io. File;
import java.io.IOException;
import java. util.logging.Level;

public class DataManager {

    private final PvPCore plugin;
    private final File dataFolder;

    // 데이터 파일들
    private File playersFile;
    private File arenasFile;
    private File zonesFile;
    private File seasonFile;

    private FileConfiguration playersConfig;
    private FileConfiguration arenasConfig;
    private FileConfiguration zonesConfig;
    private FileConfiguration seasonConfig;

    public DataManager(PvPCore plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "data");

        initializeDataFolder();
        initializeFiles();
    }

    /**
     * 데이터 폴더 초기화
     */
    private void initializeDataFolder() {
        if (!dataFolder.exists()) {
            dataFolder. mkdirs();
        }
    }

    /**
     * 데이터 파일 초기화
     */
    private void initializeFiles() {
        playersFile = new File(dataFolder, "players.yml");
        arenasFile = new File(dataFolder, "arenas.yml");
        zonesFile = new File(dataFolder, "zones.yml");
        seasonFile = new File(dataFolder, "season.yml");

        createFileIfNotExists(playersFile);
        createFileIfNotExists(arenasFile);
        createFileIfNotExists(zonesFile);
        createFileIfNotExists(seasonFile);

        reloadAllConfigs();
    }

    /**
     * 파일 생성
     */
    private void createFileIfNotExists(File file) {
        if (! file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "파일 생성 실패: " + file.getName(), e);
            }
        }
    }

    /**
     * 모든 설정 리로드
     */
    public void reloadAllConfigs() {
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        zonesConfig = YamlConfiguration.loadConfiguration(zonesFile);
        seasonConfig = YamlConfiguration.loadConfiguration(seasonFile);
    }

    /**
     * 모든 설정 저장
     */
    public void saveAllConfigs() {
        saveConfig(playersConfig, playersFile);
        saveConfig(arenasConfig, arenasFile);
        saveConfig(zonesConfig, zonesFile);
        saveConfig(seasonConfig, seasonFile);
    }

    /**
     * 설정 저장
     */
    private void saveConfig(FileConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "파일 저장 실패: " + file.getName(), e);
        }
    }

    // ==================== Getters ====================

    public File getDataFolder() {
        return dataFolder;
    }

    public File getPlayersFile() {
        return playersFile;
    }

    public File getArenasFile() {
        return arenasFile;
    }

    public File getZonesFile() {
        return zonesFile;
    }

    public File getSeasonFile() {
        return seasonFile;
    }

    public FileConfiguration getPlayersConfig() {
        return playersConfig;
    }

    public FileConfiguration getArenasConfig() {
        return arenasConfig;
    }

    public FileConfiguration getZonesConfig() {
        return zonesConfig;
    }

    public FileConfiguration getSeasonConfig() {
        return seasonConfig;
    }

    // ==================== 개별 저장 메서드 ====================

    public void savePlayersConfig() {
        saveConfig(playersConfig, playersFile);
    }

    public void saveArenasConfig() {
        saveConfig(arenasConfig, arenasFile);
    }

    public void saveZonesConfig() {
        saveConfig(zonesConfig, zonesFile);
    }

    public void saveSeasonConfig() {
        saveConfig(seasonConfig, seasonFile);
    }

    // ==================== 개별 리로드 메서드 ====================

    public void reloadPlayersConfig() {
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    public void reloadArenasConfig() {
        arenasConfig = YamlConfiguration. loadConfiguration(arenasFile);
    }

    public void reloadZonesConfig() {
        zonesConfig = YamlConfiguration.loadConfiguration(zonesFile);
    }

    public void reloadSeasonConfig() {
        seasonConfig = YamlConfiguration. loadConfiguration(seasonFile);
    }

    /**
     * 개별 플레이어 데이터 파일 경로
     */
    public File getPlayerDataFile(String uuid) {
        File playerDataFolder = new File(dataFolder, "players");
        if (!playerDataFolder. exists()) {
            playerDataFolder.mkdirs();
        }
        return new File(playerDataFolder, uuid + ".yml");
    }

    /**
     * 개별 플레이어 데이터 로드
     */
    public FileConfiguration loadPlayerDataFile(String uuid) {
        File file = getPlayerDataFile(uuid);
        if (!file.exists()) {
            try {
                file. createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level. SEVERE, "플레이어 데이터 파일 생성 실패: " + uuid, e);
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * 개별 플레이어 데이터 저장
     */
    public void savePlayerDataFile(String uuid, FileConfiguration config) {
        File file = getPlayerDataFile(uuid);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "플레이어 데이터 저장 실패:  " + uuid, e);
        }
    }

    /**
     * 백업 생성
     */
    public void createBackup() {
        File backupFolder = new File(plugin.getDataFolder(), "backups");
        if (!backupFolder. exists()) {
            backupFolder.mkdirs();
        }

        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        File backupFile = new File(backupFolder, "backup_" + timestamp);
        backupFile.mkdirs();

        // 파일 복사
        copyFile(playersFile, new File(backupFile, "players.yml"));
        copyFile(arenasFile, new File(backupFile, "arenas. yml"));
        copyFile(zonesFile, new File(backupFile, "zones.yml"));
        copyFile(seasonFile, new File(backupFile, "season.yml"));

        plugin.getLogger().info("백업 생성됨: " + backupFile.getPath());
    }

    /**
     * 파일 복사
     */
    private void copyFile(File source, File dest) {
        try {
            java.nio.file. Files.copy(source.toPath(), dest.toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            plugin. getLogger().log(Level.WARNING, "파일 복사 실패: " + source. getName(), e);
        }
    }
}