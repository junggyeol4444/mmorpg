package com.multiverse.combat.events;

import org.bukkit.entity.LivingEntity;
import org. bukkit.event.Event;
import org.bukkit.event. HandlerList;
import com. multiverse.combat.models.StatusEffect;
import com.multiverse.combat. models.enums.StatusEffectType;

/**
 * 상태이상 적용 이벤트
 * 엔티티에 상태이상이 적용될 때 발생합니다. 
 */
public class StatusEffectApplyEvent extends Event implements org.bukkit.event. Cancellable {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private LivingEntity target;
    private StatusEffect effect;
    private boolean cancelled = false;
    
    /**
     * StatusEffectApplyEvent 생성자
     * @param target 대상 엔티티
     * @param effect 적용할 상태이상
     */
    public StatusEffectApplyEvent(LivingEntity target, StatusEffect effect) {
        this.target = target;
        this.effect = effect;
    }
    
    /**
     * 대상 엔티티 반환
     * @return 대상
     */
    public LivingEntity getTarget() {
        return target;
    }
    
    /**
     * 상태이상 반환
     * @return 상태이상 객체
     */
    public StatusEffect getEffect() {
        return effect;
    }
    
    /**
     * 상태이상 타입 반환
     * @return 상태이상 타입
     */
    public StatusEffectType getEffectType() {
        return effect.getType();
    }
    
    /**
     * 상태이상 레벨 반환
     * @return 레벨
     */
    public int getLevel() {
        return effect.getLevel();
    }
    
    /**
     * 상태이상 지속 시간 반환
     * @return 지속 시간 (밀리초)
     */
    public long getDuration() {
        return effect. getDuration();
    }
    
    /**
     * 지속 시간 설정
     * @param duration 새로운 지속 시간
     */
    public void setDuration(long duration) {
        effect.setDuration(duration);
    }
    
    /**
     * 레벨 설정
     * @param level 새로운 레벨
     */
    public void setLevel(int level) {
        effect.setLevel(level);
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
        this. cancelled = cancel;
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