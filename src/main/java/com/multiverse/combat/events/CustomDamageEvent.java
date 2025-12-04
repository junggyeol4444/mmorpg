package com.multiverse. combat.events;

import org. bukkit.entity.LivingEntity;
import org. bukkit.event.Event;
import org.bukkit.event. HandlerList;
import com. multiverse.combat.models.enums.DamageType;
import com.multiverse.combat. models.enums.ElementType;

/**
 * 커스텀 데미지 이벤트
 * 플레이어가 데미지를 입거나 줄 때 발생합니다.
 */
public class CustomDamageEvent extends Event implements org.bukkit.event. Cancellable {
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    private LivingEntity attacker;
    private LivingEntity target;
    private double damage;
    private DamageType type;
    private boolean isCritical = false;
    private ElementType element = ElementType.NEUTRAL;
    private boolean cancelled = false;
    
    /**
     * CustomDamageEvent 생성자
     * @param attacker 공격자
     * @param target 대상
     * @param damage 데미지
     * @param type 데미지 타입
     */
    public CustomDamageEvent(LivingEntity attacker, LivingEntity target, 
                            double damage, DamageType type) {
        this.attacker = attacker;
        this. target = target;
        this. damage = damage;
        this. type = type;
    }
    
    /**
     * 공격자 반환
     * @return 공격자
     */
    public LivingEntity getAttacker() {
        return attacker;
    }
    
    /**
     * 대상 반환
     * @return 대상
     */
    public LivingEntity getTarget() {
        return target;
    }
    
    /**
     * 데미지 반환
     * @return 데미지
     */
    public double getDamage() {
        return damage;
    }
    
    /**
     * 데미지 설정
     * @param damage 새로운 데미지
     */
    public void setDamage(double damage) {
        this.damage = Math.max(damage, 0.1);
    }
    
    /**
     * 데미지 타입 반환
     * @return 데미지 타입
     */
    public DamageType getType() {
        return type;
    }
    
    /**
     * 크리티컬 여부 반환
     * @return 크리티컬이면 true
     */
    public boolean isCritical() {
        return isCritical;
    }
    
    /**
     * 크리티컬 설정
     * @param critical 크리티컬 여부
     */
    public void setCritical(boolean critical) {
        this.isCritical = critical;
    }
    
    /**
     * 속성 반환
     * @return 속성
     */
    public ElementType getElement() {
        return element;
    }
    
    /**
     * 속성 설정
     * @param element 새로운 속성
     */
    public void setElement(ElementType element) {
        this.element = element != null ? element : ElementType. NEUTRAL;
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