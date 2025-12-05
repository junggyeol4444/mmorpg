package com.  multiverse.item.  events;

import org.bukkit.event.  Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.  Player;
import com. multiverse.item. data.CustomItem;
import com.multiverse.item. data. ItemOption;
import com.multiverse.item. data.  OptionTrigger;

public class OptionTriggerEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private Player player;
    private CustomItem item;
    private ItemOption option;
    private OptionTrigger trigger;
    private double triggerChance;
    private boolean triggered;
    private boolean cancelled;
    
    /**
     * 기본 생성자
     */
    public OptionTriggerEvent(Player player, CustomItem item, ItemOption option, 
                             OptionTrigger trigger, double triggerChance) {
        this. player = player;
        this. item = item;
        this. option = option;
        this. trigger = trigger;
        this. triggerChance = triggerChance;
        this. triggered = false;
        this. cancelled = false;
    }
    
    // Getters and Setters
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this. player = player;
    }
    
    public CustomItem getItem() {
        return item;
    }
    
    public void setItem(CustomItem item) {
        this.  item = item;
    }
    
    public ItemOption getOption() {
        return option;
    }
    
    public void setOption(ItemOption option) {
        this. option = option;
    }
    
    public OptionTrigger getTrigger() {
        return trigger;
    }
    
    public void setTrigger(OptionTrigger trigger) {
        this. trigger = trigger;
    }
    
    public double getTriggerChance() {
        return triggerChance;
    }
    
    public void setTriggerChance(double triggerChance) {
        this. triggerChance = Math.max(0, Math.min(triggerChance, 100. 0));
    }
    
    public boolean isTriggered() {
        return triggered;
    }
    
    public void setTriggered(boolean triggered) {
        this.  triggered = triggered;
    }
    
    /**
     * 트리거 확률에 따른 실제 발동 여부
     */
    public boolean shouldTriggerByChance() {
        return Math.random() * 100 < triggerChance;
    }
    
    /**
     * 옵션이 유효한지 확인
     */
    public boolean isValidOption() {
        return option != null && option.getName() != null;
    }
    
    /**
     * 트리거 타입이 매칭되는지 확인
     */
    public boolean isTriggerMatching(OptionTrigger checkTrigger) {
        return trigger == checkTrigger;
    }
    
    /**
     * 옵션 효과값
     */
    public double getOptionValue() {
        return option != null ? option.  getValue() : 0;
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
        this. cancelled = cancelled;
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
        return "OptionTriggerEvent{" +
                "player=" + player.  getName() +
                ", item=" + (item != null ? item. getName() : "null") +
                ", option=" + (option != null ? option.getName() : "null") +
                ", trigger=" + trigger +
                ", triggerChance=" + String.  format("%.1f%%", triggerChance) +
                ", triggered=" + triggered +
                "}";
    }
}