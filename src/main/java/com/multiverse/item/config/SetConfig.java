package com. multiverse.item.  config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.  io.File;
import java.util.List;

public class SetConfig {
    
    private FileConfiguration config;
    private File configFile;
    
    /**
     * 기본 생성자
     */
    public SetConfig(File file) {
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
        
        // 세트 시스템 기본 설정
        config.set("set.enable", true);
        config.set("set.max-items-per-set", 5);
        config.set("set.min-items-for-bonus", 2);
        
        // 세트 보너스
        config.set("bonus.2-piece. enable", true);
        config. set("bonus.2-piece. stat-increase", 0.  1);
        
        config.set("bonus.3-piece.enable", true);
        config.set("bonus.3-piece.stat-increase", 0. 2);
        
        config.set("bonus.  4-piece.enable", true);
        config.set("bonus. 4-piece.stat-increase", 0.3);
        
        config.set("bonus.5-piece.enable", true);
        config.set("bonus.5-piece.stat-increase", 0.5);
        
        // 세트 효과 지속시간
        config.set("effect.duration-ticks", 200);
        config.set("effect.enable-particles", true);
    }
    
    /**
     * 설정 저장
     */
    public void saveConfig() {
        try {
            config.  save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 세트 시스템 활성화
     */
    public boolean isSetEnabled() {
        return config.getBoolean("set. enable", true);
    }
    
    /**
     * 최대 세트 아이템 개수
     */
    public int getMaxItemsPerSet() {
        return config.getInt("set.max-items-per-set", 5);
    }
    
    /**
     * 보너스 활성화 최소 아이템 개수
     */
    public int getMinItemsForBonus() {
        return config.getInt("set.min-items-for-bonus", 2);
    }
    
    /**
     * 2세트 보너스 활성화
     */
    public boolean is2PieceBonusEnabled() {
        return config.getBoolean("bonus. 2-piece.enable", true);
    }
    
    /**
     * 2세트 보너스 스탯 증가율
     */
    public double get2PieceBonusIncrease() {
        return config.getDouble("bonus.2-piece.stat-increase", 0.1);
    }
    
    /**
     * 3세트 보너스 활성화
     */
    public boolean is3PieceBonusEnabled() {
        return config. getBoolean("bonus.3-piece.enable", true);
    }
    
    /**
     * 3세트 보너스 스탯 증가율
     */
    public double get3PieceBonusIncrease() {
        return config.getDouble("bonus.3-piece.stat-increase", 0.2);
    }
    
    /**
     * 4세트 보너스 활성화
     */
    public boolean is4PieceBonusEnabled() {
        return config. getBoolean("bonus.4-piece.enable", true);
    }
    
    /**
     * 4세트 보너스 스탯 증가율
     */
    public double get4PieceBonusIncrease() {
        return config.getDouble("bonus.4-piece.stat-increase", 0.3);
    }
    
    /**
     * 5세트 보너스 활성화
     */
    public boolean is5PieceBonusEnabled() {
        return config. getBoolean("bonus.5-piece.enable", true);
    }
    
    /**
     * 5세트 보너스 스탯 증가율
     */
    public double get5PieceBonusIncrease() {
        return config.getDouble("bonus.5-piece.stat-increase", 0.5);
    }
    
    /**
     * 세트 아이템 개수에 따른 보너스 가져오기
     */
    public double getBonusIncrease(int itemCount) {
        switch (itemCount) {
            case 2:
                return is2PieceBonusEnabled() ? get2PieceBonusIncrease() : 0;
            case 3:
                return is3PieceBonusEnabled() ? get3PieceBonusIncrease() : 0;
            case 4:
                return is4PieceBonusEnabled() ? get4PieceBonusIncrease() : 0;
            case 5:
                return is5PieceBonusEnabled() ? get5PieceBonusIncrease() : 0;
            default:
                return 0;
        }
    }
    
    /**
     * 세트 효과 지속시간 (틱)
     */
    public int getEffectDurationTicks() {
        return config.getInt("effect.duration-ticks", 200);
    }
    
    /**
     * 세트 효과 파티클 표시 활성화
     */
    public boolean isParticlesEnabled() {
        return config. getBoolean("effect.enable-particles", true);
    }
    
    /**
     * 설정 다시 로드
     */
    public void reload() {
        loadConfig();
    }
}