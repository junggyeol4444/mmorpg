package com.multiverse.dungeon.events;

import com. multiverse.dungeon.data. model.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 파티 생성 이벤트
 */
public class PartyCreatedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private Party party; // 생성된 파티
    private Player leader; // 파티 리더

    /**
     * 생성자
     */
    public PartyCreatedEvent(Party party, Player leader) {
        this. party = party;
        this. leader = leader;
    }

    // ===== Getters & Setters =====

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Player getLeader() {
        return leader;
    }

    public void setLeader(Player leader) {
        this.  leader = leader;
    }

    /**
     * 파티 크기
     *
     * @return 파티 멤버 수
     */
    public int getPartySize() {
        if (party == null) {
            return 0;
        }
        return party.getMemberCount();
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