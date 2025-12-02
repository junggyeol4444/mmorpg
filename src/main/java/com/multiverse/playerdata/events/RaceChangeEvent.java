package com.multiverse.playerdata.events;

import com.multiverse.playerdata.models.Race;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 플레이어의 종족이 변경될 때 호출되는 커스텀 이벤트
 */
public class RaceChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Race oldRace;
    private final Race newRace;

    public RaceChangeEvent(Player player, Race oldRace, Race newRace) {
        super(true); // 비동기적 호출은 false로 설정 가능
        this.player = player;
        this.oldRace = oldRace;
        this.newRace = newRace;
    }

    public Player getPlayer() {
        return player;
    }

    public Race getOldRace() {
        return oldRace;
    }

    public Race getNewRace() {
        return newRace;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}