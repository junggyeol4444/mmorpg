package com.multiverse.party.events;

import com.multiverse. party.models.Party;
import org.bukkit.entity.Player;
import org.bukkit. event. Cancellable;
import org.bukkit.event.Event;
import org. bukkit.event. HandlerList;

public class PartyCreateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Player creator;
    private final Party party;
    private boolean cancelled;
    private String cancelReason;

    public PartyCreateEvent(Player creator, Party party) {
        this. creator = creator;
        this.party = party;
        this.cancelled = false;
        this.cancelReason = null;
    }

    /**
     * 파티를 생성한 플레이어 반환
     * @return 파티 생성자
     */
    public Player getCreator() {
        return creator;
    }

    /**
     * 생성된 파티 반환
     * @return 생성된 파티
     */
    public Party getParty() {
        return party;
    }

    /**
     * 파티 이름 반환
     * @return 파티 이름
     */
    public String getPartyName() {
        return party.getPartyName();
    }

    /**
     * 파티 최대 인원 반환
     * @return 최대 인원 수
     */
    public int getMaxMembers() {
        return party.getMaxMembers();
    }

    /**
     * 파티 최대 인원 설정
     * @param maxMembers 최대 인원 수
     */
    public void setMaxMembers(int maxMembers) {
        if (maxMembers > 0) {
            party.setMaxMembers(maxMembers);
        }
    }

    /**
     * 취소 사유 반환
     * @return 취소 사유
     */
    public String getCancelReason() {
        return cancelReason;
    }

    /**
     * 취소 사유 설정
     * @param reason 취소 사유
     */
    public void setCancelReason(String reason) {
        this.cancelReason = reason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * 사유와 함께 이벤트 취소
     * @param reason 취소 사유
     */
    public void setCancelled(boolean cancel, String reason) {
        this.cancelled = cancel;
        this.cancelReason = reason;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}