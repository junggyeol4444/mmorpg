package com.multiverse.npcai.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;

/**
 * 플레이어가 NPC와 상호작용한 이벤트
 */
public class NPCInteractEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final int npcId;
    private final String interactType; // 대화, 상점 등

    public NPCInteractEvent(Player player, int npcId, String interactType) {
        this.player = player;
        this.npcId = npcId;
        this.interactType = interactType;
    }

    public Player getPlayer() { return player; }
    public int getNpcId() { return npcId; }
    public String getInteractType() { return interactType; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}