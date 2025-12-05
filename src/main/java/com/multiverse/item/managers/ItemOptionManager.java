package com.multiverse.item. managers;

import com.multiverse.item.ItemCore;
import com.multiverse. item.data.*;
import com.multiverse.item.utils.RandomUtil;
import com.multiverse.item.events.ItemOptionRerollEvent;
import org.bukkit. Bukkit;
import java.util.*;

public class ItemOptionManager {
    
    private ItemCore plugin;
    private ConfigManager configManager;
    private DataManager dataManager;
    
    public ItemOptionManager(ItemCore plugin, ConfigManager configManager, DataManager dataManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.dataManager = dataManager;
    }
    
    /**
     * 옵션 리롤
     */
    public boolean rerollOptions(CustomItem item, int cost) {
        // 옵션 확인
        List<ItemOption> options = item. getOptions();
        if (options == null || options.isEmpty()) {
            return false;
        }
        
        // 나중에 경제 시스템 연동
        // 비용 차감 로직
        
        // 기존 옵션 백업
        List<ItemOption> previousOptions = new ArrayList<>(options);
        
        // 새로운 옵션 생성
        generateNewOptions(item);
        
        // 이벤트 발생
        ItemOptionRerollEvent event = new ItemOptionRerollEvent(item, previousOptions, item.getOptions());
        Bukkit.getPluginManager(). callEvent(event);
        
        return true;
    }
    
    /**
     * 새로운 옵션 생성
     */
    private void generateNewOptions(CustomItem item) {
        List<ItemOption> newOptions = new ArrayList<>();
        
        // 아이템 등급에 따른 옵션 개수
        int optionCount = getOptionCountByRarity(item.getRarity());
        
        try {
            List<ItemOptionTemplate> optionPool = dataManager.loadOptionPool();
            if (optionPool == null || optionPool.isEmpty()) {
                return;
            }
            
            Collections.shuffle(optionPool);
            
            for (int i = 0; i < Math.min(optionCount, optionPool.size()); i++) {
                ItemOptionTemplate template = optionPool.get(i);
                ItemOption option = new ItemOption();
                option.setName(template.getName());
                option.setType(template.getType());
                option.setTrigger(template.getTrigger());
                option.setPercentage(template.isPercentage());
                
                // 옵션 값 롤링
                double value = RandomUtil.nextDouble(template.getMinValue(), template.getMaxValue());
                option.setValue(value);
                
                newOptions.add(option);
            }
            
            item.setOptions(newOptions);
        } catch (Exception e) {
            plugin.getLogger().severe("옵션 생성 오류: " + e.getMessage());
        }
    }
    
    /**
     * 특정 옵션 추가
     */
    public void addOption(CustomItem item, ItemOption option) {
        List<ItemOption> options = item. getOptions();
        if (options == null) {
            options = new ArrayList<>();
            item.setOptions(options);
        }
        
        // 최대 옵션 개수 확인
        int maxOptions = getOptionCountByRarity(item.getRarity());
        if (options. size() >= maxOptions) {
            throw new RuntimeException("최대 옵션 개수를 초과했습니다!");
        }
        
        options.add(option);
    }
    
    /**
     * 특정 옵션 제거
     */
    public void removeOption(CustomItem item, int index) {
        List<ItemOption> options = item.getOptions();
        if (options == null || index < 0 || index >= options.size()) {
            return;
        }
        
        options.remove(index);
    }
    
    /**
     * 옵션 트리거 발동
     */
    public void triggerOption(CustomItem item, OptionTrigger trigger, Object data) {
        List<ItemOption> options = item.getOptions();
        if (options == null) {
            return;
        }
        
        for (ItemOption option : options) {
            if (option.getTrigger() == trigger) {
                applyOptionEffect(option, data);
            }
        }
    }
    
    /**
     * 옵션 효과 적용
     */
    private void applyOptionEffect(ItemOption option, Object data) {
        OptionType type = option.getType();
        double value = option.getValue();
        
        switch (type) {
            case DAMAGE:
                // 피해 증가
                break;
            case DEFENSE:
                // 방어력 증가
                break;
            case HEALTH:
                // 체력 증가
                break;
            case CRITICAL_RATE:
                // 치명타 확률 증가
                break;
            case CRITICAL_DAMAGE:
                // 치명타 피해 증가
                break;
            case SPEED:
                // 속도 증가
                break;
            case LIFESTEAL:
                // 생명력 흡수
                break;
            case RESISTANCE:
                // 저항 증가
                break;
        }
    }
    
    /**
     * 등급에 따른 옵션 개수 반환
     */
    private int getOptionCountByRarity(ItemRarity rarity) {
        switch (rarity) {
            case COMMON:
                return 0;
            case UNCOMMON:
                return 2;
            case RARE:
                return 3;
            case EPIC:
                return 4;
            case LEGENDARY:
                return 5;
            case MYTHIC:
                return 6;
            default:
                return 0;
        }
    }
    
    /**
     * 옵션 합계 계산
     */
    public Map<String, Double> calculateTotalStats(CustomItem item) {
        Map<String, Double> stats = new HashMap<>();
        
        // 기본 스탯 추가
        if (item.getBaseStats() != null) {
            stats.putAll(item.getBaseStats());
        }
        
        // 옵션에서 제공하는 스탯 추가
        List<ItemOption> options = item. getOptions();
        if (options != null) {
            for (ItemOption option : options) {
                String statName = convertOptionTypeToStat(option.getType());
                double value = option.getValue();
                
                stats.put(statName, stats. getOrDefault(statName, 0.0) + value);
            }
        }
        
        return stats;
    }
    
    /**
     * 옵션 타입을 스탯 이름으로 변환
     */
    private String convertOptionTypeToStat(OptionType type) {
        switch (type) {
            case DAMAGE:
                return "공격력";
            case DEFENSE:
                return "방어력";
            case HEALTH:
                return "체력";
            case CRITICAL_RATE:
                return "치명타확률";
            case CRITICAL_DAMAGE:
                return "치명타피해";
            case SPEED:
                return "속도";
            case LIFESTEAL:
                return "생명력흡수";
            case RESISTANCE:
                return "저항";
            default:
                return "기타";
        }
    }
}