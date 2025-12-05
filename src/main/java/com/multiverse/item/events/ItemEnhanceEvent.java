package com.multiverse.item. events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import com.multiverse.item.data.CustomItem;
import com.multiverse.item. data.EnhanceResult;

public class ItemEnhanceEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private Player player;
    private CustomItem item;
    private int beforeLevel;
    private int afterLevel;
    private EnhanceResult result;
    private int cost;
    private int materialCost;
    private double successRate;
    private boolean cancelled;
    
    /**
     * 기본 생성자
     */
    public ItemEnhanceEvent(Player player, CustomItem item, int beforeLevel, int afterLevel, 
                           EnhanceResult result, int cost, int materialCost, double successRate) {
        this.player = player;
        this.item = item;
        this.beforeLevel = beforeLevel;
        this.afterLevel = afterLevel;
        this.result = result;
        this.cost = cost;
        this.materialCost = materialCost;
        this.successRate = successRate;
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
    
    public int getBeforeLevel() {
        return beforeLevel;
    }
    
    public void setBeforeLevel(int beforeLevel) {
        this.beforeLevel = beforeLevel;
    }
    
    public int getAfterLevel() {
        return afterLevel;
    }
    
    public void setAfterLevel(int afterLevel) {
        this.afterLevel = afterLevel;
    }
    
    public EnhanceResult getResult() {
        return result;
    }
    
    public void setResult(EnhanceResult result) {
        this.result = result;
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
        this. successRate = Math.max(0, Math.min(successRate, 100.0));
    }
    
    /**
     * 강화 레벨 변화
     */
    public int getLevelChange() {
        return afterLevel - beforeLevel;
    }
    
    /**
     * 강화 성공 여부
     */
    public boolean isSuccess() {
        return result == EnhanceResult.SUCCESS;
    }
    
    /**
     * 강화 실패 여부
     */
    public boolean isFailed() {
        return result == EnhanceResult.FAIL || result == EnhanceResult. DOWNGRADE;
    }
    
    /**
     * 다운그레이드 여부
     */
    public boolean isDowngrade() {
        return result == EnhanceResult.DOWNGRADE;
    }
    
    /**
     * 전체 비용
     */
    public int getTotalCost() {
        return cost + materialCost;
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
        return "ItemEnhanceEvent{" +
                "player=" + player. getName() +
                ", item=" + item.getName() +
                ", beforeLevel=" + beforeLevel +
                ", afterLevel=" + afterLevel +
                ", result=" + result +
                ", successRate=" + String.format("%.1f%%", successRate) +
                "}";
    }
}