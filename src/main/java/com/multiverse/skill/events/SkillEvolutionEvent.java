package com.multiverse.skill. events;

import com.multiverse. skill.data.models.SkillEvolution;
import com.multiverse.skill. data.enums.EvolutionType;
import org.bukkit.entity.Player;
import org.bukkit.event. Cancellable;
import org. bukkit.event.Event;
import org.bukkit.event. HandlerList;

/**
 * 스킬 진화 이벤트
 */
public class SkillEvolutionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final SkillEvolution evolution;
    private final String fromSkillId;
    private final String toSkillId;
    private EvolutionType evolutionType;
    private boolean cancelled = false;

    public SkillEvolutionEvent(Player player, SkillEvolution evolution, String fromSkillId, 
                              String toSkillId, EvolutionType evolutionType) {
        this. player = player;
        this. evolution = evolution;
        this. fromSkillId = fromSkillId;
        this.toSkillId = toSkillId;
        this.evolutionType = evolutionType;
    }

    /**
     * 플레이어 조회
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 진화 데이터 조회
     */
    public SkillEvolution getEvolution() {
        return evolution;
    }

    /**
     * 이전 스킬 ID 조회
     */
    public String getFromSkillId() {
        return fromSkillId;
    }

    /**
     * 진화 후 스킬 ID 조회
     */
    public String getToSkillId() {
        return toSkillId;
    }

    /**
     * 진화 타입 조회
     */
    public EvolutionType getEvolutionType() {
        return evolutionType;
    }

    /**
     * 진화 타입 설정
     */
    public void setEvolutionType(EvolutionType evolutionType) {
        this.evolutionType = evolutionType;
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