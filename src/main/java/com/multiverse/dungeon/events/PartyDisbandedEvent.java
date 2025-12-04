package com.multiverse.dungeon.events;

import com.multiverse.dungeon.data.model.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 파티 해체 이벤트
 */
public class PartyDisbandedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private Party party; // 해체된 파티
    private Player disbander; // 해체한 플레이어
    private String reason; // 해체 사유

    /**
     * 생성자
     */
    public PartyDisbandedEvent(Party party, Player disbander, String reason) {
        this. party = party;
        this. disbander = disbander;
        this.reason = reason;
    }

    // ===== Getters & Setters =====

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Player getDisbander() {
        return disbander;
    }

    public void setDisbander(Player disbander) {
        this.disbander = disbander;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 해체 사유 상수들
     */
    public static class DisbandReason {
        public static final String LEADER_COMMAND = "leader_command"; // 리더 명령
        public static final String LAST_MEMBER_LEFT = "last_member_left"; // 마지막 멤버 퇴장
        public static final String DUNGEON_COMPLETED = "dungeon_completed"; // 던전 완료
        public static final String DUNGEON_FAILED = "dungeon_failed"; // 던전 실패
        public static final String ADMIN_COMMAND = "admin_command"; // 관리자 명령
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