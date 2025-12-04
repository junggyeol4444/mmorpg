package com.multiverse.dungeon.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

/**
 * 설정 파일 유틸리티
 */
public class ConfigUtils {

    /**
     * 파일에서 설정 로드
     *
     * @param file 설정 파일
     * @return 로드된 FileConfiguration
     */
    public static FileConfiguration loadConfig(File file) {
        if (file == null || !  file.exists()) {
            return null;
        }

        try {
            return YamlConfiguration.loadConfiguration(file);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 설정을 파일에 저장
     *
     * @param config 저장할 설정
     * @param file 저장 대상 파일
     * @return 성공하면 true
     */
    public static boolean saveConfig(FileConfiguration config, File file) {
        if (config == null || file == null) {
            return false;
        }

        try {
            config.save(file);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 중첩된 경로에서 값 가져오기
     *
     * @param config 설정
     * @param path 경로 (예: "dungeon.level.max")
     * @param defaultValue 기본값
     * @return 값
     */
    public static Object get(FileConfiguration config, String path, Object defaultValue) {
        if (config == null || path == null) {
            return defaultValue;
        }

        Object value = config.get(path);
        return value != null ? value : defaultValue;
    }

    /**
     * 문자열 값 가져오기
     *
     * @param config 설정
     * @param path 경로
     * @param defaultValue 기본값
     * @return 문자열 값
     */
    public static String getString(FileConfiguration config, String path, String defaultValue) {
        if (config == null || path == null) {
            return defaultValue;
        }

        String value = config.getString(path);
        return value != null ? value : defaultValue;
    }

    /**
     * 정수 값 가져오기
     *
     * @param config 설정
     * @param path 경로
     * @param defaultValue 기본값
     * @return 정수 값
     */
    public static int getInt(FileConfiguration config, String path, int defaultValue) {
        if (config == null || path == null) {
            return defaultValue;
        }

        if (! config.contains(path)) {
            return defaultValue;
        }

        return config.getInt(path, defaultValue);
    }

    /**
     * 실수 값 가져오기
     *
     * @param config 설정
     * @param path 경로
     * @param defaultValue 기본값
     * @return 실수 값
     */
    public static double getDouble(FileConfiguration config, String path, double defaultValue) {
        if (config == null || path == null) {
            return defaultValue;
        }

        if (!config.contains(path)) {
            return defaultValue;
        }

        return config. getDouble(path, defaultValue);
    }

    /**
     * 불린 값 가져오기
     *
     * @param config 설정
     * @param path 경로
     * @param defaultValue 기본값
     * @return 불린 값
     */
    public static boolean getBoolean(FileConfiguration config, String path, boolean defaultValue) {
        if (config == null || path == null) {
            return defaultValue;
        }

        if (!config.contains(path)) {
            return defaultValue;
        }

        return config.getBoolean(path, defaultValue);
    }

    /**
     * 리스트 값 가져오기
     *
     * @param config 설정
     * @param path 경로
     * @return 리스트
     */
    public static java.util.List<? > getList(FileConfiguration config, String path) {
        if (config == null || path == null) {
            return new java.util.ArrayList<>();
        }

        java.util.List<?> value = config.getList(path);
        return value != null ? value : new java.util.ArrayList<>();
    }

    /**
     * 문자열 리스트 가져오기
     *
     * @param config 설정
     * @param path 경로
     * @return 문자열 리스트
     */
    public static java.util. List<String> getStringList(FileConfiguration config, String path) {
        if (config == null || path == null) {
            return new java.util.ArrayList<>();
        }

        java.util. List<String> value = config. getStringList(path);
        return value != null ? value : new java.util.ArrayList<>();
    }

    /**
     * 섹션 확인
     *
     * @param config 설정
     * @param path 경로
     * @return 섹션이 존재하면 true
     */
    public static boolean contains(FileConfiguration config, String path) {
        if (config == null || path == null) {
            return false;
        }

        return config.contains(path);
    }

    /**
     * 값 설정
     *
     * @param config 설정
     * @param path 경로
     * @param value 값
     */
    public static void set(FileConfiguration config, String path, Object value) {
        if (config == null || path == null) {
            return;
        }

        config.set(path, value);
    }

    /**
     * 경로의 모든 키 가져오기
     *
     * @param config 설정
     * @param path 경로
     * @return 키 세트
     */
    public static java.util.Set<String> getKeys(FileConfiguration config, String path) {
        if (config == null) {
            return new java.util.HashSet<>();
        }

        if (path == null || path.isEmpty()) {
            return config.getKeys(false);
        }

        var section = config.getConfigurationSection(path);
        if (section == null) {
            return new java.util.HashSet<>();
        }

        return section.getKeys(false);
    }
}