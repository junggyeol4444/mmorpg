package com.multiverse.item.managers;

import com.multiverse.item.ItemCore;
import com. multiverse.item.data.*;
import com.multiverse.item.events.GemInsertEvent;
import com.multiverse.item.events.GemRemoveEvent;
import org. bukkit.Bukkit;
import java.util.*;

public class GemManager {
    
    private ItemCore plugin;
    private ConfigManager configManager;
    private DataManager dataManager;
    private Map<String, Gem> gemCache;
    
    public GemManager(ItemCore plugin, ConfigManager configManager, DataManager dataManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.dataManager = dataManager;
        this. gemCache = new HashMap<>();
    }
    
    /**
     * 보석 정보 로드
     */
    public Gem loadGem(String gemId) throws Exception {
        if (gemCache. containsKey(gemId)) {
            return gemCache.get(gemId);
        }
        
        Gem gem = dataManager.loadGem(gemId);
        if (gem != null) {
            gemCache.put(gemId, gem);
        }
        
        return gem;
    }
    
    /**
     * 보석 장착
     */
    public boolean insertGem(CustomItem item, Gem gem, int socketIndex) {
        // 소켓 개수 확인
        if (socketIndex < 0 || socketIndex >= item.getSockets()) {
            throw new IllegalArgumentException("잘못된 소켓 인덱스: " + socketIndex);
        }
        
        List<Gem> gems = item.getGems();
        if (gems == null) {
            gems = new ArrayList<>();
            item.setGems(gems);
        }
        
        // 소켓이 이미 차 있는지 확인
        if (socketIndex < gems.size() && gems.get(socketIndex) != null) {
            throw new RuntimeException("이 소켓에는 이미 보석이 장착되어 있습니다!");
        }
        
        // 소켓 색상 확인
        if (! canInsertGem(item, gem, socketIndex)) {
            return false;
        }
        
        // 리스트 크기 조정
        while (gems.size() <= socketIndex) {
            gems.add(null);
        }
        
        // 보석 장착
        gems.set(socketIndex, gem);
        
        // 이벤트 발생
        GemInsertEvent event = new GemInsertEvent(item, gem, socketIndex);
        Bukkit.getPluginManager().callEvent(event);
        
        return true;
    }
    
    /**
     * 보석 제거
     */
    public boolean removeGem(CustomItem item, int socketIndex) {
        List<Gem> gems = item. getGems();
        
        if (gems == null || socketIndex < 0 || socketIndex >= gems.size()) {
            return false;
        }
        
        Gem removedGem = gems.get(socketIndex);
        if (removedGem == null) {
            return false;
        }
        
        gems.set(socketIndex, null);
        
        // 이벤트 발생
        GemRemoveEvent event = new GemRemoveEvent(item, removedGem, socketIndex);
        Bukkit.getPluginManager().callEvent(event);
        
        return true;
    }
    
    /**
     * 보석 장착 가능 여부 확인
     */
    public boolean canInsertGem(CustomItem item, Gem gem, int socketIndex) {
        SocketColor gemColor = gem.getColor();
        
        // 소켓 색상 리스트 (아이템에 따라 다름)
        // 현재는 모든 소켓이 모든 색상 보석을 받을 수 있다고 가정
        
        return true;
    }
    
    /**
     * 보석의 스탯 합계 계산
     */
    public Map<String, Double> calculateGemStats(CustomItem item) {
        Map<String, Double> stats = new HashMap<>();
        
        List<Gem> gems = item.getGems();
        if (gems == null) {
            return stats;
        }
        
        for (Gem gem : gems) {
            if (gem != null) {
                Map<String, Double> gemStats = gem.getStats();
                if (gemStats != null) {
                    for (String statName : gemStats.keySet()) {
                        double value = gemStats.get(statName);
                        stats.put(statName, stats.getOrDefault(statName, 0.0) + value);
                    }
                }
            }
        }
        
        return stats;
    }
    
    /**
     * 보석 분해
     */
    public Gem removeAndBreakGem(CustomItem item, int socketIndex) {
        List<Gem> gems = item. getGems();
        
        if (gems == null || socketIndex < 0 || socketIndex >= gems.size()) {
            return null;
        }
        
        Gem gem = gems.get(socketIndex);
        if (gem == null) {
            return null;
        }
        
        gems.set(socketIndex, null);
        
        return gem;
    }
    
    /**
     * 모든 보석 제거
     */
    public List<Gem> removeAllGems(CustomItem item) {
        List<Gem> removedGems = new ArrayList<>();
        List<Gem> gems = item. getGems();
        
        if (gems == null) {
            return removedGems;
        }
        
        for (int i = 0; i < gems.size(); i++) {
            if (gems.get(i) != null) {
                removedGems.add(gems. get(i));
                gems.set(i, null);
            }
        }
        
        return removedGems;
    }
    
    /**
     * 캐시 초기화
     */
    public void clearCache() {
        gemCache.clear();
    }
    
    /**
     * 보석 목록 반환
     */
    public Collection<Gem> getCachedGems() {
        return gemCache.values();
    }
}