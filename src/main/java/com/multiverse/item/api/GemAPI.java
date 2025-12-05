package com.multiverse.item. api;

import com.multiverse.item.ItemCore;
import com.multiverse.item.data. Gem;
import com.multiverse. item.data.GemType;
import com.multiverse.item.data.GemRarity;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Map;

public class GemAPI {
    
    private static GemAPI instance;
    private ItemCore plugin;
    
    /**
     * 싱글톤 인스턴스 가져오기
     */
    public static GemAPI getInstance() {
        if (instance == null) {
            instance = new GemAPI();
        }
        return instance;
    }
    
    /**
     * 플러그인 초기화
     */
    public void init(ItemCore plugin) {
        this. plugin = plugin;
    }
    
    /**
     * 보석 생성
     */
    public Gem createGem(String gemId, String name, GemType type, GemRarity rarity) {
        Gem gem = new Gem();
        gem.setGemId(gemId);
        gem.setName(name);
        gem.setType(type);
        gem.setRarity(rarity);
        return gem;
    }
    
    /**
     * 보석 타입별 스탯 배수 가져오기
     */
    public double getTypeMultiplier(GemType type) {
        switch (type) {
            case STRENGTH:
                return 1.2;
            case DEXTERITY:
                return 1. 15;
            case INTELLIGENCE:
                return 1.25;
            case VITALITY:
                return 1. 1;
            case RESISTANCE:
                return 1.05;
            default:
                return 1.0;
        }
    }
    
    /**
     * 보석 등급별 스탯 배수 가져오기
     */
    public double getRarityMultiplier(GemRarity rarity) {
        switch (rarity) {
            case COMMON:
                return 1.0;
            case UNCOMMON:
                return 1.2;
            case RARE:
                return 1.5;
            case EPIC:
                return 1.8;
            case LEGENDARY:
                return 2.2;
            default:
                return 1.0;
        }
    }
    
    /**
     * 보석 효과값 계산
     */
    public double calculateGemValue(Gem gem, double baseValue) {
        if (gem == null) {
            return baseValue;
        }
        
        double typeMultiplier = getTypeMultiplier(gem.getType());
        double rarityMultiplier = getRarityMultiplier(gem.getRarity());
        
        return baseValue * typeMultiplier * rarityMultiplier;
    }
    
    /**
     * 모든 보석 조회
     */
    public Map<String, Gem> getAllGems() {
        return plugin. getGemManager().getAllGems();
    }
    
    /**
     * 보석 ID로 조회
     */
    public Gem getGemById(String gemId) {
        return plugin. getGemManager().getGemById(gemId);
    }
    
    /**
     * 타입별 보석 조회
     */
    public List<Gem> getGemsByType(GemType type) {
        return plugin.getGemManager().getGemsByType(type);
    }
    
    /**
     * 등급별 보석 조회
     */
    public List<Gem> getGemsByRarity(GemRarity rarity) {
        return plugin.getGemManager().getGemsByRarity(rarity);
    }
    
    /**
     * 보석 정보 조회
     */
    public String getGemInfo(Gem gem) {
        if (gem == null) {
            return "";
        }
        
        return "§b" + gem.getName() + "\n" +
               "§7타입: " + gem.getType() + "\n" +
               "§7등급: " + gem.getRarity();
    }
    
    /**
     * 보석 유효성 확인
     */
    public boolean isValidGem(Gem gem) {
        return gem != null && gem.getGemId() != null && gem.getName() != null;
    }
}