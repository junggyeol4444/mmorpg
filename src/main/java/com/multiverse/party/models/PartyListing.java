package com.multiverse.party.models;

import com.multiverse.party.models.enums.PartyPurpose;

import java.util.UUID;

/**
 * 파티 모집 공고 데이터
 */
public class PartyListing {

    private UUID partyId;
    private String title;
    private String description;
    private long createdTime;
    private long expireTime;
    private int minLevel;
    private int maxLevel;
    private PartyPurpose purpose;

    public UUID getPartyId() { return partyId; }
    public void setPartyId(UUID partyId) { this.partyId = partyId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }

    public long getExpireTime() { return expireTime; }
    public void setExpireTime(long expireTime) { this.expireTime = expireTime; }

    public int getMinLevel() { return minLevel; }
    public void setMinLevel(int minLevel) { this.minLevel = minLevel; }

    public int getMaxLevel() { return maxLevel; }
    public void setMaxLevel(int maxLevel) { this.maxLevel = maxLevel; }

    public PartyPurpose getPurpose() { return purpose; }
    public void setPurpose(PartyPurpose purpose) { this.purpose = purpose; }
}