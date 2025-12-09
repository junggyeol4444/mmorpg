package com.multiverse.party. events;

import com.multiverse. party.models.Party;
import com. multiverse.party. models.enums.PartyRole;
import org.bukkit.entity.Player;
import org.bukkit. event. Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PartyMemberJoinEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Party party;
    private final Player player;
    private final JoinType joinType;
    private final UUID invitedBy;
    private boolean cancelled;
    private String cancelReason;

    public PartyMemberJoinEvent(Party party, Player player, JoinType joinType) {
        this(party, player, joinType, null);
    }

    public PartyMemberJoinEvent(Party party, Player player, JoinType joinType, UUID invitedBy) {
        this.party = party;
        this.player = player;
        this.joinType = joinType;
        this.invitedBy = invitedBy;
        this.cancelled = false;
        this. cancelReason = null;
    }

    /**
     * 가입할 파티 반환
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
     * 가입하는 플레이어 반환
     * @return 플레이어
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * 플레이어 UUID 반환
     * @return 플레이어 UUID
     */
    public UUID getPlayerUUID() {
        return player.getUniqueId();
    }

    /**
     * 플레이어 이름 반환
     * @return 플레이어 이름
     */
    public String getPlayerName() {
        return player. getName();
    }

    /**
     * 가입 방식 반환
     * @return 가입 방식
     */
    public JoinType getJoinType() {
        return joinType;
    }

    /**
     * 초대한 플레이어 UUID 반환
     * @return 초대자 UUID (초대가 아닌 경우 null)
     */
    public UUID getInvitedBy() {
        return invitedBy;
    }

    /**
     * 초대를 통한 가입인지 확인
     * @return 초대 가입 여부
     */
    public boolean isInviteJoin() {
        return joinType == JoinType.INVITE;
    }

    /**
     * 공개 파티 참가인지 확인
     * @return 공개 참가 여부
     */
    public boolean isPublicJoin() {
        return joinType == JoinType.PUBLIC;
    }

    /**
     * 관리자에 의한 강제 가입인지 확인
     * @return 강제 가입 여부
     */
    public boolean isAdminForced() {
        return joinType == JoinType.ADMIN_FORCE;
    }

    /**
     * 가입 후 파티 멤버 수 반환
     * @return 멤버 수 (가입 후)
     */
    public int getMemberCountAfterJoin() {
        return party.getMembers().size() + 1;
    }

    /**
     * 파티 최대 인원 반환
     * @return 최대 인원
     */
    public int getMaxMembers() {
        return party.getMaxMembers();
    }

    /**
     * 가입 후 파티가 가득 차는지 확인
     * @return 풀파티 여부
     */
    public boolean willBeFull() {
        return getMemberCountAfterJoin() >= party.getMaxMembers();
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
        this. cancelReason = reason;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * 가입 방식 열거형
     */
    public enum JoinType {
        /** 초대를 통한 가입 */
        INVITE,
        /** 공개 파티 직접 참가 */
        PUBLIC,
        /** 관리자 강제 가입 */
        ADMIN_FORCE,
        /** 자동 매칭을 통한 가입 */
        AUTO_MATCH,
        /** 파티 생성 시 (리더) */
        CREATE
    }
}