package com. multiverse.item. events;

import org.bukkit.event. Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity. Player;
import com.multiverse.item.data.ItemSet;
import java.util.List;

public class SetBonusActivateEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private Player player;
    private ItemSet itemSet;
    private List<?> activatedBonuses; // 활성화된 보너스 리스트
    private int bonusCount; // 몇 개의 세트 아이템을 착용했는지
    private boolean cancelled;
    
    /**
     * 기본 생성자
     */
    public SetBonusActivateEvent(Player player, ItemSet itemSet, List<? > activatedBonuses, int bonusCount) {
        this.player = player;
        this.itemSet = itemSet;
        this.  activatedBonuses = activatedBonuses;
        this. bonusCount = bonusCount;
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
    
    public List<? > getActivatedBonuses() {
        return activatedBonuses;
    }
    
    public void setActivatedBonuses(List<?> activatedBonuses) {
        this.  activatedBonuses = activatedBonuses;
    }
    
    public int getBonusCount() {
        return bonusCount;
    }
    
    public void setBonusCount(int bonusCount) {
        this.bonusCount = Math. max(0, bonusCount);
    }
    
    /**
     * 활성화된 보너스 개수
     */
    public int getActivatedBonusCount() {
        return activatedBonuses != null ? activatedBonuses.size() : 0;
    }
    
    /**
     * 세트 완성도 (%)
     */
    public double getSetCompletion() {
        if (itemSet == null || itemSet.  getItems() == null) {
            return 0;
        }
        return (bonusCount / (double) itemSet.  getItems().size()) * 100. 0;
    }
    
    /**
     * 세트가 완성되었는지 확인
     */
    public boolean isSetComplete() {
        return bonusCount == (itemSet != null && itemSet.getItems() != null ?  itemSet.getItems().size() : 0);
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
        return "SetBonusActivateEvent{" +
                "player=" + player.getName() +
                ", itemSet=" + (itemSet != null ? itemSet. getName() : "null") +
                ", bonusCount=" + bonusCount +
                ", activatedBonuses=" + getActivatedBonusCount() +
                ", completion=" + String.format("%.1f%%", getSetCompletion()) +
                ", complete=" + isSetComplete() +
                "}";
    }
}