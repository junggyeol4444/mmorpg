package com. multiverse.item.storage;

import com.multiverse.item. data.ItemSet;
import java.util.HashMap;
import java.util.Map;

public class SetStorage {
    
    private Map<String, ItemSet> sets;
    private YAMLStorage storage;
    
    /**
     * 기본 생성자
     */
    public SetStorage(YAMLStorage storage) {
        this.storage = storage;
        this.sets = new HashMap<>();
        loadAllSets();
    }
    
    /**
     * 모든 세트 로드
     */
    private void loadAllSets() {
        if (!storage.contains("sets")) {
            return;
        }
        
        Map<String, Object> setData = storage.getSection("sets");
        for (String setId : setData.keySet()) {
            // 나중에 SetManager와 연동하여 ItemSet으로 변환
        }
    }
    
    /**
     * 세트 저장
     */
    public void saveSet(String setId, ItemSet itemSet) {
        sets.put(setId, itemSet);
        
        String path = "sets." + setId;
        storage.set(path + ".name", itemSet.getName());
        storage.set(path + ".description", itemSet.getDescription());
        storage.set(path + ". items-count", itemSet.getItems() != null ? itemSet.getItems().size() : 0);
        storage.set(path + ".bonuses-count", itemSet.getBonuses() != null ? itemSet.getBonuses().size() : 0);
    }
    
    /**
     * 세트 로드
     */
    public ItemSet loadSet(String setId) {
        if (!storage. contains("sets." + setId)) {
            return null;
        }
        
        return sets. get(setId);
    }
    
    /**
     * 세트 삭제
     */
    public void deleteSet(String setId) {
        sets.remove(setId);
        storage.remove("sets." + setId);
    }
    
    /**
     * 모든 세트 조회
     */
    public Map<String, ItemSet> getAllSets() {
        return new HashMap<>(sets);
    }
    
    /**
     * 세트 존재 여부 확인
     */
    public boolean existsSet(String setId) {
        return storage.contains("sets." + setId);
    }
    
    /**
     * 세트 개수
     */
    public int getSetCount() {
        return sets. size();
    }
    
    /**
     * 플레이어가 착용한 세트 조회
     */
    public ItemSet getPlayerEquippedSet(String setId) {
        return loadSet(setId);
    }
    
    /**
     * 세트 보너스 정보 저장
     */
    public void saveBonusInfo(String setId, int itemCount, Map<String, Object> bonus) {
        String path = "sets." + setId + ".bonus." + itemCount;
        storage. set(path, bonus);
    }
    
    /**
     * 저장소 초기화
     */
    public void clear() {
        sets.clear();
        storage.remove("sets");
    }
    
    /**
     * 세트 다시 로드
     */
    public void reload() {
        clear();
        loadAllSets();
    }
}