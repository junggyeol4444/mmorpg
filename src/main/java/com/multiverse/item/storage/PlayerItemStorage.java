package com. multiverse.item.storage;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory. ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerItemStorage {
    
    private Map<UUID, Inventory> playerInventories;
    private YAMLStorage storage;
    
    /**
     * 기본 생성자
     */
    public PlayerItemStorage(YAMLStorage storage) {
        this.storage = storage;
        this.playerInventories = new HashMap<>();
    }
    
    /**
     * 플레이어 아이템 저장
     */
    public void savePlayerItems(UUID playerId, Inventory inventory) {
        playerInventories.put(playerId, inventory);
        
        String path = "players." + playerId + ".items";
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                storage.set(path + "." + i, item);
            }
        }
    }
    
    /**
     * 플레이어 아이템 로드
     */
    public Inventory loadPlayerItems(UUID playerId) {
        if (!storage.contains("players." + playerId)) {
            return null;
        }
        
        // 나중에 Inventory로 변환하여 반환
        return playerInventories.get(playerId);
    }
    
    /**
     * 플레이어 아이템 삭제
     */
    public void deletePlayerItems(UUID playerId) {
        playerInventories.remove(playerId);
        storage.remove("players." + playerId + ".items");
    }
    
    /**
     * 모든 플레이어 데이터 조회
     */
    public Map<UUID, Inventory> getAllPlayerInventories() {
        return new HashMap<>(playerInventories);
    }
    
    /**
     * 플레이어 데이터 존재 여부 확인
     */
    public boolean existsPlayerData(UUID playerId) {
        return storage.contains("players." + playerId);
    }
    
    /**
     * 플레이어 저장된 골드 조회
     */
    public int getPlayerGold(UUID playerId) {
        return storage.getInt("players." + playerId + ".gold");
    }
    
    /**
     * 플레이어 골드 저장
     */
    public void setPlayerGold(UUID playerId, int gold) {
        storage.set("players." + playerId + ". gold", gold);
    }
    
    /**
     * 저장소 초기화
     */
    public void clear() {
        playerInventories.clear();
        storage.remove("players");
    }
}