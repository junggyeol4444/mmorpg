package com.multiverse.playerdata.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigUtil {

    /**
     * YAML 설정 파일 로드 또는 생성
     */
    public static YamlConfiguration loadConfig(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create config: " + file.getPath(), e);
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * YAML 설정 저장
     */
    public static void saveConfig(YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config: " + file.getPath(), e);
        }
    }

    /**
     * 기본값 적용 (이미 값이 없을 경우)
     */
    public static void setDefault(FileConfiguration config, String path, Object value) {
        if (!config.contains(path)) {
            config.set(path, value);
        }
    }
}