package com.multiverse.item. storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YAMLStorage {
    
    private File file;
    private FileConfiguration config;
    
    /**
     * 기본 생성자
     */
    public YAMLStorage(File file) {
        this. file = file;
        loadConfig();
    }
    
    /**
     * 설정 파일 로드
     */
    private void loadConfig() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }
    
    /**
     * 설정 저장
     */
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 데이터 저장 (경로, 값)
     */
    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }
    
    /**
     * 데이터 조회
     */
    public Object get(String path) {
        return config.get(path);
    }
    
    /**
     * 데이터 조회 (기본값)
     */
    public Object get(String path, Object defaultValue) {
        return config.get(path, defaultValue);
    }
    
    /**
     * 문자열 데이터 조회
     */
    public String getString(String path) {
        return config.getString(path);
    }
    
    /**
     * 정수 데이터 조회
     */
    public int getInt(String path) {
        return config.getInt(path);
    }
    
    /**
     * 더블 데이터 조회
     */
    public double getDouble(String path) {
        return config.getDouble(path);
    }
    
    /**
     * 불린 데이터 조회
     */
    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }
    
    /**
     * 리스트 데이터 조회
     */
    public List<? > getList(String path) {
        return config.getList(path);
    }
    
    /**
     * 섹션 데이터 조회
     */
    public Map<String, Object> getSection(String path) {
        Map<String, Object> result = new HashMap<>();
        if (config.contains(path)) {
            for (String key : config.getConfigurationSection(path).getKeys(false)) {
                result.put(key, config.get(path + "." + key));
            }
        }
        return result;
    }
    
    /**
     * 데이터 존재 여부 확인
     */
    public boolean contains(String path) {
        return config.contains(path);
    }
    
    /**
     * 데이터 삭제
     */
    public void remove(String path) {
        config.set(path, null);
        save();
    }
    
    /**
     * 모든 데이터 삭제
     */
    public void clear() {
        for (String key : config.getKeys(true)) {
            config.set(key, null);
        }
        save();
    }
    
    /**
     * 설정 다시 로드
     */
    public void reload() {
        loadConfig();
    }
    
    /**
     * 파일 경로 반환
     */
    public File getFile() {
        return file;
    }
    
    /**
     * FileConfiguration 반환
     */
    public FileConfiguration getConfig() {
        return config;
    }
}