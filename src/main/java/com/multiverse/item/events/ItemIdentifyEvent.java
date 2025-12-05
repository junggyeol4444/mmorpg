package com.multiverse.item.  events;

import org.bukkit.event.Event;
import org.bukkit. event.HandlerList;
import org.bukkit.entity.Player;
import com.multiverse.item.data.CustomItem;
import java.util.List;

public class ItemIdentifyEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private Player player;
    private CustomItem item;
    private List<? > revealedOptions; // 공개된 옵션
    private int cost;
    private boolean success;
    private boolean cancelled;
    
    /**
     * 기본 생성자
     */
    public ItemIdentifyEvent(Player player, CustomItem item, List<?> revealedOptions, int cost) {
        this. player = player;
        this. item = item;
        this.  revealedOptions = revealedOptions;
        this.cost = cost;
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
    
    public List<?> getRevealedOptions() {
        return revealedOptions;
    }
    
    public void setRevealedOptions(List<?> revealedOptions) {
        this.  revealedOptions = revealedOptions;
    }
    
    public int getCost() {
        return cost;
    }
    
    public void setCost(int cost) {
        this. cost = Math.max(0, cost);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    /**
     * 공개된 옵션 개수
     */
    public int getRevealedOptionCount() {
        return revealedOptions != null ? revealedOptions.size() : 0;
    }
    
    /**
     * 아이템이 이미 식별되었는지 확인
     */
    public boolean isAlreadyIdentified() {
        return item. isIdentified();
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
        return "ItemIdentifyEvent{" +
                "player=" + player.getName() +
                ", item=" + item.getName() +
                ", revealedOptions=" + getRevealedOptionCount() +
                ", cost=" + cost +
                ", success=" + success +
                ", alreadyIdentified=" + isAlreadyIdentified() +
                "}";
    }
}