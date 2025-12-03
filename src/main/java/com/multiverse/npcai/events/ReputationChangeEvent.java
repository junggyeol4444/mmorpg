package com.multiverse.npcai.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;

/**
 * 플레이어-NPC 간 호감도 변경 이벤트
 */
public class ReputationChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final int npcId;
    private final int beforePoints;
    private final int afterPoints;
    private final String reason;

    public ReputationChangeEvent(Player player, int npcId, int beforePoints, int afterPoints, String reason) {
        this.player = player;
        this.npcId = npcId;
        this.beforePoints = beforePoints;
        this.afterPoints = afterPoints;
        this.reason = reason;
    }

    public Player getPlayer() { return player; }
    public int getNpcId() { return npcId; }
    public int getBeforePoints() { return beforePoints; }
    public int getAfterPoints() { return afterPoints; }
    public String getReason() { return reason; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}