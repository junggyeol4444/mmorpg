package com.multiverse. trade.data;

import com.multiverse.trade.TradeCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io. File;
import java. io.IOException;
import java. text.SimpleDateFormat;
import java.util.Date;
import java. util.logging.Level;

public class DataManager {

    private final TradeCore plugin;
    private final File dataFolder;
    private final File backupFolder;

    public DataManager(TradeCore plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        this.backupFolder = new File(dataFolder, "backups");
        
        if (!backupFolder. exists()) {
            backupFolder.mkdirs();
        }
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public File getSubFolder(String name) {
        File folder = new File(dataFolder, name);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public FileConfiguration loadYaml(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "파일 생성 실패:  " + file. getName(), e);
                return new YamlConfiguration();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveYaml(FileConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "파일 저장 실패: " + file.getName(), e);
        }
    }

    public void createBackup(String folderName) {
        File sourceFolder = new File(dataFolder, folderName);
        if (! sourceFolder.exists()) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = sdf. format(new Date());
        File backupDir = new File(backupFolder, folderName + "_" + timestamp);
        backupDir.mkdirs();

        copyFolder(sourceFolder, backupDir);
        
        plugin.getLogger().info(folderName + " 백업 완료:  " + backupDir.getName());
        
        cleanOldBackups(folderName, 5);
    }

    private void copyFolder(File source, File destination) {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination. mkdirs();
            }

            String[] files = source. list();
            if (files != null) {
                for (String file : files) {
                    copyFolder(new File(source, file), new File(destination, file));
                }
            }
        } else {
            try {
                java.nio.file. Files.copy(source.toPath(), destination.toPath(), 
                    java. nio.file.StandardCopyOption. REPLACE_EXISTING);
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "파일 복사 실패: " + source. getName(), e);
            }
        }
    }

    private void cleanOldBackups(String folderName, int keepCount) {
        File[] backups = backupFolder.listFiles((dir, name) -> name.startsWith(folderName + "_"));
        
        if (backups == null || backups.length <= keepCount) {
            return;
        }

        java.util.Arrays. sort(backups, (a, b) -> Long.compare(a.lastModified(), b.lastModified()));

        int toDelete = backups.length - keepCount;
        for (int i = 0; i < toDelete; i++) {
            deleteFolder(backups[i]);
        }
    }

    private void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFolder(file);
                }
            }
        }
        folder.delete();
    }

    public boolean fileExists(File file) {
        return file.exists() && file.isFile();
    }

    public boolean folderExists(File folder) {
        return folder.exists() && folder.isDirectory();
    }

    public void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
}