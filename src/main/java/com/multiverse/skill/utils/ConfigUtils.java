package com.multiverse.skill.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit. configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

/**
 * 설정 유틸리티
 */
public class ConfigUtils {

    /**
     * YAML 파일 로드
     */
    public static FileConfiguration loadYamlFile(File file) {
        if (file == null || !file.exists()) {
            return new YamlConfiguration();
        }

        try {
            return YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            e.printStackTrace();
            return new YamlConfiguration();
        }
    }

    /**
     * YAML 파일 저장
     */
    public static void saveYamlFile(FileConfiguration config, File file) {
        if (config == null || file == null) {
            return;
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 설정 값 조회 (정수)
     */
    public static int getInt(FileConfiguration config, String path, int defaultValue) {
        if (config == null || path == null) {
            return defaultValue;
        }

        if (config.contains(path)) {
            return config.getInt(path);
        }

        return defaultValue;
    }

    /**
     * 설정 값 조회 (실수)
     */
    public static double getDouble(FileConfiguration config, String path, double defaultValue) {
        if (config == null || path == null) {
            return defaultValue;
        }

        if (config.contains(path)) {
            return config.getDouble(path);
        }

        return defaultValue;
    }

    /**
     * 설정 값 조회 (문자열)
     */
    public static String getString(FileConfiguration config, String path, String defaultValue) {
        if (config == null || path == null) {
            return defaultValue;
        }

        if (config.contains(path)) {
            return config. getString(path);
        }

        return defaultValue;
    }

    /**
     * 설정 값 조회 (불린)
     */
    public static boolean getBoolean(FileConfiguration config, String path, boolean defaultValue) {
        if (config == null || path == null) {
            return defaultValue;
        }

        if (config.contains(path)) {
            return config. getBoolean(path);
        }

        return defaultValue;
    }

    /**
     * 설정 값 설정 (정수)
     */
    public static void setInt(FileConfiguration config, String path, int value) {
        if (config == null || path == null) {
            return;
        }

        config.set(path, value);
    }

    /**
     * 설정 값 설정 (실수)
     */
    public static void setDouble(FileConfiguration config, String path, double value) {
        if (config == null || path == null) {
            return;
        }

        config.set(path, value);
    }

    /**
     * 설정 값 설정 (문자열)
     */
    public static void setString(FileConfiguration config, String path, String value) {
        if (config == null || path == null) {
            return;
        }

        config.set(path, value);
    }

    /**
     * 설정 값 설정 (불린)
     */
    public static void setBoolean(FileConfiguration config, String path, boolean value) {
        if (config == null || path == null) {
            return;
        }

        config.set(path, value);
    }
}