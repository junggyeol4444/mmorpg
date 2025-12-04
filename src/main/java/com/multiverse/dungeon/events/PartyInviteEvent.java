package com.multiverse.dungeon.events;

import com.multiverse.dungeon. data.model.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit. event.HandlerList;

/**
 * 파티 초대 이벤트
 */
public class PartyInviteEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private Party party; // 파티
    private Player inviter; // 초대한 플레이어
    private Player invitee; // 초대받은 플레이어
    private long expireTime; // 초대 만료 시간 (밀리초)
    private boolean cancelled = false;

    /**
     * 생성자
     */
    public PartyInviteEvent(Party party, Player inviter, Player invitee, long expireTime) {
        this.party = party;
        this.inviter = inviter;
        this.invitee = invitee;
        this.expireTime = expireTime;
    }

    // ===== Getters & Setters =====

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Player getInviter() {
        return inviter;
    }

    public void setInviter(Player inviter) {
        this.inviter = inviter;
    }

    public Player getInvitee() {
        return invitee;
    }

    public void setInvitee(Player invitee) {
        this.invitee = invitee;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * 초대 만료까지 남은 시간 (초)
     *
     * @return 남은 시간
     */
    public long getRemainingTime() {
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 초대가 만료되었는지 확인
     *
     * @return 만료되었으면 true
     */
    public boolean isExpired() {
        return System. currentTimeMillis() >= expireTime;
    }

    // ===== Cancellable =====

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
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