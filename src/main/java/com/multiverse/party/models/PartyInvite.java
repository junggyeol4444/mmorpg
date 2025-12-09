package com.multiverse.party.models;

import java.util.UUID;

/**
 * 파티 초대 데이터
 */
public class PartyInvite {

    private UUID inviteId;
    private UUID partyId;
    private UUID inviterId;
    private UUID targetId;
    private long createdTime;   // 초대 생성 시각
    private long expireTime;    // 초대 만료 시각

    public UUID getInviteId() { return inviteId; }
    public void setInviteId(UUID inviteId) { this.inviteId = inviteId; }

    public UUID getPartyId() { return partyId; }
    public void setPartyId(UUID partyId) { this.partyId = partyId; }

    public UUID getInviterId() { return inviterId; }
    public void setInviterId(UUID inviterId) { this.inviterId = inviterId; }

    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }

    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }

    public long getExpireTime() { return expireTime; }
    public void setExpireTime(long expireTime) { this.expireTime = expireTime; }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expireTime;
    }

    public long getRemainingTime() {
        long rem = expireTime - System.currentTimeMillis();
        return Math.max(rem / 1000, 0);
    }
}