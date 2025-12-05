package com.multiverse.skill. events;

import com.multiverse.skill.data.models. Skill;
import com.multiverse.skill.data.models.LearnedSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit. event.Event;
import org.bukkit. event.HandlerList;

/**
 * 스킬 사용 이벤트
 */
public class SkillUseEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final Skill skill;
    private final LearnedSkill learnedSkill;
    private double manaCost;
    private boolean cancelled = false;

    public SkillUseEvent(Player player, Skill skill, LearnedSkill learnedSkill, double manaCost) {
        this.player = player;
        this.skill = skill;
        this.learnedSkill = learnedSkill;
        this.manaCost = manaCost;
    }

    /**
     * 플레이어 조회
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 사용한 스킬 조회
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * 습득한 스킬 정보 조회
     */
    public LearnedSkill getLearnedSkill() {
        return learnedSkill;
    }

    /**
     * 마나 비용 조회
     */
    public double getManaCost() {
        return manaCost;
    }

    /**
     * 마나 비용 설정
     */
    public void setManaCost(double manaCost) {
        this.manaCost = manaCost;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}