package com.multiverse.playerdata.events;

import com.multiverse.playerdata.models.Transcendence;
import com.multiverse.playerdata.models.enums.TranscendentPower;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 플레이어가 초월을 수행할 때 호출되는 커스텀 이벤트
 */
public class TranscendenceEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Transcendence transcendence;
    private final TranscendentPower selectedPower;

    public TranscendenceEvent(Player player, Transcendence transcendence, TranscendentPower selectedPower) {
        super(true); // 비동기적 호출은 false로 설정 가능
        this.player = player;
        this.transcendence = transcendence;
        this.selectedPower = selectedPower;
    }

    public Player getPlayer() {
        return player;
    }

    public Transcendence getTranscendence() {
        return transcendence;
    }

    public TranscendentPower getSelectedPower() {
        return selectedPower;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}