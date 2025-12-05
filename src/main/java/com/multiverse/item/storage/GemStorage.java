package com. multiverse.item.storage;

import com.multiverse.item. data. Gem;
import java.util. HashMap;
import java.util. Map;

public class GemStorage {
    
    private Map<String, Gem> gems;
    private YAMLStorage storage;
    
    /**
     * 기본 생성자
     */
    public GemStorage(YAMLStorage storage) {
        this.storage = storage;
        this.gems = new HashMap<>();
        loadAllGems();
    }
    
    /**
     * 모든 보석 로드
     */
    private void loadAllGems() {
        if (!storage.contains("gems")) {
            return;
        }
        
        Map<String, Object> gemData = storage.getSection("gems");
        for (String gemId : gemData.keySet()) {
            // 나중에 GemManager와 연동하여 Gem으로 변환
        }
    }
    
    /**
     * 보석 저장
     */
    public void saveGem(String gemId, Gem gem) {
        gems.put(gemId, gem);
        
        String path = "gems." + gemId;
        storage.set(path + ". name", gem.getName());
        storage.set(path + ".type", gem.getType());
        storage.set(path + ".rarity", gem.getRarity());
        storage.set(path + ".effect", gem.getEffect());
        storage.set(path + ".color", gem.getColor());
    }
    
    /**
     * 보석 로드
     */
    public Gem loadGem(String gemId) {
        if (!storage.contains("gems." + gemId)) {
            return null;
        }
        
        return gems.get(gemId);
    }
    
    /**
     * 보석 삭제
     */
    public void deleteGem(String gemId) {
        gems.remove(gemId);
        storage.remove("gems." + gemId);
    }
    
    /**
     * 모든 보석 조회
     */
    public Map<String, Gem> getAllGems() {
        return new HashMap<>(gems);
    }
    
    /**
     * 보석 존재 여부 확인
     */
    public boolean existsGem(String gemId) {
        return storage.contains("gems." + gemId);
    }
    
    /**
     * 보석 개수
     */
    public int getGemCount() {
        return gems.size();
    }
    
    /**
     * 희귀도별 보석 조회
     */
    public Map<String, Gem> getGemsByRarity(String rarity) {
        Map<String, Gem> result = new HashMap<>();
        for (Map.Entry<String, Gem> entry : gems.entrySet()) {
            if (entry.getValue().getRarity(). equalsIgnoreCase(rarity)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    /**
     * 색상별 보석 조회
     */
    public Map<String, Gem> getGemsByColor(String color) {
        Map<String, Gem> result = new HashMap<>();
        for (Map.Entry<String, Gem> entry : gems.entrySet()) {
            if (entry.getValue().getColor().equalsIgnoreCase(color)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    /**
     * 저장소 초기화
     */
    public void clear() {
        gems.clear();
        storage. remove("gems");
    }
    
    /**
     * 보석 다시 로드
     */
    public void reload() {
        clear();
        loadAllGems();
    }
}