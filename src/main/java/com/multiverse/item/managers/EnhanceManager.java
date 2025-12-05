package com.multiverse.item.managers;

import com.multiverse.item.ItemCore;
import com.multiverse.item. data.*;
import com.  multiverse.item.utils.RandomUtil;
import com.multiverse.item.events.ItemEnhanceEvent;
import com. multiverse.item.events.ItemEnhanceFailEvent;
import org.bukkit.  Bukkit;
import java.util.*;

public class EnhanceManager {
    
    private ItemCore plugin;
    private ConfigManager configManager;
    private DataManager dataManager;
    
    // 강화 성공 확률 (레벨별)
    private static final Map<Integer, Double> ENHANCE_SUCCESS_RATES = new HashMap<>();
    
    static {
        ENHANCE_SUCCESS_RATES.put(0, 100.0); // +0 -> +1: 100%
        ENHANCE_SUCCESS_RATES.put(1, 100.0); // +1 -> +2: 100%
        ENHANCE_SUCCESS_RATES.put(2, 100.0); // +2 -> +3: 100%
        ENHANCE_SUCCESS_RATES.put(3, 100.0); // +3 -> +4: 100%
        ENHANCE_SUCCESS_RATES.put(4, 100.0); // +4 -> +5: 100%
        ENHANCE_SUCCESS_RATES. put(5, 80.0);  // +5 -> +6: 80%
        ENHANCE_SUCCESS_RATES.put(6, 75.0);  // +6 -> +7: 75%
        ENHANCE_SUCCESS_RATES.put(7, 70. 0);  // +7 -> +8: 70%
        ENHANCE_SUCCESS_RATES. put(8, 65.0);  // +8 -> +9: 65%
        ENHANCE_SUCCESS_RATES.put(9, 60.0);  // +9 -> +10: 60%
        ENHANCE_SUCCESS_RATES.put(10, 50.0); // +10 -> +11: 50%
        ENHANCE_SUCCESS_RATES. put(11, 40.0); // +11 -> +12: 40%
        ENHANCE_SUCCESS_RATES. put(12, 30.0); // +12 -> +13: 30%
        ENHANCE_SUCCESS_RATES.put(13, 20.0); // +13 -> +14: 20%
        ENHANCE_SUCCESS_RATES.put(14, 10.0); // +14 -> +15: 10%
    }
    
    public EnhanceManager(ItemCore plugin, ConfigManager configManager, DataManager dataManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.dataManager = dataManager;
    }
    
    /**
     * 강화 시도
     */
    public EnhanceResult enhance(CustomItem item) {
        int currentLevel = item.getEnhanceLevel();
        
        // 최대 강화 확인
        if (currentLevel >= item.getMaxEnhance()) {
            return EnhanceResult.MAX_LEVEL;
        }
        
        // 강화 성공 확률 계산
        double successRate = getSuccessRate(currentLevel);
        
        // 강화 시도 기록
        EnhanceAttempt attempt = new EnhanceAttempt();
        attempt.setItemId(item.getItemId());
        attempt.setBeforeLevel(currentLevel);
        attempt. setSuccessRate(successRate);
        attempt.setTimestamp(System.currentTimeMillis());
        
        // 성공/실패 판정
        boolean success = Math.random() * 100 < successRate;
        
        EnhanceResult result;
        if (success) {
            // 강화 성공
            item.setEnhanceLevel(currentLevel + 1);
            attempt.setSuccess(true);
            attempt.setAfterLevel(currentLevel + 1);
            result = EnhanceResult.SUCCESS;
            
            // 스탯 증가
            applyEnhanceBonus(item);
            
            // 이벤트 발생
            ItemEnhanceEvent event = new ItemEnhanceEvent(item, currentLevel, currentLevel + 1);
            Bukkit.getPluginManager().callEvent(event);
            
        } else {
            // 강화 실패
            attempt.setSuccess(false);
            attempt.setAfterLevel(currentLevel);
            
            // 실패 유형 결정
            if (Math.random() < 0.3) {
                // 30% 확률로 다운그레이드
                if (currentLevel > 0) {
                    item.setEnhanceLevel(currentLevel - 1);
                    attempt. setAfterLevel(currentLevel - 1);
                    result = EnhanceResult.DOWNGRADE;
                } else {
                    result = EnhanceResult.FAIL;
                }
            } else {
                result = EnhanceResult.FAIL;
            }
            
            // 이벤트 발생
            ItemEnhanceFailEvent event = new ItemEnhanceFailEvent(item, currentLevel, result);
            Bukkit.getPluginManager().callEvent(event);
        }
        
        // 강화 시도 기록 저장
        dataManager.saveEnhanceAttempt(attempt);
        
        return result;
    }
    
    /**
     * 성공 확률 조회
     */
    public double getSuccessRate(int level) {
        return ENHANCE_SUCCESS_RATES. getOrDefault(level, 10.0);
    }
    
    /**
     * 강화 보너스 적용
     */
    private void applyEnhanceBonus(CustomItem item) {
        int enhanceLevel = item.getEnhanceLevel();
        
        // 강화 레벨에 따른 스탯 증가
        double statBonus = enhanceLevel * 5.0; // 레벨당 5% 증가
        
        Map<String, Double> baseStats = item.getBaseStats();
        if (baseStats != null) {
            for (String stat : baseStats.keySet()) {
                double currentValue = baseStats.get(stat);
                double newValue = currentValue * (1 + (statBonus / 100));
                baseStats.put(stat, newValue);
            }
        }
    }
    
    /**
     * 강화 취소 가능 여부
     */
    public boolean canEnhance(CustomItem item) {
        return item.getEnhanceLevel() < item.getMaxEnhance();
    }
    
    /**
     * 강화 레벨 직접 설정 (관리자용)
     */
    public void setEnhanceLevel(CustomItem item, int level) {
        if (level < 0 || level > item.getMaxEnhance()) {
            throw new IllegalArgumentException("잘못된 강화 레벨: " + level);
        }
        
        item. setEnhanceLevel(level);
        applyEnhanceBonus(item);
    }
}