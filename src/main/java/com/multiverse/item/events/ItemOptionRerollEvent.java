package com.multiverse.item.  events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import com.multiverse.item.data.CustomItem;
import java.util.List;

public class ItemOptionRerollEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private Player player;
    private CustomItem item;
    private List<? > oldOptions;
    private List<?> newOptions;
    private int cost;
    private int rerollCount;
    private boolean cancelled;
    
    /**
     * 기본 생성자
     */
    public ItemOptionRerollEvent(Player player, CustomItem item, List<?> oldOptions, 
                                List<?> newOptions, int cost, int rerollCount) {
        this.player = player;
        this.item = item;
        this.oldOptions = oldOptions;
        this.newOptions = newOptions;
        this.cost = cost;
        this.rerollCount = rerollCount;
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
    
    public List<?> getOldOptions() {
        return oldOptions;
    }
    
    public void setOldOptions(List<?> oldOptions) {
        this.oldOptions = oldOptions;
    }
    
    public List<?> getNewOptions() {
        return newOptions;
    }
    
    public void setNewOptions(List<?> newOptions) {
        this.newOptions = newOptions;
    }
    
    public int getCost() {
        return cost;
    }
    
    public void setCost(int cost) {
        this.cost = Math.max(0, cost);
    }
    
    public int getRerollCount() {
        return rerollCount;
    }
    
    public void setRerollCount(int rerollCount) {
        this.rerollCount = Math.max(0, rerollCount);
    }
    
    /**
     * 옵션 개수 변화
     */
    public int getOptionCountChange() {
        return (newOptions != null ?  newOptions.size() : 0) - 
               (oldOptions != null ?  oldOptions.size() : 0);
    }
    
    /**
     * 리롤 비용 (횟수 기반)
     */
    public int calculateRerollCost() {
        return cost * rerollCount;
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
        return "ItemOptionRerollEvent{" +
                "player=" + player.getName() +
                ", item=" + item.getName() +
                ", oldOptions=" + (oldOptions != null ? oldOptions. size() : 0) +
                ", newOptions=" + (newOptions != null ? newOptions.size() : 0) +
                ", cost=" + cost +
                ", rerollCount=" + rerollCount +
                "}";
    }
}