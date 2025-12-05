package com.multiverse.item.  events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import com.multiverse.item.data.CustomItem;
import com.multiverse.item. data. Gem;

public class GemInsertEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private Player player;
    private CustomItem item;
    private Gem gem;
    private int socketIndex;
    private int cost;
    private int materialCost;
    private boolean success;
    private boolean cancelled;
    
    /**
     * 기본 생성자
     */
    public GemInsertEvent(Player player, CustomItem item, Gem gem, int socketIndex, 
                         int cost, int materialCost) {
        this. player = player;
        this. item = item;
        this. gem = gem;
        this. socketIndex = socketIndex;
        this.cost = cost;
        this.materialCost = materialCost;
        this.success = false;
        this.cancelled = false;
    }
    
    // Getters and Setters
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public CustomItem getItem() {
        return item;
    }
    
    public void setItem(CustomItem item) {
        this.item = item;
    }
    
    public Gem getGem() {
        return gem;
    }
    
    public void setGem(Gem gem) {
        this.gem = gem;
    }
    
    public int getSocketIndex() {
        return socketIndex;
    }
    
    public void setSocketIndex(int socketIndex) {
        this.socketIndex = Math.max(0, socketIndex);
    }
    
    public int getCost() {
        return cost;
    }
    
    public void setCost(int cost) {
        this.cost = Math.max(0, cost);
    }
    
    public int getMaterialCost() {
        return materialCost;
    }
    
    public void setMaterialCost(int materialCost) {
        this.materialCost = Math.max(0, materialCost);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    /**
     * 전체 비용
     */
    public int getTotalCost() {
        return cost + materialCost;
    }
    
    /**
     * 보석이 유효한지 확인
     */
    public boolean isValidGem() {
        return gem != null && gem.getName() != null;
    }
    
    /**
     * 소켓 인덱스가 유효한지 확인
     */
    public boolean isValidSocket() {
        return socketIndex >= 0 && socketIndex < 3; // 최대 3개 소켓
    }
    
    /**
     * 이벤트 취소
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    /**
     * 이벤트 취소 설정
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    /**
     * 이벤트 핸들러 리스트 반환
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    /**
     * 핸들러 리스트 반환 (정적)
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    /**
     * 이벤트 정보 출력
     */
    @Override
    public String toString() {
        return "GemInsertEvent{" +
                "player=" + player.getName() +
                ", item=" + item.getName() +
                ", gem=" + (gem != null ? gem.getName() : "null") +
                ", socketIndex=" + socketIndex +
                ", totalCost=" + getTotalCost() +
                ", success=" + success +
                "}";
    }
}