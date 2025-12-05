package com.  multiverse.item. managers;

import com.multiverse.item.ItemCore;
import com.multiverse. item.data.*;
import com.multiverse.item.utils.RandomUtil;
import com.multiverse.item.utils. StatCalculator;
import java.util.*;

public class ItemGenerator {
    
    private ItemCore plugin;
    private ConfigManager configManager;
    private DataManager dataManager;
    
    public ItemGenerator(ItemCore plugin, ConfigManager configManager, DataManager dataManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.dataManager = dataManager;
    }
    
    /**
     * 아이템 생성
     */
    public CustomItem generateItem(String templateId, ItemRarity rarity) throws Exception {
        // 템플릿 로드
        CustomItem template = plugin.getItemManager().getItemById(templateId);
        if (template == null) {
            throw new Exception("템플릿을 찾을 수 없습니다: " + templateId);
        }
        
        // 템플릿 복사
        CustomItem item = template.clone();
        
        // 등급 설정
        item.setRarity(rarity);
        
        // 기본 스탯 계산
        calculateBaseStats(item);
        
        // 옵션 롤링
        generateOptions(item);
        
        // 소켓 설정
        generateSockets(item);
        
        return item;
    }
    
    /**
     * 기본 스탯 계산
     */
    private void calculateBaseStats(CustomItem item) {
        Map<String, Double> baseStats = item.getBaseStats();
        if (baseStats == null) {
            return;
        }
        
        // 등급에 따른 스탯 배수 적용
        double multiplier = item.getRarity().getStatMultiplier();
        
        for (String stat : baseStats.keySet()) {
            double baseValue = baseStats.get(stat);
            double calculatedValue = baseValue * multiplier;
            baseStats.put(stat, calculatedValue);
        }
    }
    
    /**
     * 옵션 생성
     */
    private void generateOptions(CustomItem item) throws Exception {
        List<ItemOption> options = new ArrayList<>();
        
        // 아이템 등급에 따른 옵션 개수 결정
        int optionCount = getOptionCountByRarity(item.getRarity());
        
        // 옵션 풀에서 랜덤 선택
        List<ItemOptionTemplate> optionPool = dataManager.loadOptionPool();
        
        if (optionPool == null || optionPool.isEmpty()) {
            plugin.getLogger().warning("옵션 풀을 로드할 수 없습니다!");
            return;
        }
        
        // 중복 없이 옵션 선택
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
            
            options.add(option);
        }
        
        item.setOptions(options);
    }
    
    /**
     * 소켓 생성
     */
    private void generateSockets(CustomItem item) {
        int sockets = 0;
        
        ItemRarity rarity = item.getRarity();
        switch (rarity) {
            case COMMON:
                sockets = 0;
                break;
            case UNCOMMON:
                sockets = RandomUtil.nextInt(0, 1);
                break;
            case RARE:
                sockets = RandomUtil.nextInt(1, 2);
                break;
            case EPIC:
                sockets = RandomUtil.nextInt(2, 3);
                break;
            case LEGENDARY:
                sockets = RandomUtil.nextInt(3, 4);
                break;
            case MYTHIC:
                sockets = RandomUtil.nextInt(4, 5);
                break;
        }
        
        item.setSockets(sockets);
    }
    
    /**
     * 등급에 따른 옵션 개수 반환
     */
    private int getOptionCountByRarity(ItemRarity rarity) {
        switch (rarity) {
            case COMMON:
                return 0;
            case UNCOMMON:
                return RandomUtil.nextInt(1, 2);
            case RARE:
                return RandomUtil.nextInt(2, 3);
            case EPIC:
                return RandomUtil.nextInt(3, 4);
            case LEGENDARY:
                return RandomUtil.nextInt(4, 5);
            case MYTHIC:
                return RandomUtil.nextInt(5, 6);
            default:
                return 0;
        }
    }
    
    /**
     * 랜덤 아이템 생성
     */
    public CustomItem generateRandomItem(String itemType) throws Exception {
        // 아이템 타입에 따른 템플릿 로드
        List<ItemOptionTemplate> templates = dataManager.loadItemTemplatesByType(itemType);
        
        if (templates == null || templates.isEmpty()) {
            throw new Exception("해당 타입의 템플릿을 찾을 수 없습니다: " + itemType);
        }
        
        // 랜덤 템플릿 선택
        ItemOptionTemplate randomTemplate = templates.get(RandomUtil.nextInt(0, templates.size() - 1));
        
        // 랜덤 등급 결정 (등급 확률 고려)
        ItemRarity rarity = getRandomRarity();
        
        return generateItem(randomTemplate.getId(), rarity);
    }
    
    /**
     * 랜덤 등급 반환 (확률 고려)
     */
    private ItemRarity getRandomRarity() {
        double random = Math.random() * 100;
        
        if (random < 50) {
            return ItemRarity. COMMON;
        } else if (random < 75) {
            return ItemRarity.UNCOMMON;
        } else if (random < 90) {
            return ItemRarity. RARE;
        } else if (random < 97) {
            return ItemRarity.EPIC;
        } else if (random < 99. 5) {
            return ItemRarity.LEGENDARY;
        } else {
            return ItemRarity.MYTHIC;
        }
    }
}