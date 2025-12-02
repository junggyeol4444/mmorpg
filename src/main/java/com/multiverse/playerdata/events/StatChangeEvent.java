package com.multiverse.playerdata.events;

import com.multiverse.playerdata.models.enums.StatType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 플레이어의 스탯이 변경될 때 호출되는 커스텀 이벤트
 */
public class StatChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final StatType statType;
    private final int oldValue;
    private final int newValue;

    public StatChangeEvent(Player player, StatType statType, int oldValue, int newValue) {
        super(true); // 비동기적 호출은 false로 설정 가능
        this.player = player;
        this.statType = statType;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Player getPlayer() {
        return player;
    }

    public StatType getStatType() {
        return statType;
    }

    public int getOldValue() {
        return oldValue;
    }

    public int getNewValue() {
        return newValue;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}