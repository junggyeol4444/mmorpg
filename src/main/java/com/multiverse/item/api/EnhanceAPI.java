package com.multiverse.item.api;

import com.multiverse.item.ItemCore;
import com.multiverse.item. data.CustomItem;
import com.multiverse.item.data. EnhanceResult;
import org.bukkit. entity.Player;

public class EnhanceAPI {
    
    private static EnhanceAPI instance;
    private ItemCore plugin;
    
    /**
     * 싱글톤 인스턴스 가져오기
     */
    public static EnhanceAPI getInstance() {
        if (instance == null) {
            instance = new EnhanceAPI();
        }
        return instance;
    }
    
    /**
     * 플러그인 초기화
     */
    public void init(ItemCore plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 강화 실행
     */
    public EnhanceResult enhance(Player player, CustomItem item) {
        if (player == null || item == null) {
            return EnhanceResult. FAIL;
        }
        
        return plugin.getEnhanceManager(). enhance(player, item);
    }
    
    /**
     * 강화 가능 여부 확인
     */
    public boolean canEnhance(CustomItem item) {
        if (item == null) {
            return false;
        }
        
        int maxLevel = 15; // 설정에서 가져오기
        return item.getEnhanceLevel() < maxLevel;
    }
    
    /**
     * 강화 레벨 가져오기
     */
    public int getEnhanceLevel(CustomItem item) {
        if (item == null) {
            return 0;
        }
        return item.getEnhanceLevel();
    }
    
    /**
     * 강화 레벨 설정
     */
    public void setEnhanceLevel(CustomItem item, int level) {
        if (item != null) {
            item.setEnhanceLevel(Math.max(0, Math.min(level, 15)));
        }
    }
    
    /**
     * 강화 성공률 계산
     */
    public double calculateSuccessRate(int enhanceLevel) {
        double rate = 100.0 - (enhanceLevel * 5.0);
        return Math.max(10.0, rate);
    }
    
    /**
     * 강화 비용 계산
     */
    public int calculateCost(int enhanceLevel) {
        return 1000 + (enhanceLevel * 500);
    }
    
    /**
     * 강화 재료 비용 계산
     */
    public int calculateMaterialCost(int enhanceLevel) {
        return 10 + (enhanceLevel * 5);
    }
    
    /**
     * 다운그레이드 확률 계산
     */
    public double calculateDowngradeChance(int enhanceLevel) {
        double chance = enhanceLevel * 2.0;
        return Math. min(50.0, chance);
    }
    
    /**
     * 강화 정보 조회
     */
    public String getEnhanceInfo(CustomItem item) {
        if (item == null) {
            return "";
        }
        
        int currentLevel = item.getEnhanceLevel();
        double successRate = calculateSuccessRate(currentLevel);
        int cost = calculateCost(currentLevel);
        
        return "§7현재 강화: +§a" + currentLevel + "\n" +
               "§7성공률: §a" + String.format("%.1f%%", successRate) + "\n" +
               "§7비용: §c" + cost + " Gold";
    }
    
    /**
     * 강화 레벨 범위 확인
     */
    public boolean isValidEnhanceLevel(int level) {
        return level >= 0 && level <= 15;
    }
    
    /**
     * 최대 강화 레벨 가져오기
     */
    public int getMaxEnhanceLevel() {
        return 15;
    }
}