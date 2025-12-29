package com.multiverse. pet.api.event;

import com. multiverse.pet. model.Pet;
import com.multiverse.pet.model. skill.PetSkill;
import com.multiverse.pet.model.skill. SkillType;
import org. bukkit.entity. LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit. event. Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 펫 스킬 사용 이벤트
 * 펫이 스킬을 사용할 때 발생
 */
public class PetSkillUseEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Pet pet;
    private final PetSkill skill;
    private LivingEntity target;
    private boolean cancelled;
    private String cancelReason;

    // 스킬 수정 가능한 값들
    private double damageMultiplier;
    private double cooldownMultiplier;
    private boolean bypassCooldown;

    /**
     * 생성자
     */
    public PetSkillUseEvent(Player player, Pet pet, PetSkill skill, LivingEntity target) {
        this. player = player;
        this.pet = pet;
        this.skill = skill;
        this.target = target;
        this.cancelled = false;
        this.damageMultiplier = 1.0;
        this.cooldownMultiplier = 1.0;
        this.bypassCooldown = false;
    }

    /**
     * 플레이어 가져오기
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 펫 가져오기
     */
    public Pet getPet() {
        return pet;
    }

    /**
     * 스킬 가져오기
     */
    public PetSkill getSkill() {
        return skill;
    }

    /**
     * 스킬 ID 가져오기
     */
    public String getSkillId() {
        return skill.getSkillId();
    }

    /**
     * 스킬 이름 가져오기
     */
    public String getSkillName() {
        return skill.getName();
    }

    /**
     * 스킬 타입 가져오기
     */
    public SkillType getSkillType() {
        return skill.getType();
    }

    /**
     * 패시브 스킬 여부
     */
    public boolean isPassive() {
        return skill.isPassive();
    }

    /**
     * 타겟 가져오기
     */
    public LivingEntity getTarget() {
        return target;
    }

    /**
     * 타겟 설정
     */
    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    /**
     * 타겟 존재 여부
     */
    public boolean hasTarget() {
        return target != null;
    }

    /**
     * 데미지 배율 가져오기
     */
    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    /**
     * 데미지 배율 설정
     */
    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    /**
     * 쿨다운 배율 가져오기
     */
    public double getCooldownMultiplier() {
        return cooldownMultiplier;
    }

    /**
     * 쿨다운 배율 설정
     */
    public void setCooldownMultiplier(double cooldownMultiplier) {
        this.cooldownMultiplier = cooldownMultiplier;
    }

    /**
     * 쿨다운 무시 여부
     */
    public boolean isBypassCooldown() {
        return bypassCooldown;
    }

    /**
     * 쿨다운 무시 설정
     */
    public void setBypassCooldown(boolean bypassCooldown) {
        this.bypassCooldown = bypassCooldown;
    }

    /**
     * 최종 쿨다운 계산
     */
    public int getFinalCooldown() {
        if (bypassCooldown) {
            return 0;
        }
        return (int) (skill.getCooldown() * cooldownMultiplier);
    }

    /**
     * 스킬 레벨
     */
    public int getSkillLevel() {
        return skill.getCurrentLevel();
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
        this.cancelled = cancelled;
    }

    /**
     * 취소 사유와 함께 취소
     */
    public void setCancelled(boolean cancelled, String reason) {
        this.cancelled = cancelled;
        this.cancelReason = reason;
    }

    /**
     * 취소 사유 가져오기
     */
    public String getCancelReason() {
        return cancelReason;
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