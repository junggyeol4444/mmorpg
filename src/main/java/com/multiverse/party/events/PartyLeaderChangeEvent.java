package com. multiverse.party. events;

import com.multiverse.party.models.Party;
import com.multiverse.party.models.enums.PartyRole;
import org.bukkit. entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit. event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PartyLeaderChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Party party;
    private final UUID oldLeaderUUID;
    private final String oldLeaderName;
    private final UUID newLeaderUUID;
    private final String newLeaderName;
    private final ChangeReason reason;
    private boolean cancelled;
    private String cancelReason;

    public PartyLeaderChangeEvent(Party party, UUID oldLeaderUUID, String oldLeaderName,
                                   UUID newLeaderUUID, String newLeaderName, ChangeReason reason) {
        this.party = party;
        this.oldLeaderUUID = oldLeaderUUID;
        this.oldLeaderName = oldLeaderName;
        this.newLeaderUUID = newLeaderUUID;
        this.newLeaderName = newLeaderName;
        this.reason = reason;
        this. cancelled = false;
        this.cancelReason = null;
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
     * 이전 리더 UUID 반환
     * @return 이전 리더 UUID
     */
    public UUID getOldLeaderUUID() {
        return oldLeaderUUID;
    }

    /**
     * 이전 리더 이름 반환
     * @return 이전 리더 이름
     */
    public String getOldLeaderName() {
        return oldLeaderName;
    }

    /**
     * 새 리더 UUID 반환
     * @return 새 리더 UUID
     */
    public UUID getNewLeaderUUID() {
        return newLeaderUUID;
    }

    /**
     * 새 리더 이름 반환
     * @return 새 리더 이름
     */
    public String getNewLeaderName() {
        return newLeaderName;
    }

    /**
     * 변경 사유 반환
     * @return 변경 사유
     */
    public ChangeReason getReason() {
        return reason;
    }

    /**
     * 자발적 위임인지 확인
     * @return 자발적 위임 여부
     */
    public boolean isVoluntaryTransfer() {
        return reason == ChangeReason.VOLUNTARY_TRANSFER;
    }

    /**
     * 리더 탈퇴로 인한 변경인지 확인
     * @return 리더 탈퇴 여부
     */
    public boolean isLeaderLeft() {
        return reason == ChangeReason.LEADER_LEFT;
    }

    /**
     * 리더 접속 종료로 인한 변경인지 확인
     * @return 접속 종료 여부
     */
    public boolean isLeaderDisconnected() {
        return reason == ChangeReason.LEADER_DISCONNECTED;
    }

    /**
     * 관리자에 의한 변경인지 확인
     * @return 관리자 변경 여부
     */
    public boolean isAdminForced() {
        return reason == ChangeReason.ADMIN_FORCE;
    }

    /**
     * 새 리더가 기존 부리더였는지 확인
     * @return 부리더 승격 여부
     */
    public boolean isOfficerPromotion() {
        PartyRole previousRole = party.getRoles().get(newLeaderUUID);
        return previousRole == PartyRole.OFFICER;
    }

    /**
     * 파티 멤버 수 반환
     * @return 멤버 수
     */
    public int getMemberCount() {
        return party.getMembers().size();
    }

    /**
     * 파티 레벨 반환
     * @return 파티 레벨
     */
    public int getPartyLevel() {
        return party.getPartyLevel() != null ? party.getPartyLevel().getLevel() : 1;
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
        this. cancelReason = reason;
    }

    @Override
    public boolean isCancelled() {
        // 리더 탈퇴/접속 종료 또는 관리자 강제 변경은 취소 불가
        if (reason == ChangeReason.LEADER_LEFT ||
            reason == ChangeReason.LEADER_DISCONNECTED ||
            reason == ChangeReason.ADMIN_FORCE) {
            return false;
        }
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        // 리더 탈퇴/접속 종료 또는 관리자 강제 변경은 취소 불가
        if (reason == ChangeReason. LEADER_LEFT ||
            reason == ChangeReason. LEADER_DISCONNECTED ||
            reason == ChangeReason. ADMIN_FORCE) {
            return;
        }
        this.cancelled = cancel;
    }

    /**
     * 사유와 함께 이벤트 취소
     * 참고:  특정 사유의 경우 취소가 무시됨
     * @param cancel 취소 여부
     * @param reason 취소 사유
     */
    public void setCancelled(boolean cancel, String reason) {
        if (this.reason == ChangeReason.LEADER_LEFT ||
            this.reason == ChangeReason. LEADER_DISCONNECTED ||
            this. reason == ChangeReason.ADMIN_FORCE) {
            return;
        }
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

    /**
     * 리더 변경 사유 열거형
     */
    public enum ChangeReason {
        /** 자발적 위임 (/party transfer) */
        VOLUNTARY_TRANSFER,
        /** 리더가 파티를 떠남 */
        LEADER_LEFT,
        /** 리더 접속 종료 (설정에 따라) */
        LEADER_DISCONNECTED,
        /** 관리자에 의한 강제 변경 */
        ADMIN_FORCE,
        /** 리더 AFK로 인한 자동 변경 */
        LEADER_AFK,
        /** 투표에 의한 변경 */
        VOTE
    }
}