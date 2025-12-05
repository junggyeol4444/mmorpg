package com.multiverse.item.managers;

import org.bukkit.inventory.ItemStack;
import com.multiverse.item.ItemCore;
import com.multiverse. item.data.CustomItem;
import com.multiverse.item.events.ItemDisassembleEvent;
import org.bukkit. Bukkit;
import java.util.*;

public class DisassembleSystem {
    
    private ItemCore plugin;
    private ConfigManager configManager;
    private DataManager dataManager;
    
    public DisassembleSystem(ItemCore plugin, ConfigManager configManager, DataManager dataManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.dataManager = dataManager;
    }
    
    /**
     * 분해 가능 여부 확인
     */
    public boolean canDisassemble(CustomItem item) {
        // 귀속 아이템은 분해 불가
        if (item.isSoulbound()) {
            return false;
        }
        
        // 세트 아이템은 분해 불가
        if (item.getSetId() != null && ! item.getSetId().isEmpty()) {
            return false;
        }
        
        // 특수 아이템은 분해 불가
        if (item.isUnbreakable()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 아이템 분해
     */
    public Map<String, Integer> disassembleItem(CustomItem item) throws Exception {
        if (!canDisassemble(item)) {
            throw new Exception("이 아이템은 분해할 수 없습니다!");
        }
        
        Map<String, Integer> materials = new HashMap<>();
        
        // 아이템 등급에 따른 기본 재료 계산
        int baseMaterial = calculateBaseMaterial(item. getRarity());
        
        // 강화 레벨에 따른 추가 재료
        int enhanceMaterial = item.getEnhanceLevel() * 10;
        
        // 옵션 개수에 따른 추가 재료
        int optionMaterial = (item.getOptions() != null ?  item.getOptions().size() : 0) * 5;
        
        // 소켓 개수에 따른 추가 재료
        int socketMaterial = (item.getGems() != null ? item.getGems().size() : 0) * 3;
        
        // 총 재료
        int totalMaterial = baseMaterial + enhanceMaterial + optionMaterial + socketMaterial;
        
        materials.put("기본재료", baseMaterial);
        materials.put("강화재료", enhanceMaterial);
        materials.put("옵션재료", optionMaterial);
        materials.put("소켓재료", socketMaterial);
        materials.put("합계", totalMaterial);
        
        // 보석 반환
        if (item.getGems() != null) {
            for (int i = 0; i < item. getGems().size(); i++) {
                if (item.getGems().get(i) != null) {
                    materials.put("보석_" + i, 1);
                }
            }
        }
        
        // 이벤트 발생
        ItemDisassembleEvent event = new ItemDisassembleEvent(item, materials);
        Bukkit. getPluginManager().callEvent(event);
        
        return materials;
    }
    
    /**
     * 등급에 따른 기본 재료 계산
     */
    private int calculateBaseMaterial(ItemRarity rarity) {
        switch (rarity) {
            case COMMON:
                return 10;
            case UNCOMMON:
                return 20;
            case RARE:
                return 50;
            case EPIC:
                return 100;
            case LEGENDARY:
                return 200;
            case MYTHIC:
                return 500;
            default:
                return 0;
        }
    }
    
    /**
     * 분해 비용 계산
     */
    public double calculateDisassembleCost(CustomItem item) {
        if (!canDisassemble(item)) {
            return 0;
        }
        
        double baseCost = 1000;
        
        // 등급에 따른 비용 배수
        double rarityMultiplier = item.getRarity().getStatMultiplier();
        
        // 강화 레벨에 따른 추가 비용
        double enhanceCost = item.getEnhanceLevel() * 500;
        
        // 옵션 개수에 따른 추가 비용
        double optionCost = (item.getOptions() != null ?  item.getOptions().size() : 0) * 100;
        
        return (baseCost * rarityMultiplier) + enhanceCost + optionCost;
    }
    
    /**
     * 분해 가능 아이템 목록 조회
     */
    public List<CustomItem> getDisassemblableItems(List<CustomItem> items) {
        List<CustomItem> result = new ArrayList<>();
        
        for (CustomItem item : items) {
            if (canDisassemble(item)) {
                result. add(item);
            }
        }
        
        return result;
    }
}