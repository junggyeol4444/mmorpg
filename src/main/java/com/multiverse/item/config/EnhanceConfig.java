package com.multiverse.item.config;

import org.bukkit.configuration.file.FileConfiguration;
import org. bukkit.configuration.file.YamlConfiguration;
import java. io.File;

public class EnhanceConfig {
    
    private FileConfiguration config;
    private File configFile;
    
    /**
     * 기본 생성자
     */
    public EnhanceConfig(File file) {
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
        config = YamlConfiguration. loadConfiguration(configFile);
    }
    
    /**
     * 기본 설정 설정
     */
    private void setDefaults() {
        config = new YamlConfiguration();
        
        // 강화 기본 설정
        config. set("enhance.base-cost", 1000);
        config.set("enhance.cost-multiplier", 500);
        config.set("enhance.base-success-rate", 100. 0);
        config.set("enhance.success-rate-decrease", 5.0);
        config.set("enhance.min-success-rate", 10.0);
        
        // 강화 재료
        config.set("enhance.material-base-cost", 10);
        config.set("enhance. material-multiplier", 5);
        
        // 다운그레이드
        config.set("enhance.downgrade. enable", true);
        config.set("enhance.downgrade.chance-multiplier", 2.0);
        config.set("enhance.downgrade.max-chance", 50.0);
        
        // 실패 패널티
        config.set("enhance. penalty.enable", true);
        config.set("enhance.penalty.amount-multiplier", 1.0);
        config.set("enhance.penalty.max-penalty", 30.0);
        
        // 강화 제한
        config.set("enhance. max-level", 15);
        config.set("enhance.min-required-level", 1);
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
     * 강화 기본 비용
     */
    public int getBaseCost() {
        return config.getInt("enhance.base-cost", 1000);
    }
    
    /**
     * 강화 비용 배수
     */
    public int getCostMultiplier() {
        return config.getInt("enhance.cost-multiplier", 500);
    }
    
    /**
     * 강화 레벨에 따른 비용 계산
     */
    public int calculateCost(int enhanceLevel) {
        return getBaseCost() + (enhanceLevel * getCostMultiplier());
    }
    
    /**
     * 기본 성공률
     */
    public double getBaseSuccessRate() {
        return config.getDouble("enhance. base-success-rate", 100.0);
    }
    
    /**
     * 강화 레벨당 성공률 감소
     */
    public double getSuccessRateDecrease() {
        return config.getDouble("enhance.success-rate-decrease", 5.0);
    }
    
    /**
     * 최소 성공률
     */
    public double getMinSuccessRate() {
        return config. getDouble("enhance.min-success-rate", 10.0);
    }
    
    /**
     * 강화 레벨에 따른 성공률 계산
     */
    public double calculateSuccessRate(int enhanceLevel) {
        double rate = getBaseSuccessRate() - (enhanceLevel * getSuccessRateDecrease());
        return Math.max(getMinSuccessRate(), rate);
    }
    
    /**
     * 재료 기본 비용
     */
    public int getBaseMaterialCost() {
        return config.getInt("enhance.material-base-cost", 10);
    }
    
    /**
     * 재료 비용 배수
     */
    public int getMaterialMultiplier() {
        return config.getInt("enhance.material-multiplier", 5);
    }
    
    /**
     * 강화 레벨에 따른 재료 비용 계산
     */
    public int calculateMaterialCost(int enhanceLevel) {
        return getBaseMaterialCost() + (enhanceLevel * getMaterialMultiplier());
    }
    
    /**
     * 다운그레이드 활성화
     */
    public boolean isDowngradeEnabled() {
        return config.getBoolean("enhance.downgrade.enable", true);
    }
    
    /**
     * 다운그레이드 확률 배수
     */
    public double getDowngradeMultiplier() {
        return config.getDouble("enhance.downgrade.chance-multiplier", 2.0);
    }
    
    /**
     * 다운그레이드 최대 확률
     */
    public double getMaxDowngradeChance() {
        return config.getDouble("enhance.downgrade.max-chance", 50.0);
    }
    
    /**
     * 강화 레벨에 따른 다운그레이드 확률 계산
     */
    public double calculateDowngradeChance(int enhanceLevel) {
        if (! isDowngradeEnabled()) {
            return 0;
        }
        double chance = enhanceLevel * getDowngradeMultiplier();
        return Math. min(getMaxDowngradeChance(), chance);
    }
    
    /**
     * 실패 패널티 활성화
     */
    public boolean isPenaltyEnabled() {
        return config.getBoolean("enhance.penalty.enable", true);
    }
    
    /**
     * 실패 패널티 배수
     */
    public double getPenaltyMultiplier() {
        return config.getDouble("enhance.penalty.amount-multiplier", 1.0);
    }
    
    /**
     * 실패 패널티 최대값
     */
    public double getMaxPenalty() {
        return config.getDouble("enhance.penalty.max-penalty", 30.0);
    }
    
    /**
     * 강화 레벨에 따른 패널티 계산
     */
    public double calculatePenalty(int enhanceLevel) {
        if (!isPenaltyEnabled()) {
            return 0;
        }
        double penalty = enhanceLevel * getPenaltyMultiplier();
        return Math.min(getMaxPenalty(), penalty);
    }
    
    /**
     * 최대 강화 레벨
     */
    public int getMaxEnhanceLevel() {
        return config.getInt("enhance.max-level", 15);
    }
    
    /**
     * 최소 요구 레벨
     */
    public int getMinRequiredLevel() {
        return config.getInt("enhance.min-required-level", 1);
    }
    
    /**
     * 설정 다시 로드
     */
    public void reload() {
        loadConfig();
    }
}