package com.multiverse.item. config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ItemConfig {
    
    private FileConfiguration config;
    private File configFile;
    
    /**
     * 기본 생성자
     */
    public ItemConfig(File file) {
        this.configFile = file;
        loadConfig();
    }
    
    /**
     * 설정 파일 로드
     */
    private void loadConfig() {
        if (!configFile.exists()) {
            setDefaults();
            saveConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    
    /**
     * 기본 설정 설정
     */
    private void setDefaults() {
        config = new YamlConfiguration();
        
        // 아이템 시스템 기본 설정
        config.set("item. max-enhance-level", 15);
        config.set("item.default-rarity", "common");
        config.set("item.max-sockets", 3);
        config.set("item.default-durability", 100);
        
        // 아이템 생성 설정
        config.set("generation.enable-random-options", true);
        config.set("generation.min-options", 1);
        config. set("generation.max-options", 5);
        config.set("generation.rare-item-rate", 0.15);
        config.set("generation.epic-item-rate", 0. 05);
        
        // 희귀도 별 스탯 배수
        config.set("rarity.common. multiplier", 1.0);
        config.set("rarity. uncommon.multiplier", 1.2);
        config.set("rarity.rare.multiplier", 1.5);
        config. set("rarity.epic.multiplier", 1.8);
        config.set("rarity.legendary. multiplier", 2.2);
    }
    
    /**
     * 설정 저장
     */
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 최대 강화 레벨
     */
    public int getMaxEnhanceLevel() {
        return config.getInt("item. max-enhance-level", 15);
    }
    
    /**
     * 기본 희귀도
     */
    public String getDefaultRarity() {
        return config.getString("item.default-rarity", "common");
    }
    
    /**
     * 최대 소켓 개수
     */
    public int getMaxSockets() {
        return config.getInt("item.max-sockets", 3);
    }
    
    /**
     * 기본 내구도
     */
    public int getDefaultDurability() {
        return config.getInt("item. default-durability", 100);
    }
    
    /**
     * 랜덤 옵션 생성 활성화
     */
    public boolean isRandomOptionEnabled() {
        return config. getBoolean("generation.enable-random-options", true);
    }
    
    /**
     * 최소 옵션 개수
     */
    public int getMinOptions() {
        return config.getInt("generation.min-options", 1);
    }
    
    /**
     * 최대 옵션 개수
     */
    public int getMaxOptions() {
        return config.getInt("generation. max-options", 5);
    }
    
    /**
     * 레어 아이템 생성 확률
     */
    public double getRareItemRate() {
        return config.getDouble("generation.rare-item-rate", 0.15);
    }
    
    /**
     * 에픽 아이템 생성 확률
     */
    public double getEpicItemRate() {
        return config.getDouble("generation.epic-item-rate", 0.05);
    }
    
    /**
     * 희귀도별 스탯 배수 가져오기
     */
    public double getRarityMultiplier(String rarity) {
        return config.getDouble("rarity." + rarity.  toLowerCase() + ".multiplier", 1. 0);
    }
    
    /**
     * 희귀도별 모든 배수 가져오기
     */
    public Map<String, Double> getAllRarityMultipliers() {
        Map<String, Double> multipliers = new HashMap<>();
        for (String key : config.getConfigurationSection("rarity").getKeys(false)) {
            multipliers. put(key, config.getDouble("rarity." + key + ".multiplier"));
        }
        return multipliers;
    }
    
    /**
     * 설정 다시 로드
     */
    public void reload() {
        loadConfig();
    }
    
    /**
     * 설정값 설정
     */
    public void set(String path, Object value) {
        config.set(path, value);
        saveConfig();
    }
    
    /**
     * 설정값 가져오기
     */
    public Object get(String path, Object defaultValue) {
        return config.get(path, defaultValue);
    }
}