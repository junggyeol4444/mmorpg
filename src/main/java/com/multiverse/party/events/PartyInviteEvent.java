package com.multiverse.party. events;

import com.multiverse. party.models.Party;
import org.bukkit.entity.Player;
import org.bukkit. event. Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util. UUID;

public class PartyInviteEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Party party;
    private final Player inviter;
    private final Player target;
    private final long expireTime;
    private boolean cancelled;
    private String cancelReason;

    public PartyInviteEvent(Party party, Player inviter, Player target, long expireTime) {
        this.party = party;
        this. inviter = inviter;
        this. target = target;
        this.expireTime = expireTime;
        this.cancelled = false;
        this. cancelReason = null;
    }

    /**
     * 파티 반환
     * @return 파티
     */
    public Party getParty() {
        return party;
    }

    /**
     * 파티 ID 반환
     * @return 파티 UUID
     */
    public UUID getPartyId() {
        return party.getPartyId();
    }

    /**
     * 파티 이름 반환
     * @return 파티 이름
     */
    public String getPartyName() {
        return party.getPartyName();
    }

    /**
     * 초대한 플레이어 반환
     * @return 초대자
     */
    public Player getInviter() {
        return inviter;
    }

    /**
     * 초대자 UUID 반환
     * @return 초대자 UUID
     */
    public UUID getInviterUUID() {
        return inviter. getUniqueId();
    }

    /**
     * 초대자 이름 반환
     * @return 초대자 이름
     */
    public String getInviterName() {
        return inviter.getName();
    }

    /**
     * 초대받은 플레이어 반환
     * @return 초대 대상
     */
    public Player getTarget() {
        return target;
    }

    /**
     * 대상 UUID 반환
     * @return 대상 UUID
     */
    public UUID getTargetUUID() {
        return target. getUniqueId();
    }

    /**
     * 대상 이름 반환
     * @return 대상 이름
     */
    public String getTargetName() {
        return target.getName();
    }

    /**
     * 초대 만료 시간 반환
     * @return 만료 시간 (밀리초)
     */
    public long getExpireTime() {
        return expireTime;
    }

    /**
     * 남은 시간 반환 (초 단위)
     * @return 남은 시간
     */
    public long getRemainingSeconds() {
        long remaining = expireTime - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    /**
     * 파티 리더 UUID 반환
     * @return 리더 UUID
     */
    public UUID getLeaderId() {
        return party.getLeaderId();
    }

    /**
     * 초대자가 리더인지 확인
     * @return 리더 여부
     */
    public boolean isInviterLeader() {
        return inviter.getUniqueId().equals(party.getLeaderId());
    }

    /**
     * 현재 파티 멤버 수 반환
     * @return 멤버 수
     */
    public int getCurrentMemberCount() {
        return party.getMembers().size();
    }

    /**
     * 파티 최대 인원 반환
     * @return 최대 인원
     */
    public int getMaxMembers() {
        return party.getMaxMembers();
    }

    /**
     * 가입 시 남은 자리 수 반환
     * @return 남은 자리
     */
    public int getRemainingSlots() {
        return party.getMaxMembers() - party.getMembers().size();
    }

    /**
     * 파티 레벨 반환
     * @return 파티 레벨
     */
    public int getPartyLevel() {
        return party.getPartyLevel() != null ? party. getPartyLevel().getLevel() : 1;
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
     * @param cancel 취소 여부
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