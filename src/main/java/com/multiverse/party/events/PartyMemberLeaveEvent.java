package com.multiverse. party.events;

import com.multiverse.party.models. Party;
import com.multiverse.party.models.enums.LeaveReason;
import com.multiverse.party.models.enums.PartyRole;
import org.bukkit.entity.Player;
import org. bukkit.event. Cancellable;
import org.bukkit.event.Event;
import org. bukkit.event. HandlerList;

import java.util. UUID;

public class PartyMemberLeaveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Party party;
    private final UUID playerUUID;
    private final String playerName;
    private final LeaveReason reason;
    private final UUID kickedBy;
    private final PartyRole previousRole;
    private boolean cancelled;
    private String cancelReason;

    public PartyMemberLeaveEvent(Party party, UUID playerUUID, String playerName, 
                                  LeaveReason reason, PartyRole previousRole) {
        this(party, playerUUID, playerName, reason, previousRole, null);
    }

    public PartyMemberLeaveEvent(Party party, UUID playerUUID, String playerName,
                                  LeaveReason reason, PartyRole previousRole, UUID kickedBy) {
        this.party = party;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.reason = reason;
        this.previousRole = previousRole;
        this.kickedBy = kickedBy;
        this.cancelled = false;
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
     * 떠나는 플레이어 UUID 반환
     * @return 플레이어 UUID
     */
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    /**
     * 떠나는 플레이어 이름 반환
     * @return 플레이어 이름
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * 탈퇴 사유 반환
     * @return 탈퇴 사유
     */
    public LeaveReason getReason() {
        return reason;
    }

    /**
     * 자발적 탈퇴인지 확인
     * @return 자발적 탈퇴 여부
     */
    public boolean isVoluntary() {
        return reason == LeaveReason.VOLUNTARY;
    }

    /**
     * 추방으로 인한 탈퇴인지 확인
     * @return 추방 여부
     */
    public boolean isKicked() {
        return reason == LeaveReason.KICKED;
    }

    /**
     * 접속 종료로 인한 탈퇴인지 확인
     * @return 접속 종료 탈퇴 여부
     */
    public boolean isDisconnect() {
        return reason == LeaveReason.DISCONNECT;
    }

    /**
     * 추방한 플레이어 UUID 반환
     * @return 추방자 UUID (추방이 아닌 경우 null)
     */
    public UUID getKickedBy() {
        return kickedBy;
    }

    /**
     * 이전 역할 반환
     * @return 이전 파티 역할
     */
    public PartyRole getPreviousRole() {
        return previousRole;
    }

    /**
     * 리더가 떠나는지 확인
     * @return 리더 탈퇴 여부
     */
    public boolean isLeaderLeaving() {
        return previousRole == PartyRole.LEADER;
    }

    /**
     * 부리더가 떠나는지 확인
     * @return 부리더 탈퇴 여부
     */
    public boolean isOfficerLeaving() {
        return previousRole == PartyRole.OFFICER;
    }

    /**
     * 탈퇴 후 남은 멤버 수 반환
     * @return 남은 멤버 수
     */
    public int getMemberCountAfterLeave() {
        return Math.max(0, party.getMembers().size() - 1);
    }

    /**
     * 탈퇴 후 파티가 해체되는지 확인
     * @return 파티 해체 여부
     */
    public boolean willPartyDisband() {
        return getMemberCountAfterLeave() <= 0;
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
        // 접속 종료의 경우 취소 불가
        if (reason == LeaveReason.DISCONNECT) {
            return false;
        }
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        // 접속 종료의 경우 취소 불가
        if (reason == LeaveReason.DISCONNECT) {
            return;
        }
        this.cancelled = cancel;
    }

    /**
     * 사유와 함께 이벤트 취소
     * 참고:  DISCONNECT 사유의 경우 취소가 무시됨
     * @param cancel 취소 여부
     * @param reason 취소 사유
     */
    public void setCancelled(boolean cancel, String reason) {
        if (this.reason == LeaveReason.DISCONNECT) {
            return;
        }
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