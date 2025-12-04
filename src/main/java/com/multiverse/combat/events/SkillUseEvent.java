package com.multiverse.combat. events;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org. bukkit.event.Event;
import org.bukkit. event.HandlerList;
import com.multiverse.combat.models. Skill;

/**
 * 스킬 사용 이벤트
 * 플레이어가 스킬을 사용할 때 발생합니다. 
 */
public class SkillUseEvent extends Event implements org.bukkit.event.Cancellable {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private Player player;
    private Skill skill;
    private LivingEntity target;
    private boolean cancelled = false;
    
    /**
     * SkillUseEvent 생성자
     * @param player 스킬을 사용한 플레이어
     * @param skill 사용한 스킬
     * @param target 스킬의 대상
     */
    public SkillUseEvent(Player player, Skill skill, LivingEntity target) {
        this. player = player;
        this. skill = skill;
        this. target = target;
    }
    
    /**
     * 스킬을 사용한 플레이어 반환
     * @return 플레이어
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * 사용한 스킬 반환
     * @return 스킬 객체
     */
    public Skill getSkill() {
        return skill;
    }
    
    /**
     * 스킬의 대상 엔티티 반환
     * @return 대상 엔티티
     */
    public LivingEntity getTarget() {
        return target;
    }
    
    /**
     * 스킬의 대상 설정
     * @param target 새로운 대상
     */
    public void setTarget(LivingEntity target) {
        this.target = target;
    }
    
    /**
     * 이벤트 취소 여부 조회
     * @return 취소되었으면 true
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    /**
     * 이벤트 취소 설정
     * @param cancel 취소 여부
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
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