package com.multiverse.party.events;

import com.multiverse. party.models.Party;
import com.multiverse.party.models.enums.PartyDisbandReason;
import org.bukkit.entity.Player;
import org. bukkit.event. Cancellable;
import org.bukkit.event.Event;
import org. bukkit.event. HandlerList;

import java.util.List;
import java.util. UUID;

public class PartyDisbandEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final Party party;
    private final PartyDisbandReason reason;
    private final UUID disbandedBy;
    private boolean cancelled;
    private String cancelReason;

    public PartyDisbandEvent(Party party, PartyDisbandReason reason) {
        this(party, reason, null);
    }

    public PartyDisbandEvent(Party party, PartyDisbandReason reason, UUID disbandedBy) {
        this.party = party;
        this. reason = reason;
        this.disbandedBy = disbandedBy;
        this.cancelled = false;
        this. cancelReason = null;
    }

    /**
     * 해체되는 파티 반환
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
        return party. getPartyId();
    }

    /**
     * 파티 이름 반환
     * @return 파티 이름
     */
    public String getPartyName() {
        return party.getPartyName();
    }

    /**
     * 해체 사유 반환
     * @return 해체 사유
     */
    public PartyDisbandReason getReason() {
        return reason;
    }

    /**
     * 파티를 해체한 플레이어 UUID 반환
     * @return 해체자 UUID (관리자/시스템의 경우 null)
     */
    public UUID getDisbandedBy() {
        return disbandedBy;
    }

    /**
     * 리더에 의한 해체인지 확인
     * @return 리더 해체 여부
     */
    public boolean isDisbandedByLeader() {
        return reason == PartyDisbandReason. LEADER_DISBAND;
    }

    /**
     * 인원 부족으로 인한 자동 해체인지 확인
     * @return 자동 해체 여부
     */
    public boolean isAutoDisbanded() {
        return reason == PartyDisbandReason. NO_MEMBERS;
    }

    /**
     * 관리자에 의한 강제 해체인지 확인
     * @return 강제 해체 여부
     */
    public boolean isAdminForced() {
        return reason == PartyDisbandReason. ADMIN_FORCE;
    }

    /**
     * 파티 멤버 UUID 목록 반환
     * @return 멤버 UUID 목록
     */
    public List<UUID> getMembers() {
        return party.getMembers();
    }

    /**
     * 파티 멤버 수 반환
     * @return 멤버 수
     */
    public int getMemberCount() {
        return party.getMembers().size();
    }

    /**
     * 파티 리더 UUID 반환
     * @return 리더 UUID
     */
    public UUID getLeaderId() {
        return party.getLeaderId();
    }

    /**
     * 파티 레벨 반환
     * @return 파티 레벨
     */
    public int getPartyLevel() {
        return party.getPartyLevel() != null ? party.getPartyLevel().getLevel() : 1;
    }

    /**
     * 파티 경험치 반환
     * @return 파티 경험치
     */
    public long getPartyExp() {
        return party.getPartyLevel() != null ? party.getPartyLevel().getExperience() : 0;
    }

    /**
     * 파티 생성 시간 반환
     * @return 생성 시간 (밀리초)
     */
    public long getCreatedTime() {
        return party.getCreatedTime();
    }

    /**
     * 파티가 존재한 시간 반환 (밀리초)
     * @return 존재 시간
     */
    public long getExistedDuration() {
        return System.currentTimeMillis() - party.getCreatedTime();
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
     * 참고:  ADMIN_FORCE 사유의 경우 취소가 무시될 수 있음
     * @param cancel 취소 여부
     * @param reason 취소 사유
     */
    public void setCancelled(boolean cancel, String reason) {
        this. cancelled = cancel;
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