package com.multiverse.item.storage;

import com.multiverse.item.data.CustomItem;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemStorage {
    
    private Map<String, CustomItem> items;
    private YAMLStorage storage;
    
    /**
     * 기본 생성자
     */
    public ItemStorage(YAMLStorage storage) {
        this.storage = storage;
        this.items = new HashMap<>();
        loadAllItems();
    }
    
    /**
     * 모든 아이템 로드
     */
    private void loadAllItems() {
        Map<String, Object> itemData = storage.getSection("items");
        for (String itemId : itemData.keySet()) {
            // 나중에 ItemManager와 연동하여 CustomItem으로 변환
        }
    }
    
    /**
     * 아이템 저장
     */
    public void saveItem(String itemId, CustomItem item) {
        items.put(itemId, item);
        storage.set("items." + itemId + ".name", item.getName());
        storage.set("items." + itemId + ".type", item. getType(). toString());
        storage.set("items." + itemId + ".rarity", item.getRarity(). toString());
        storage.set("items." + itemId + ".enhance-level", item.getEnhanceLevel());
        storage.set("items." + itemId + ".durability", item.getDurability());
    }
    
    /**
     * 아이템 로드
     */
    public CustomItem loadItem(String itemId) {
        if (! storage.contains("items." + itemId)) {
            return null;
        }
        
        // 나중에 ItemManager와 연동하여 CustomItem으로 변환
        return items.get(itemId);
    }
    
    /**
     * 아이템 삭제
     */
    public void deleteItem(String itemId) {
        items.remove(itemId);
        storage.remove("items." + itemId);
    }
    
    /**
     * 모든 아이템 조회
     */
    public Map<String, CustomItem> getAllItems() {
        return new HashMap<>(items);
    }
    
    /**
     * 아이템 존재 여부 확인
     */
    public boolean existsItem(String itemId) {
        return storage.contains("items." + itemId);
    }
    
    /**
     * 전체 아이템 개수
     */
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * 저장소 초기화
     */
    public void clear() {
        items. clear();
        storage.remove("items");
    }
}