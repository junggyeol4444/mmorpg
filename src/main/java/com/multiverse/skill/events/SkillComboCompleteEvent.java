package com.multiverse.skill. events;

import com.multiverse.skill.data.models. SkillCombo;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 스킬 콤보 완성 이벤트
 */
public class SkillComboCompleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final SkillCombo combo;
    private final long completionTime;
    private double bonusDamage;
    private boolean hasBonus;

    public SkillComboCompleteEvent(Player player, SkillCombo combo, long completionTime) {
        this.player = player;
        this.combo = combo;
        this.completionTime = completionTime;
        this.bonusDamage = combo.getDamageBonus();
        this.hasBonus = combo.isHasBonus();
    }

    /**
     * 플레이어 조회
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 완성된 콤보 조회
     */
    public SkillCombo getCombo() {
        return combo;
    }

    /**
     * 완성 시간 조회 (밀리초)
     */
    public long getCompletionTime() {
        return completionTime;
    }

    /**
     * 보너스 데미지 조회
     */
    public double getBonusDamage() {
        return bonusDamage;
    }

    /**
     * 보너스 데미지 설정
     */
    public void setBonusDamage(double bonusDamage) {
        this.bonusDamage = bonusDamage;
    }

    /**
     * 보너스 여부 조회
     */
    public boolean isHasBonus() {
        return hasBonus;
    }

    /**
     * 보너스 여부 설정
     */
    public void setHasBonus(boolean hasBonus) {
        this.hasBonus = hasBonus;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}