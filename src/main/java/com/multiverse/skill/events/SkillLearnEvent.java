package com.multiverse.skill. events;

import com.multiverse.skill.data.models. Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit. event.Event;
import org.bukkit. event.HandlerList;

/**
 * 스킬 습득 이벤트
 */
public class SkillLearnEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final Skill skill;
    private final int pointsUsed;
    private boolean cancelled = false;

    public SkillLearnEvent(Player player, Skill skill, int pointsUsed) {
        this.player = player;
        this.skill = skill;
        this.pointsUsed = pointsUsed;
    }

    /**
     * 플레이어 조회
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 습득한 스킬 조회
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * 사용된 포인트 조회
     */
    public int getPointsUsed() {
        return pointsUsed;
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