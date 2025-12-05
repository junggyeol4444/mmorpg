package com.multiverse.item.api;

import com.multiverse.item.ItemCore;
import com.multiverse.item.data.CustomItem;
import org.bukkit.entity.Player;
import java.util.Map;

public class ItemCoreAPI {
    
    private static ItemCoreAPI instance;
    private ItemCore plugin;
    
    private ItemAPI itemAPI;
    private EnhanceAPI enhanceAPI;
    private OptionAPI optionAPI;
    private SetAPI setAPI;
    private GemAPI gemAPI;
    private TradeAPI tradeAPI;
    
    /**
     * 싱글톤 인스턴스 가져오기
     */
    public static ItemCoreAPI getInstance() {
        if (instance == null) {
            instance = new ItemCoreAPI();
        }
        return instance;
    }
    
    /**
     * 플러그인 초기화
     */
    public void init(ItemCore plugin) {
        this.plugin = plugin;
        
        itemAPI = ItemAPI.getInstance();
        itemAPI.init(plugin);
        
        enhanceAPI = EnhanceAPI.getInstance();
        enhanceAPI.init(plugin);
        
        optionAPI = OptionAPI.getInstance();
        optionAPI.init(plugin);
        
        setAPI = SetAPI.getInstance();
        setAPI. init(plugin);
        
        gemAPI = GemAPI.getInstance();
        gemAPI.init(plugin);
        
        tradeAPI = TradeAPI.getInstance();
        tradeAPI.init(plugin);
    }
    
    /**
     * Item API 가져오기
     */
    public ItemAPI getItemAPI() {
        return itemAPI;
    }
    
    /**
     * Enhance API 가져오기
     */
    public EnhanceAPI getEnhanceAPI() {
        return enhanceAPI;
    }
    
    /**
     * Option API 가져오기
     */
    public OptionAPI getOptionAPI() {
        return optionAPI;
    }
    
    /**
     * Set API 가져오기
     */
    public SetAPI getSetAPI() {
        return setAPI;
    }
    
    /**
     * Gem API 가져오기
     */
    public GemAPI getGemAPI() {
        return gemAPI;
    }
    
    /**
     * Trade API 가져오기
     */
    public TradeAPI getTradeAPI() {
        return tradeAPI;
    }
    
    /**
     * 플러그인 정보 조회
     */
    public String getPluginVersion() {
        return plugin.getDescription().getVersion();
    }
    
    /**
     * 플러그인 활성화 여부
     */
    public boolean isPluginEnabled() {
        return plugin.isEnabled();
    }
    
    /**
     * 통합 아이템 정보 조회
     */
    public String getFullItemInfo(CustomItem item) {
        if (item == null) {
            return "";
        }
        
        StringBuilder info = new StringBuilder();
        info.append(itemAPI.getItemInfo(item)). append("\n");
        info. append(enhanceAPI.getEnhanceInfo(item)).append("\n");
        
        return info.toString();
    }
}