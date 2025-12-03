package com.multiverse.npcai.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;

/**
 * 플레이어가 NPC와 대화를 시작할 때 발생하는 이벤트
 */
public class DialogueStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final int npcId;
    private final String startNodeId;

    public DialogueStartEvent(Player player, int npcId, String startNodeId) {
        this.player = player;
        this.npcId = npcId;
        this.startNodeId = startNodeId;
    }

    public Player getPlayer() { return player; }
    public int getNpcId() { return npcId; }
    public String getStartNodeId() { return startNodeId; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}