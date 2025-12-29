package com.multiverse.pet. api.event;

import com.multiverse. pet.model.Pet;
import org.bukkit.entity.Player;
import org.bukkit. event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util. Map;

/**
 * 펫 레벨업 이벤트
 * 펫이 레벨업할 때 발생
 */
public class PetLevelUpEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Pet pet;
    private final int oldLevel;
    private final int newLevel;
    private final long experienceGained;
    private Map<String, Double> statIncreases;
    private List<String> unlockedSkills;
    private int skillPointsGained;

    /**
     * 생성자
     */
    public PetLevelUpEvent(Player player, Pet pet, int oldLevel, int newLevel, long experienceGained) {
        this. player = player;
        this.pet = pet;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.experienceGained = experienceGained;
        this. skillPointsGained = newLevel - oldLevel;
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
     * 이전 레벨
     */
    public int getOldLevel() {
        return oldLevel;
    }

    /**
     * 새 레벨
     */
    public int getNewLevel() {
        return newLevel;
    }

    /**
     * 레벨 증가량
     */
    public int getLevelsGained() {
        return newLevel - oldLevel;
    }

    /**
     * 획득한 경험치
     */
    public long getExperienceGained() {
        return experienceGained;
    }

    /**
     * 스탯 증가량
     */
    public Map<String, Double> getStatIncreases() {
        return statIncreases;
    }

    /**
     * 스탯 증가량 설정
     */
    public void setStatIncreases(Map<String, Double> statIncreases) {
        this. statIncreases = statIncreases;
    }

    /**
     * 해금된 스킬 목록
     */
    public List<String> getUnlockedSkills() {
        return unlockedSkills;
    }

    /**
     * 해금된 스킬 설정
     */
    public void setUnlockedSkills(List<String> unlockedSkills) {
        this. unlockedSkills = unlockedSkills;
    }

    /**
     * 새 스킬 해금 여부
     */
    public boolean hasUnlockedSkills() {
        return unlockedSkills != null && !unlockedSkills. isEmpty();
    }

    /**
     * 획득한 스킬 포인트
     */
    public int getSkillPointsGained() {
        return skillPointsGained;
    }

    /**
     * 스킬 포인트 설정
     */
    public void setSkillPointsGained(int skillPointsGained) {
        this.skillPointsGained = skillPointsGained;
    }

    /**
     * 특정 레벨 도달 여부
     */
    public boolean reachedLevel(int level) {
        return oldLevel < level && newLevel >= level;
    }

    /**
     * 최대 레벨 도달 여부
     */
    public boolean reachedMaxLevel() {
        return newLevel >= pet.getMaxLevel();
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