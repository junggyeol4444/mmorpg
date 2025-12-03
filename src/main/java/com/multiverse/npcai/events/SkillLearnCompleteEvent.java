package com.multiverse.npcai.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;

/**
 * 플레이어가 스킬 트레이너로부터 스킬을 완전히 습득했을 때의 이벤트
 */
public class SkillLearnCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final int npcId;
    private final String skillId;

    public SkillLearnCompleteEvent(Player player, int npcId, String skillId) {
        this.player = player;
        this.npcId = npcId;
        this.skillId = skillId;
    }

    public Player getPlayer() { return player; }
    public int getNpcId() { return npcId; }
    public String getSkillId() { return skillId; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}