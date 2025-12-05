package com. multiverse.item. api;

import com.multiverse.item.ItemCore;
import com.multiverse. item.  data.ItemSet;
import com.multiverse. item.data.CustomItem;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Map;

public class SetAPI {
    
    private static SetAPI instance;
    private ItemCore plugin;
    
    /**
     * 싱글톤 인스턴스 가져오기
     */
    public static SetAPI getInstance() {
        if (instance == null) {
            instance = new SetAPI();
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
     * 세트 생성
     */
    public ItemSet createSet(String setId, String name) {
        ItemSet itemSet = new ItemSet();
        itemSet.setId(setId);
        itemSet.setName(name);
        return itemSet;
    }
    
    /**
     * 세트에 아이템 추가
     */
    public void addItemToSet(ItemSet itemSet, CustomItem item) {
        if (itemSet != null && item != null) {
            itemSet. getItems().add(item);
            item.setSetId(itemSet.getId());
        }
    }
    
    /**
     * 세트에서 아이템 제거
     */
    public void removeItemFromSet(ItemSet itemSet, CustomItem item) {
        if (itemSet != null && item != null) {
            itemSet.getItems().remove(item);
            item.setSetId(null);
        }
    }
    
    /**
     * 세트의 아이템 개수
     */
    public int getSetItemCount(ItemSet itemSet) {
        if (itemSet == null || itemSet.getItems() == null) {
            return 0;
        }
        return itemSet.getItems().size();
    }
    
    /**
     * 플레이어가 착용한 세트 아이템 개수
     */
    public int getPlayerSetItemCount(Player player, String setId) {
        if (player == null || setId == null) {
            return 0;
        }
        
        int count = 0;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            // 아이템 확인 및 세트 ID 비교
        }
        return count;
    }
    
    /**
     * 세트 보너스 계산
     */
    public double getSetBonusIncrease(ItemSet itemSet, int itemCount) {
        if (itemSet == null) {
            return 0;
        }
        
        // 각 아이템마다 10% 보너스
        return itemCount * 0.1;
    }
    
    /**
     * 세트 완성도 계산
     */
    public double getSetCompletion(int equippedCount, int totalCount) {
        if (totalCount == 0) {
            return 0;
        }
        return (equippedCount / (double) totalCount) * 100;
    }
    
    /**
     * 세트가 완성되었는지 확인
     */
    public boolean isSetComplete(int equippedCount, int totalCount) {
        return equippedCount == totalCount && totalCount > 0;
    }
    
    /**
     * 세트 정보 조회
     */
    public String getSetInfo(ItemSet itemSet) {
        if (itemSet == null) {
            return "";
        }
        
        return "§b" + itemSet. getName() + "\n" +
               "§7아이템: " + getSetItemCount(itemSet);
    }
    
    /**
     * 모든 세트 조회
     */
    public Map<String, ItemSet> getAllSets() {
        return plugin.getSetManager().getAllSets();
    }
    
    /**
     * 세트 ID로 조회
     */
    public ItemSet getSetById(String setId) {
        return plugin.getSetManager().getSetById(setId);
    }
    
    /**
     * 세트 유효성 확인
     */
    public boolean isValidSet(ItemSet itemSet) {
        return itemSet != null && itemSet.getId() != null && itemSet.getName() != null;
    }
}