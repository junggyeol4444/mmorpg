package com.  multiverse.pet.  api.  event;

import com.multiverse.pet.model. Pet;
import org.bukkit.  entity.Entity;
import org. bukkit.entity. LivingEntity;
import org.  bukkit.entity.   Player;
import org.bukkit.event.Cancellable;
import org. bukkit.event. Event;
import org.bukkit.  event.HandlerList;
import org.bukkit.event.  entity.EntityDamageEvent;

/**
 * 펫 사망/기절 이벤트
 * 펫이 사망하거나 기절할 때 발생
 */
public class PetDeathEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player owner;
    private final Pet pet;
    private final EntityDamageEvent. DamageCause damageCause;
    private Entity killer;
    private double finalDamage;
    private DeathType deathType;
    private boolean cancelled;
    private boolean dropItems;
    private boolean loseExp;
    private double expLossPercent;

    /**
     * 사망 타입
     */
    public enum DeathType {
        /** 기절 (부활 가능) */
        FAINT,
        /** 영구 사망 */
        PERMANENT,
        /** 굶주림 */
        STARVATION,
        /** 보이드 */
        VOID,
        /** 전투 */
        COMBAT,
        /** 환경 (용암, 익사 등) */
        ENVIRONMENT
    }

    /**
     * 생성자
     */
    public PetDeathEvent(Player owner, Pet pet, EntityDamageEvent lastDamage) {
        this. owner = owner;
        this.pet = pet;

        if (lastDamage != null) {
            this. damageCause = lastDamage. getCause();
            this.finalDamage = lastDamage.getFinalDamage();
        } else {
            this.  damageCause = EntityDamageEvent. DamageCause.CUSTOM;
            this. finalDamage = 0;
        }

        this. deathType = determineDeathType(damageCause);
        this.cancelled = false;
        this.dropItems = false;
        this.loseExp = true;
        this. expLossPercent = 10.0;
    }

    /**
     * 사망 타입 결정
     */
    private DeathType determineDeathType(EntityDamageEvent. DamageCause cause) {
        if (cause == null) {
            return DeathType. FAINT;
        }

        switch (cause) {
            case VOID:
                return DeathType.VOID;
            case STARVATION:
                return DeathType.  STARVATION;
            case LAVA:
            case DROWNING:
            case FIRE:
            case FIRE_TICK: 
            case SUFFOCATION:
            case FALL:
                return DeathType.  ENVIRONMENT;
            case ENTITY_ATTACK:
            case ENTITY_SWEEP_ATTACK:
            case PROJECTILE: 
            case MAGIC: 
                return DeathType. COMBAT;
            default:
                return DeathType. FAINT;
        }
    }

    /**
     * 주인 가져오기
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * 펫 가져오기
     */
    public Pet getPet() {
        return pet;
    }

    /**
     * 데미지 원인 가져오기
     */
    public EntityDamageEvent.DamageCause getDamageCause() {
        return damageCause;
    }

    /**
     * 킬러 가져오기
     */
    public Entity getKiller() {
        return killer;
    }

    /**
     * 킬러 설정
     */
    public void setKiller(Entity killer) {
        this.killer = killer;
    }

    /**
     * 킬러가 플레이어인지
     */
    public boolean isKilledByPlayer() {
        return killer instanceof Player;
    }

    /**
     * 킬러가 몬스터인지
     */
    public boolean isKilledByMob() {
        return killer instanceof LivingEntity && !(killer instanceof Player);
    }

    /**
     * 최종 데미지 가져오기
     */
    public double getFinalDamage() {
        return finalDamage;
    }

    /**
     * 최종 데미지 설정
     */
    public void setFinalDamage(double finalDamage) {
        this.finalDamage = finalDamage;
    }

    /**
     * 사망 타입 가져오기
     */
    public DeathType getDeathType() {
        return deathType;
    }

    /**
     * 사망 타입 설정
     */
    public void setDeathType(DeathType deathType) {
        this.deathType = deathType;
    }

    /**
     * 기절인지 (부활 가능)
     */
    public boolean isFaint() {
        return deathType == DeathType.FAINT;
    }

    /**
     * 영구 사망인지
     */
    public boolean isPermanentDeath() {
        return deathType == DeathType.  PERMANENT;
    }

    /**
     * 아이템 드롭 여부
     */
    public boolean isDropItems() {
        return dropItems;
    }

    /**
     * 아이템 드롭 설정
     */
    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    /**
     * 경험치 손실 여부
     */
    public boolean isLoseExp() {
        return loseExp;
    }

    /**
     * 경험치 손실 설정
     */
    public void setLoseExp(boolean loseExp) {
        this. loseExp = loseExp;
    }

    /**
     * 경험치 손실률 가져오기
     */
    public double getExpLossPercent() {
        return expLossPercent;
    }

    /**
     * 경험치 손실률 설정
     */
    public void setExpLossPercent(double expLossPercent) {
        this.expLossPercent = Math.max(0, Math.min(100, expLossPercent));
    }

    /**
     * 취소 가능 여부
     */
    public boolean isCancellable() {
        // 보이드는 취소 불가
        return deathType != DeathType.VOID;
    }

    /**
     * 취소 여부
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * 취소 설정
     */
    @Override
    public void setCancelled(boolean cancelled) {
        if (!isCancellable() && cancelled) {
            return;
        }
        this.cancelled = cancelled;
    }

    /**
     * 핸들러 리스트
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * 핸들러 리스트 (static)
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}