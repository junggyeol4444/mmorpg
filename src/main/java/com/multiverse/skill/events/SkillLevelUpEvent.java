package com.multiverse.skill.events;

import com.multiverse.skill.data. models.LearnedSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit. event.HandlerList;

/**
 * 스킬 레벨업 이벤트
 */
public class SkillLevelUpEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final LearnedSkill learnedSkill;
    private final int previousLevel;
    private final int newLevel;
    private final long experienceGained;

    public SkillLevelUpEvent(Player player, LearnedSkill learnedSkill, int previousLevel, 
                            int newLevel, long experienceGained) {
        this.player = player;
        this.learnedSkill = learnedSkill;
        this.previousLevel = previousLevel;
        this.newLevel = newLevel;
        this.experienceGained = experienceGained;
    }

    /**
     * 플레이어 조회
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 레벨업한 스킬 조회
     */
    public LearnedSkill getLearnedSkill() {
        return learnedSkill;
    }

    /**
     * 이전 레벨 조회
     */
    public int getPreviousLevel() {
        return previousLevel;
    }

    /**
     * 새로운 레벨 조회
     */
    public int getNewLevel() {
        return newLevel;
    }

    /**
     * 획득한 경험치 조회
     */
    public long getExperienceGained() {
        return experienceGained;
    }

    /**
     * 레벨 상승 수 조회
     */
    public int getLevelIncrease() {
        return newLevel - previousLevel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}