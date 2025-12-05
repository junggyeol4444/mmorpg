package com.multiverse.item. config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OptionConfig {
    
    private FileConfiguration config;
    private File configFile;
    
    /**
     * 기본 생성자
     */
    public OptionConfig(File file) {
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
        
        // 옵션 시스템 기본 설정
        config.set("option.enable-random-generation", true);
        config.set("option.min-options", 1);
        config. set("option.max-options", 5);
        
        // 옵션 리롤
        config.set("reroll.base-cost", 1000);
        config.set("reroll.cost-multiplier", 500);
        config.set("reroll.cost-increases-per-reroll", true);
        
        // 옵션 발동 확률
        config.set("trigger.default-chance", 100.0);
        config.set("trigger.critical-rate", 30.0);
        config.set("trigger.lifesteal-rate", 20.0);
        
        // 옵션 값 범위
        config.set("value.damage. min", 1. 0);
        config.set("value.damage.max", 50.0);
        config.set("value.defense.min", 1.0);
        config.set("value. defense.max", 30.0);
        config.set("value.health.min", 10.0);
        config.set("value.health.max", 100.0);
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
     * 랜덤 옵션 생성 활성화
     */
    public boolean isRandomGenerationEnabled() {
        return config.getBoolean("option.enable-random-generation", true);
    }
    
    /**
     * 최소 옵션 개수
     */
    public int getMinOptions() {
        return config.getInt("option.min-options", 1);
    }
    
    /**
     * 최대 옵션 개수
     */
    public int getMaxOptions() {
        return config.getInt("option. max-options", 5);
    }
    
    /**
     * 리롤 기본 비용
     */
    public int getRerollBaseCost() {
        return config.getInt("reroll.base-cost", 1000);
    }
    
    /**
     * 리롤 비용 배수
     */
    public int getRerollCostMultiplier() {
        return config.getInt("reroll.cost-multiplier", 500);
    }
    
    /**
     * 리롤 비용 증가 활성화
     */
    public boolean isRerollCostIncreases() {
        return config.getBoolean("reroll.cost-increases-per-reroll", true);
    }
    
    /**
     * 리롤 레벨에 따른 비용 계산
     */
    public int calculateRerollCost(int rerollCount) {
        if (!isRerollCostIncreases()) {
            return getRerollBaseCost();
        }
        return getRerollBaseCost() + (rerollCount * getRerollCostMultiplier());
    }
    
    /**
     * 트리거 기본 발동 확률
     */
    public double getDefaultTriggerChance() {
        return config.getDouble("trigger.default-chance", 100.0);
    }
    
    /**
     * 치명타 발동 확률
     */
    public double getCriticalRateTrigger() {
        return config.getDouble("trigger.critical-rate", 30.0);
    }
    
    /**
     * 생명력 흡수 발동 확률
     */
    public double getLifestealRateTrigger() {
        return config.getDouble("trigger.lifesteal-rate", 20.0);
    }
    
    /**
     * 옵션값 범위 최소값
     */
    public double getOptionValueMin(String optionType) {
        return config.getDouble("value." + optionType + ".min", 1.0);
    }
    
    /**
     * 옵션값 범위 최대값
     */
    public double getOptionValueMax(String optionType) {
        return config.getDouble("value." + optionType + ".max", 50.0);
    }
    
    /**
     * 설정 다시 로드
     */
    public void reload() {
        loadConfig();
    }
}