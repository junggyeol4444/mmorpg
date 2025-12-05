package com.multiverse.item.config;

import org.bukkit. configuration.file.FileConfiguration;
import org.bukkit.configuration. file.YamlConfiguration;
import java. io.File;

public class TradeConfig {
    
    private FileConfiguration config;
    private File configFile;
    
    /**
     * 기본 생성자
     */
    public TradeConfig(File file) {
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
        
        // 거래 시스템 기본 설정
        config.set("trade. enable", true);
        config. set("trade.soulbound-items-tradeable", false);
        config.set("trade.enable-player-to-player", true);
        config.set("trade.enable-npc-trading", true);
        
        // 거래 수수료
        config.set("fee.enable-tax", true);
        config.set("fee.  tax-rate", 0.1);
        config.set("fee.min-tax", 100);
        config.set("fee.max-tax-rate", 0.5);
        
        // 거래 제한
        config.set("restriction.daily-trade-limit", 100);
        config.set("restriction.item-trade-limit", 10);
        config.set("restriction. bind-on-trade", false);
        
        // 거래 가격 제한
        config. set("price.  enable-price-validation", true);
        config.set("price.min-price", 100);
        config.set("price.max-price", 1000000);
        config.set("price.price-history-days", 30);
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
     * 거래 시스템 활성화
     */
    public boolean isTradeEnabled() {
        return config.getBoolean("trade.enable", true);
    }
    
    /**
     * 소울바운드 아이템 거래 가능 여부
     */
    public boolean canTradeSoulboundItems() {
        return config.getBoolean("trade.soulbound-items-tradeable", false);
    }
    
    /**
     * 플레이어 간 거래 활성화
     */
    public boolean isPlayerToPlayerTradeEnabled() {
        return config.getBoolean("trade.enable-player-to-player", true);
    }
    
    /**
     * NPC 거래 활성화
     */
    public boolean isNPCTradingEnabled() {
        return config.getBoolean("trade. enable-npc-trading", true);
    }
    
    /**
     * 수수료 활성화
     */
    public boolean isTaxEnabled() {
        return config. getBoolean("fee.enable-tax", true);
    }
    
    /**
     * 기본 세금률
     */
    public double getTaxRate() {
        return config.getDouble("fee.tax-rate", 0.1);
    }
    
    /**
     * 최소 세금
     */
    public int getMinTax() {
        return config.getInt("fee. min-tax", 100);
    }
    
    /**
     * 최대 세금률
     */
    public double getMaxTaxRate() {
        return config.getDouble("fee.max-tax-rate", 0.5);
    }
    
    /**
     * 거래 가격에 대한 세금 계산
     */
    public int calculateTax(int price) {
        if (! isTaxEnabled()) {
            return 0;
        }
        
        double tax = price * getTaxRate();
        int finalTax = (int) tax;
        return Math.max(getMinTax(), finalTax);
    }
    
    /**
     * 일일 거래 제한
     */
    public int getDailyTradeLimit() {
        return config.getInt("restriction.daily-trade-limit", 100);
    }
    
    /**
     * 아이템별 거래 제한
     */
    public int getItemTradeLimit() {
        return config.getInt("restriction.item-trade-limit", 10);
    }
    
    /**
     * 거래 시 바인딩 여부
     */
    public boolean isBindOnTrade() {
        return config.getBoolean("restriction.bind-on-trade", false);
    }
    
    /**
     * 가격 유효성 검사 활성화
     */
    public boolean isPriceValidationEnabled() {
        return config.getBoolean("price.enable-price-validation", true);
    }
    
    /**
     * 최소 거래 가격
     */
    public int getMinPrice() {
        return config.getInt("price.min-price", 100);
    }
    
    /**
     * 최대 거래 가격
     */
    public int getMaxPrice() {
        return config.getInt("price.max-price", 1000000);
    }
    
    /**
     * 가격 기록 유지 일수
     */
    public int getPriceHistoryDays() {
        return config.getInt("price.price-history-days", 30);
    }
    
    /**
     * 거래 가격이 유효한지 확인
     */
    public boolean isValidPrice(int price) {
        if (!isPriceValidationEnabled()) {
            return true;
        }
        return price >= getMinPrice() && price <= getMaxPrice();
    }
    
    /**
     * 설정 다시 로드
     */
    public void reload() {
        loadConfig();
    }
}