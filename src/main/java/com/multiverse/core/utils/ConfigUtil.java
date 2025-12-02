package com.multiverse.core.utils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {
    private final Properties properties = new Properties();

    public boolean loadConfig(File file) {
        try (var reader = new java.io.FileReader(file)) {
            properties.load(reader);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        return Boolean.parseBoolean(value);
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }
}