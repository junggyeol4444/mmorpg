package com. multiverse.pet. api.event;

import com.multiverse.pet.model. Pet;
import com.multiverse.pet.model.PetRarity;
import com.multiverse.pet.model.evolution.PetEvolution;
import org. bukkit.entity. Player;
import org.bukkit.event. Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util. List;
import java.util.Map;

/**
 * 펫 진화 이벤트
 * 펫이 진화할 때 발생
 */
public class PetEvolutionEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Pet pet;
    private final PetEvolution evolution;
    private final String fromSpeciesId;
    private final String toSpeciesId;
    private final int fromStage;
    private final int toStage;
    private boolean cancelled;
    private String cancelReason;

    // 진화 결과
    private boolean success;
    private PetRarity newRarity;
    private Map<String, Double> statBonuses;
    private List<String> newSkills;
    private List<String> newAbilities;

    /**
     * 생성자 (진화 시도 전)
     */
    public PetEvolutionEvent(Player player, Pet pet, PetEvolution evolution) {
        this. player = player;
        this.pet = pet;
        this.evolution = evolution;
        this.fromSpeciesId = pet.getSpeciesId();
        this.toSpeciesId = evolution.getToSpeciesId();
        this.fromStage = pet.getEvolutionStage();
        this.toStage = evolution. getToStage();
        this.cancelled = false;
        this.success = false;
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
     * 진화 정보 가져오기
     */
    public PetEvolution getEvolution() {
        return evolution;
    }

    /**
     * 진화 전 종족 ID
     */
    public String getFromSpeciesId() {
        return fromSpeciesId;
    }

    /**
     * 진화 후 종족 ID
     */
    public String getToSpeciesId() {
        return toSpeciesId;
    }

    /**
     * 진화 전 단계
     */
    public int getFromStage() {
        return fromStage;
    }

    /**
     * 진화 후 단계
     */
    public int getToStage() {
        return toStage;
    }

    /**
     * 진화 성공 여부
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 진화 성공 설정
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 새 희귀도
     */
    public PetRarity getNewRarity() {
        return newRarity;
    }

    /**
     * 새 희귀도 설정
     */
    public void setNewRarity(PetRarity newRarity) {
        this.newRarity = newRarity;
    }

    /**
     * 스탯 보너스
     */
    public Map<String, Double> getStatBonuses() {
        return statBonuses;
    }

    /**
     * 스탯 보너스 설정
     */
    public void setStatBonuses(Map<String, Double> statBonuses) {
        this. statBonuses = statBonuses;
    }

    /**
     * 새 스킬 목록
     */
    public List<String> getNewSkills() {
        return newSkills;
    }

    /**
     * 새 스킬 설정
     */
    public void setNewSkills(List<String> newSkills) {
        this.newSkills = newSkills;
    }

    /**
     * 새 능력 목록
     */
    public List<String> getNewAbilities() {
        return newAbilities;
    }

    /**
     * 새 능력 설정
     */
    public void setNewAbilities(List<String> newAbilities) {
        this.newAbilities = newAbilities;
    }

    /**
     * 진화 성공 확률
     */
    public double getSuccessChance() {
        return evolution.getSuccessChance();
    }

    /**
     * 진화 비용 (골드)
     */
    public double getGoldCost() {
        return evolution.getGoldCost();
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