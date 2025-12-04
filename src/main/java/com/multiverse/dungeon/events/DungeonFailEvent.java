package com.multiverse.dungeon.events;

import com.multiverse.dungeon.data.model.DungeonInstance;
import org.bukkit.event.Event;
import org.bukkit. event.HandlerList;

/**
 * 던전 실패 이벤트
 */
public class DungeonFailEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private DungeonInstance instance; // 던전 인스턴스
    private String reason; // 실패 사유

    /**
     * 생성자
     */
    public DungeonFailEvent(DungeonInstance instance, String reason) {
        this.instance = instance;
        this.reason = reason;
    }

    // ===== Getters & Setters =====

    public DungeonInstance getInstance() {
        return instance;
    }

    public void setInstance(DungeonInstance instance) {
        this.instance = instance;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 실패 사유 상수들
     */
    public static class FailReason {
        public static final String PARTY_WIPEOUT = "party_wipeout"; // 파티 전멸
        public static final String TIME_EXPIRED = "time_expired"; // 시간 초과
        public static final String ABANDONED = "abandoned"; // 포기
        public static final String ALL_PLAYERS_LEFT = "all_players_left"; // 모든 플레이어 퇴장
    }

    // ===== Handler =====

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}