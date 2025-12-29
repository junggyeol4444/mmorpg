package com.multiverse.pet. api.  event;

import com.multiverse.pet.model.Pet;
import com.multiverse.  pet.model.PetRarity;
import com.  multiverse.pet.  model.breeding.PetBreeding;
import org.bukkit.entity.Player;
import org. bukkit.event. Cancellable;
import org.bukkit.  event. Event;
import org.bukkit.event.HandlerList;

/**
 * 펫 교배 이벤트
 * 교배가 시작되거나 완료될 때 발생
 */
public class PetBreedEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Pet parent1;
    private final Pet parent2;
    private final BreedPhase phase;
    private PetBreeding breeding;
    private Pet offspring;
    private boolean cancelled;
    private String cancelReason;

    /**
     * 교배 단계
     */
    public enum BreedPhase {
        /** 교배 시작 */
        START,
        /** 교배 진행 중 */
        IN_PROGRESS,
        /** 교배 완료 (성공) */
        COMPLETE,
        /** 교배 실패 */
        FAILED,
        /** 교배 취소 */
        CANCELLED,
        /** 자손 수령 */
        COLLECT
    }

    /**
     * 생성자 (교배 시작)
     */
    public PetBreedEvent(Player player, Pet parent1, Pet parent2, BreedPhase phase) {
        this.player = player;
        this. parent1 = parent1;
        this.parent2 = parent2;
        this.phase = phase;
        this.cancelled = false;
    }

    /**
     * 생성자 (교배 완료)
     */
    public PetBreedEvent(Player player, Pet parent1, Pet parent2, BreedPhase phase, Pet offspring) {
        this(player, parent1, parent2, phase);
        this.offspring = offspring;
    }

    /**
     * 플레이어 가져오기
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 부모 1 가져오기
     */
    public Pet getParent1() {
        return parent1;
    }

    /**
     * 부모 2 가져오기
     */
    public Pet getParent2() {
        return parent2;
    }

    /**
     * 교배 단계 가져오기
     */
    public BreedPhase getPhase() {
        return phase;
    }

    /**
     * 교배 정보 가져오기
     */
    public PetBreeding getBreeding() {
        return breeding;
    }

    /**
     * 교배 정보 설정
     */
    public void setBreeding(PetBreeding breeding) {
        this.breeding = breeding;
    }

    /**
     * 자손 가져오기
     */
    public Pet getOffspring() {
        return offspring;
    }

    /**
     * 자손 설정
     */
    public void setOffspring(Pet offspring) {
        this.offspring = offspring;
    }

    /**
     * 자손 존재 여부
     */
    public boolean hasOffspring() {
        return offspring != null;
    }

    /**
     * 교배 시작 단계인지
     */
    public boolean isStartPhase() {
        return phase == BreedPhase.START;
    }

    /**
     * 교배 완료 단계인지
     */
    public boolean isCompletePhase() {
        return phase == BreedPhase.COMPLETE;
    }

    /**
     * 변이 발생 여부
     */
    public boolean isMutation() {
        return offspring != null && offspring.isMutation();
    }

    /**
     * 자손 희귀도
     */
    public PetRarity getOffspringRarity() {
        return offspring != null ? offspring.getRarity() : null;
    }

    /**
     * 자손 종족 ID
     */
    public String getOffspringSpeciesId() {
        return offspring != null ?  offspring.getSpeciesId() : null;
    }

    /**
     * 취소 가능 여부
     */
    public boolean isCancellable() {
        return phase == BreedPhase.START;
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