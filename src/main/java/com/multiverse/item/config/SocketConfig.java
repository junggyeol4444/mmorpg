package com.multiverse. item. config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java. io.File;

public class SocketConfig {
    
    private FileConfiguration config;
    private File configFile;
    
    /**
     * 기본 생성자
     */
    public SocketConfig(File file) {
        this.configFile = file;
        loadConfig();
    }
    
    /**
     * 설정 파일 로드
     */
    private void loadConfig() {
        if (! configFile.exists()) {
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
        
        // 소켓 시스템 기본 설정
        config.set("socket. enable", true);
        config. set("socket.max-sockets-per-item", 3);
        config.set("socket.socket-unlock-enhance-level", 5);
        
        // 보석 끼우기 비용
        config.set("insert.base-cost", 500);
        config.set("insert.cost-multiplier", 100);
        config.set("insert.base-material-cost", 5);
        config.set("insert. material-multiplier", 2);
        
        // 보석 빼기 비용
        config.set("remove.base-cost", 300);
        config.set("remove.cost-multiplier", 50);
        config.set("remove.destroy-gem-chance", 0.1);
        
        // 보석 레벨별 보너스
        config.set("gem.common.stat-multiplier", 1.0);
        config.set("gem.uncommon.stat-multiplier", 1.  2);
        config.set("gem.rare.stat-multiplier", 1. 5);
        config.set("gem.epic.stat-multiplier", 1.8);
        
        // 소켓 색상 제한
        config.set("color-restriction.enable", true);
        config.set("color-restriction.penalty", 0.3);
    }
    
    /**
     * 설정 저장
     */
    public void saveConfig() {
        try {
            config. save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 소켓 시스템 활성화
     */
    public boolean isSocketEnabled() {
        return config.getBoolean("socket.enable", true);
    }
    
    /**
     * 아이템당 최대 소켓 개수
     */
    public int getMaxSocketsPerItem() {
        return config.getInt("socket.max-sockets-per-item", 3);
    }
    
    /**
     * 소켓 해제 필요 강화 레벨
     */
    public int getSocketUnlockEnhanceLevel() {
        return config.getInt("socket.socket-unlock-enhance-level", 5);
    }
    
    /**
     * 보석 끼우기 기본 비용
     */
    public int getInsertBaseCost() {
        return config.getInt("insert.base-cost", 500);
    }
    
    /**
     * 보석 끼우기 비용 배수
     */
    public int getInsertCostMultiplier() {
        return config.getInt("insert.cost-multiplier", 100);
    }
    
    /**
     * 보석 끼우기 기본 재료 비용
     */
    public int getInsertBaseMaterialCost() {
        return config.getInt("insert.base-material-cost", 5);
    }
    
    /**
     * 보석 끼우기 재료 비용 배수
     */
    public int getInsertMaterialMultiplier() {
        return config.getInt("insert.material-multiplier", 2);
    }
    
    /**
     * 보석 끼우기 비용 계산
     */
    public int calculateInsertCost(int socketIndex) {
        return getInsertBaseCost() + (socketIndex * getInsertCostMultiplier());
    }
    
    /**
     * 보석 빼기 기본 비용
     */
    public int getRemoveBaseCost() {
        return config. getInt("remove.base-cost", 300);
    }
    
    /**
     * 보석 빼기 비용 배수
     */
    public int getRemoveCostMultiplier() {
        return config.getInt("remove.cost-multiplier", 50);
    }
    
    /**
     * 보석 파괴 확률
     */
    public double getDestroyGemChance() {
        return config.getDouble("remove.destroy-gem-chance", 0.  1);
    }
    
    /**
     * 보석 레벨별 스탯 배수
     */
    public double getGemStatMultiplier(String gemRarity) {
        return config.getDouble("gem." + gemRarity. toLowerCase() + ".stat-multiplier", 1.0);
    }
    
    /**
     * 색상 제한 활성화
     */
    public boolean isColorRestrictionEnabled() {
        return config.getBoolean("color-restriction.enable", true);
    }
    
    /**
     * 색상 불일치 패널티
     */
    public double getColorMismatchPenalty() {
        return config.getDouble("color-restriction.penalty", 0.  3);
    }
    
    /**
     * 설정 다시 로드
     */
    public void reload() {
        loadConfig();
    }
}