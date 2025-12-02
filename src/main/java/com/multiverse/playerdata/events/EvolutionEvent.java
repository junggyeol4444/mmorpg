package com.multiverse.playerdata.events;

import com.multiverse.playerdata.models.Evolution;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 플레이어가 진화를 수행할 때 호출되는 커스텀 이벤트
 */
public class EvolutionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Evolution evolution;
    private final String fromRace;
    private final String toRace;

    public EvolutionEvent(Player player, Evolution evolution, String fromRace, String toRace) {
        super(true); // 비동기적 호출은 false로 설정 가능
        this.player = player;
        this.evolution = evolution;
        this.fromRace = fromRace;
        this.toRace = toRace;
    }

    public Player getPlayer() {
        return player;
    }

    public Evolution getEvolution() {
        return evolution;
    }

    public String getFromRace() {
        return fromRace;
    }

    public String getToRace() {
        return toRace;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}