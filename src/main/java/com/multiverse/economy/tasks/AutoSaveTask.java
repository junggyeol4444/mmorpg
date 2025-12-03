package com.multiverse.economy.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import com.multiverse.economy.utils.FileUtil;

public class AutoSaveTask extends BukkitRunnable {

    @Override
    public void run() {
        FileUtil.saveAllData();
    }
}