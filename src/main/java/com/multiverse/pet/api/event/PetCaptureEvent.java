package com.multiverse.pet.  api.  event;

import com.multiverse.pet.model.  Pet;
import com.multiverse.pet.  model.PetRarity;
import com.multiverse.  pet.model.acquisition.  CaptureBall;
import org.bukkit.  entity.LivingEntity;
import org.bukkit.  entity.Player;
import org. bukkit.event.  Cancellable;
import org.bukkit.  event.Event;
import org. bukkit.event. HandlerList;

/**
 * 펫 포획 이벤트
 * 펫 포획 시도 또는 성공 시 발생
 */
public class PetCaptureEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final LivingEntity target;
    private final CaptureBall captureBall;
    private final CapturePhase phase;
    private Pet capturedPet;
    private double captureChance;
    private boolean success;
    private boolean cancelled;
    private String cancelReason;

    /**
     * 포획 단계
     */
    public enum CapturePhase {
        /** 포획 시도 전 */
        ATTEMPT,
        /** 포획 판정 중 */
        PROCESSING,
        /** 포획 성공 */
        SUCCESS,
        /** 포획 실패 */
        FAILED
    }

    /**
     * 생성자 (포획 시도)
     */
    public PetCaptureEvent(Player player, LivingEntity target, CaptureBall captureBall, double captureChance) {
        this. player = player;
        this.  target = target;
        this.captureBall = captureBall;
        this.captureChance = captureChance;
        this.phase = CapturePhase.ATTEMPT;
        this.success = false;
        this. cancelled = false;
    }

    /**
     * 생성자 (포획 결과)
     */
    public PetCaptureEvent(Player player, LivingEntity target, CaptureBall captureBall, 
                            CapturePhase phase, boolean success, Pet capturedPet) {
        this. player = player;
        this. target = target;
        this.captureBall = captureBall;
        this.phase = phase;
        this. success = success;
        this.capturedPet = capturedPet;
        this.cancelled = false;
    }

    /**
     * 플레이어 가져오기
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 타겟 엔티티 가져오기
     */
    public LivingEntity getTarget() {
        return target;
    }

    /**
     * 타겟 엔티티 타입
     */
    public org.bukkit.entity.EntityType getTargetType() {
        return target.getType();
    }

    /**
     * 포획구 가져오기
     */
    public CaptureBall getCaptureBall() {
        return captureBall;
    }

    /**
     * 포획구 타입
     */
    public String getCaptureBallType() {
        return captureBall.getBallType();
    }

    /**
     * 포획 단계 가져오기
     */
    public CapturePhase getPhase() {
        return phase;
    }

    /**
     * 포획 확률 가져오기
     */
    public double getCaptureChance() {
        return captureChance;
    }

    /**
     * 포획 확률 설정
     */
    public void setCaptureChance(double captureChance) {
        this. captureChance = Math.max(0, Math.min(100, captureChance));
    }

    /**
     * 포획 확률 배율 적용
     */
    public void multiplyCaptureChance(double multiplier) {
        this.captureChance *= multiplier;
        this.captureChance = Math.max(0, Math.min(100, captureChance));
    }

    /**
     * 포획 성공 여부
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 포획 성공 설정
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 포획된 펫 가져오기
     */
    public Pet getCapturedPet() {
        return capturedPet;
    }

    /**
     * 포획된 펫 설정
     */
    public void setCapturedPet(Pet capturedPet) {
        this. capturedPet = capturedPet;
    }

    /**
     * 포획된 펫 존재 여부
     */
    public boolean hasCapturedPet() {
        return capturedPet != null;
    }

    /**
     * 포획된 펫 희귀도
     */
    public PetRarity getCapturedPetRarity() {
        return capturedPet != null ? capturedPet.getRarity() : null;
    }

    /**
     * 시도 단계인지
     */
    public boolean isAttemptPhase() {
        return phase == CapturePhase. ATTEMPT;
    }

    /**
     * 성공 단계인지
     */
    public boolean isSuccessPhase() {
        return phase == CapturePhase.SUCCESS;
    }

    /**
     * 실패 단계인지
     */
    public boolean isFailedPhase() {
        return phase == CapturePhase. FAILED;
    }

    /**
     * 취소 가능 여부
     */
    public boolean isCancellable() {
        return phase == CapturePhase.ATTEMPT;
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
        if (! isCancellable() && cancelled) {
            return;
        }
        this.cancelled = cancelled;
    }

    /**
     * 취소 사유와 함께 취소
     */
    public void setCancelled(boolean cancelled, String reason) {
        setCancelled(cancelled);
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