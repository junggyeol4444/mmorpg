package com.multiverse.npcai.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 설정 파일 읽기 및 관련 유틸리티 기능 제공 클래스
 */
public class ConfigUtil {
    private Properties properties = new Properties();

    public void loadConfig(String path) throws IOException {
        try (FileInputStream input = new FileInputStream(path)) {
            properties.load(input);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }
}