package com.multiverse.death.events;

import com.multiverse.death.models.RevivalQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 플레이어가 부활 완료 시 발생하는 이벤트
 */
public class PlayerRevivalEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final RevivalQuest quest;

    public PlayerRevivalEvent(Player player, RevivalQuest quest) {
        this.player = player;
        this.quest = quest;
    }

    public Player getPlayer() {
        return player;
    }

    public RevivalQuest getQuest() {
        return quest;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}