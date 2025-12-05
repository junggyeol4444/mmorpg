package com. multiverse.item. events;

import org.bukkit.event. Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity. Player;
import com.multiverse.item.data.ItemSet;
import java.util.List;

public class SetBonusDeactivateEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private Player player;
    private ItemSet itemSet;
    private List<?> deactivatedBonuses; // 비활성화된 보너스 리스트
    private int remainingBonusCount; // 남은 세트 아이템 개수
    private boolean cancelled;
    
    /**
     * 기본 생성자
     */
    public SetBonusDeactivateEvent(Player player, ItemSet itemSet, List<?> deactivatedBonuses, int remainingBonusCount) {
        this.player = player;
        this.itemSet = itemSet;
        this. deactivatedBonuses = deactivatedBonuses;
        this.remainingBonusCount = remainingBonusCount;
        this.cancelled = false;
    }
    
    // Getters and Setters
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this. player = player;
    }
    
    public ItemSet getItemSet() {
        return itemSet;
    }
    
    public void setItemSet(ItemSet itemSet) {
        this. itemSet = itemSet;
    }
    
    public List<? > getDeactivatedBonuses() {
        return deactivatedBonuses;
    }
    
    public void setDeactivatedBonuses(List<?> deactivatedBonuses) {
        this. deactivatedBonuses = deactivatedBonuses;
    }
    
    public int getRemainingBonusCount() {
        return remainingBonusCount;
    }
    
    public void setRemainingBonusCount(int remainingBonusCount) {
        this.remainingBonusCount = Math.max(0, remainingBonusCount);
    }
    
    /**
     * 비활성화된 보너스 개수
     */
    public int getDeactivatedBonusCount() {
        return deactivatedBonuses != null ? deactivatedBonuses.size() : 0;
    }
    
    /**
     * 세트 완성도 (%)
     */
    public double getSetCompletion() {
        if (itemSet == null || itemSet. getItems() == null) {
            return 0;
        }
        return (remainingBonusCount / (double) itemSet.getItems().size()) * 100.  0;
    }
    
    /**
     * 세트가 여전히 활성화되어 있는지 확인
     */
    public boolean isStillActive() {
        return remainingBonusCount > 0;
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
        return "SetBonusDeactivateEvent{" +
                "player=" + player.getName() +
                ", itemSet=" + (itemSet != null ? itemSet.getName() : "null") +
                ", remainingBonusCount=" + remainingBonusCount +
                ", deactivatedBonuses=" + getDeactivatedBonusCount() +
                ", completion=" + String.format("%.1f%%", getSetCompletion()) +
                ", stillActive=" + isStillActive() +
                "}";
    }
}