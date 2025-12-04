package com.multiverse.combat.events;

import org.bukkit.entity.Player;
import org.bukkit. event.Event;
import org. bukkit.event.HandlerList;

/**
 * 콤보 마일스톤 이벤트
 * 플레이어가 콤보 목표치에 도달했을 때 발생합니다.
 */
public class ComboMilestoneEvent extends Event {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private Player player;
    private int combo;
    private double bonus;
    
    /**
     * ComboMilestoneEvent 생성자
     * @param player 플레이어
     * @param combo 도달한 콤보 수
     * @param bonus 콤보 보너스 배수
     */
    public ComboMilestoneEvent(Player player, int combo, double bonus) {
        this.player = player;
        this.combo = combo;
        this.bonus = bonus;
    }
    
    /**
     * 플레이어 반환
     * @return 플레이어
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * 도달한 콤보 수 반환
     * @return 콤보 수
     */
    public int getCombo() {
        return combo;
    }
    
    /**
     * 콤보 보너스 배수 반환
     * @return 보너스 배수
     */
    public double getBonus() {
        return bonus;
    }
    
    /**
     * 핸들러 리스트 반환
     * @return 핸들러 리스트
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    /**
     * 정적 핸들러 리스트 반환
     * @return 핸들러 리스트
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}