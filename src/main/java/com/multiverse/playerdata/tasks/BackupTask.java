package com.multiverse.playerdata.tasks;

import com.multiverse.playerdata.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupTask extends BukkitRunnable {

    private final File dataFolder;
    private final String backupFolderName;
    private final long intervalTicks;

    public BackupTask(File dataFolder, String backupFolderName, long intervalTicks) {
        this.dataFolder = dataFolder;
        this.backupFolderName = backupFolderName;
        this.intervalTicks = intervalTicks;
    }

    @Override
    public void run() {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File backupDir = new File(dataFolder, backupFolderName + "/" + timestamp);
            FileUtil.copyDirectory(dataFolder, backupDir);
            Bukkit.getLogger().info("[PlayerData] 백업 완료: " + backupDir.getPath());
        } catch (Exception e) {
            Bukkit.getLogger().warning("[PlayerData] 백업 실패: " + e.getMessage());
        }
    }

    public void start() {
        this.runTaskTimerAsynchronously(Bukkit.getPluginManager().getPlugin("PlayerDataCore"), intervalTicks, intervalTicks);
    }

    public void stop() {
        this.cancel();
    }
}