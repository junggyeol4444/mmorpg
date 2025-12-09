package com. multiverse.party. events;

import com.multiverse.party.models.Party;
import com.multiverse.party.models.enums.PartyRole;
import org. bukkit.entity. Player;
import org.bukkit.event.Cancellable;
import org. bukkit.event. Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PartyKickEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Party party;
    private final UUID kickerUUID;
    private final String kickerName;
    private final UUID targetUUID;
    private final String targetName;
    private final PartyRole targetRole;
    private final KickType kickType;
    private String kickReason;
    private boolean cancelled;
    private String cancelReason;

    public PartyKickEvent(Party party, UUID kickerUUID, String kickerName,
                          UUID targetUUID, String targetName, PartyRole targetRole) {
        this(party, kickerUUID, kickerName, targetUUID, targetName, targetRole, KickType.NORMAL, null);
    }

    public PartyKickEvent(Party party, UUID kickerUUID, String kickerName,
                          UUID targetUUID, String targetName, PartyRole targetRole,
                          KickType kickType, String kickReason) {
        this. party = party;
        this.kickerUUID = kickerUUID;
        this.kickerName = kickerName;
        this.targetUUID = targetUUID;
        this.targetName = targetName;
        this.targetRole = targetRole;
        this.kickType = kickType;
        this.kickReason = kickReason;
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
     * 추방한 플레이어 UUID 반환
     * @return 추방자 UUID
     */
    public UUID getKickerUUID() {
        return kickerUUID;
    }

    /**
     * 추방한 플레이어 이름 반환
     * @return 추방자 이름
     */
    public String getKickerName() {
        return kickerName;
    }

    /**
     * 추방당한 플레이어 UUID 반환
     * @return 대상 UUID
     */
    public UUID getTargetUUID() {
        return targetUUID;
    }

    /**
     * 추방당한 플레이어 이름 반환
     * @return 대상 이름
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * 추방당한 플레이어의 역할 반환
     * @return 대상 역할
     */
    public PartyRole getTargetRole() {
        return targetRole;
    }

    /**
     * 추방 타입 반환
     * @return 추방 타입
     */
    public KickType getKickType() {
        return kickType;
    }

    /**
     * 일반 추방인지 확인
     * @return 일반 추방 여부
     */
    public boolean isNormalKick() {
        return kickType == KickType.NORMAL;
    }

    /**
     * 관리자 추방인지 확인
     * @return 관리자 추방 여부
     */
    public boolean isAdminKick() {
        return kickType == KickType. ADMIN;
    }

    /**
     * 시스템 추방인지 확인 (AFK 등)
     * @return 시스템 추방 여부
     */
    public boolean isSystemKick() {
        return kickType == KickType. SYSTEM;
    }

    /**
     * 추방 사유 반환
     * @return 추방 사유
     */
    public String getKickReason() {
        return kickReason;
    }

    /**
     * 추방 사유 설정
     * @param reason 추방 사유
     */
    public void setKickReason(String reason) {
        this.kickReason = reason;
    }

    /**
     * 추방자가 리더인지 확인
     * @return 리더 여부
     */
    public boolean isKickerLeader() {
        return kickerUUID != null && kickerUUID.equals(party.getLeaderId());
    }

    /**
     * 대상이 부리더였는지 확인
     * @return 부리더 여부
     */
    public boolean isTargetOfficer() {
        return targetRole == PartyRole. OFFICER;
    }

    /**
     * 파티 리더 UUID 반환
     * @return 리더 UUID
     */
    public UUID getLeaderId() {
        return party.getLeaderId();
    }

    /**
     * 추방 후 남은 멤버 수 반환
     * @return 남은 멤버 수
     */
    public int getMemberCountAfterKick() {
        return Math.max(0, party.getMembers().size() - 1);
    }

    /**
     * 파티 레벨 반환
     * @return 파티 레벨
     */
    public int getPartyLevel() {
        return party. getPartyLevel() != null ? party. getPartyLevel().getLevel() : 1;
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
        // 관리자 추방은 취소 불가
        if (kickType == KickType.ADMIN) {
            return false;
        }
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        // 관리자 추방은 취소 불가
        if (kickType == KickType. ADMIN) {
            return;
        }
        this. cancelled = cancel;
    }

    /**
     * 사유와 함께 이벤트 취소
     * 참고:  ADMIN 타입의 경우 취소가 무시됨
     * @param cancel 취소 여부
     * @param reason 취소 사유
     */
    public void setCancelled(boolean cancel, String reason) {
        if (kickType == KickType.ADMIN) {
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

    /**
     * 추방 타입 열거형
     */
    public enum KickType {
        /** 일반 추방 (리더/부리더에 의한) */
        NORMAL,
        /** 관리자에 의한 강제 추방 */
        ADMIN,
        /** 시스템에 의한 자동 추방 (AFK, 비활동 등) */
        SYSTEM,
        /** 투표에 의한 추방 */
        VOTE
    }
}