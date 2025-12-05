package com.multiverse.item. events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import com.multiverse.item.data.CustomItem;
import java.util.List;
import java.util.Map;

public class ItemDisassembleEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private Player player;
    private CustomItem item;
    private Map<String, Integer> rewards; // 재료 보상
    private int goldReward;
    private int cost;
    private boolean cancelled;
    
    /**
     * 기본 생성자
     */
    public ItemDisassembleEvent(Player player, CustomItem item, Map<String, Integer> rewards, 
                               int goldReward, int cost) {
        this. player = player;
        this. item = item;
        this. rewards = rewards;
        this. goldReward = goldReward;
        this.cost = cost;
        this.cancelled = false;
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
        this.item = item;
    }
    
    public Map<String, Integer> getRewards() {
        return rewards;
    }
    
    public void setRewards(Map<String, Integer> rewards) {
        this.rewards = rewards;
    }
    
    public int getGoldReward() {
        return goldReward;
    }
    
    public void setGoldReward(int goldReward) {
        this.goldReward = Math.max(0, goldReward);
    }
    
    public int getCost() {
        return cost;
    }
    
    public void setCost(int cost) {
        this.cost = Math.max(0, cost);
    }
    
    /**
     * 총 재료 개수
     */
    public int getTotalRewardCount() {
        if (rewards == null) {
            return 0;
        }
        return rewards.values(). stream().mapToInt(Integer::intValue).sum();
    }
    
    /**
     * 특정 재료의 개수
     */
    public int getRewardCount(String materialName) {
        if (rewards == null) {
            return 0;
        }
        return rewards.getOrDefault(materialName, 0);
    }
    
    /**
     * 순 이익 (보상 - 비용)
     */
    public int getNetProfit() {
        return goldReward - cost;
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
        return "ItemDisassembleEvent{" +
                "player=" + player.getName() +
                ", item=" + item. getName() +
                ", rewardCount=" + getTotalRewardCount() +
                ", goldReward=" + goldReward +
                ", cost=" + cost +
                ", netProfit=" + getNetProfit() +
                "}";
    }
}