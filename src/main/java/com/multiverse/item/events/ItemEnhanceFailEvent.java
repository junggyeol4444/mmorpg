package com.multiverse.item.  events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import com.multiverse.item.data. CustomItem;
import com.multiverse.item.  data.EnhanceResult;

public class ItemEnhanceFailEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private Player player;
    private CustomItem item;
    private int currentLevel;
    private EnhanceResult failReason;
    private int cost;
    private int materialCost;
    private double successRate;
    private double penalty; // 패널티 비율
    private boolean downgraded;
    private boolean cancelled;
    
    /**
     * 기본 생성자
     */
    public ItemEnhanceFailEvent(Player player, CustomItem item, int currentLevel,
                              EnhanceResult failReason, int cost, int materialCost, 
                              double successRate, double penalty, boolean downgraded) {
        this.player = player;
        this.item = item;
        this.currentLevel = currentLevel;
        this.failReason = failReason;
        this.cost = cost;
        this.materialCost = materialCost;
        this.successRate = successRate;
        this.penalty = penalty;
        this.downgraded = downgraded;
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
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = Math.max(0, currentLevel);
    }
    
    public EnhanceResult getFailReason() {
        return failReason;
    }
    
    public void setFailReason(EnhanceResult failReason) {
        this.failReason = failReason;
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
    
    public double getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(double successRate) {
        this.successRate = Math.max(0, Math. min(successRate, 100. 0));
    }
    
    public double getPenalty() {
        return penalty;
    }
    
    public void setPenalty(double penalty) {
        this.penalty = Math.max(0, Math.min(penalty, 100. 0));
    }
    
    public boolean isDowngraded() {
        return downgraded;
    }
    
    public void setDowngraded(boolean downgraded) {
        this. downgraded = downgraded;
    }
    
    /**
     * 전체 비용
     */
    public int getTotalCost() {
        return cost + materialCost;
    }
    
    /**
     * 패널티 비용 계산
     */
    public int calculatePenaltyCost() {
        return (int) (getTotalCost() * (penalty / 100.0));
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
        return "ItemEnhanceFailEvent{" +
                "player=" + player.getName() +
                ", item=" + item.getName() +
                ", currentLevel=" + currentLevel +
                ", failReason=" + failReason +
                ", downgraded=" + downgraded +
                ", penalty=" + String.format("%.1f%%", penalty) +
                "}";
    }
}