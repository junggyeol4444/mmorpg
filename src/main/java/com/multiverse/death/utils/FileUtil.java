package com.multiverse.death.utils;

import java.io.File;
import java.io.IOException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 파일 저장/로딩 관련 유틸리티
 */
public class FileUtil {

    public static File getResourceFile(JavaPlugin plugin, String filename) {
        return new File(plugin.getDataFolder(), filename);
    }

    public static boolean createFileIfNotExists(File file) {
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static boolean deleteFile(File file) {
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}