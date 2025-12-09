package com.multiverse.party.models;

import java.util.*;
import com.multiverse.party.models.enums.*;

public class Party {

    private UUID partyId;            // 파티 고유 ID
    private String partyName;        // 파티 이름
    private UUID leaderId;           // 리더 UUID
    private List<UUID> members;      // 멤버 UUID 리스트
    private Map<UUID, PartyRole> roles; // 멤버별 역할
    private int maxMembers;          // 최대 인원
    private PartyPrivacy privacy;    // 공개 설정
    private boolean allowInvites;    // 부리더 초대 권한
    private LootDistribution lootDistribution; // 아이템 분배 방식
    private ExpDistribution expDistribution;   // 경험치 분배 방식

    private long createdTime;         // 생성 시각
    private List<PartyBuff> activeBuffs; // 활성화된 버프
    private PartyLevel partyLevel;    // 파티 레벨
    private PartyStatistics statistics;// 파티 통계
    private PartyListing listing;      // 모집 공고 (nullable)
    private PartyQuest activeQuest;    // 진행중 퀘스트 (nullable)

    // Getters & Setters
    public UUID getPartyId() { return partyId; }
    public void setPartyId(UUID partyId) { this.partyId = partyId; }
    public String getPartyName() { return partyName; }
    public void setPartyName(String partyName) { this.partyName = partyName; }
    public UUID getLeaderId() { return leaderId; }
    public void setLeaderId(UUID leaderId) { this.leaderId = leaderId; }
    public List<UUID> getMembers() { return members == null ? (members = new ArrayList<>()) : members; }
    public void setMembers(List<UUID> members) { this.members = members; }
    public Map<UUID, PartyRole> getRoles() { return roles == null ? (roles = new HashMap<>()) : roles; }
    public void setRoles(Map<UUID, PartyRole> roles) { this.roles = roles; }
    public int getMaxMembers() { return maxMembers; }
    public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
    public PartyPrivacy getPrivacy() { return privacy; }
    public void setPrivacy(PartyPrivacy privacy) { this.privacy = privacy; }
    public boolean isAllowInvites() { return allowInvites; }
    public void setAllowInvites(boolean allowInvites) { this.allowInvites = allowInvites; }
    public LootDistribution getLootDistribution() { return lootDistribution; }
    public void setLootDistribution(LootDistribution lootDistribution) { this.lootDistribution = lootDistribution; }
    public ExpDistribution getExpDistribution() { return expDistribution; }
    public void setExpDistribution(ExpDistribution expDistribution) { this.expDistribution = expDistribution; }

    public long getCreatedTime() { return createdTime; }
    public void setCreatedTime(long createdTime) { this.createdTime = createdTime; }

    public List<PartyBuff> getActiveBuffs() { return activeBuffs == null ? (activeBuffs = new ArrayList<>()) : activeBuffs; }
    public void setActiveBuffs(List<PartyBuff> activeBuffs) { this.activeBuffs = activeBuffs; }

    public PartyLevel getPartyLevel() { return partyLevel; }
    public void setPartyLevel(PartyLevel partyLevel) { this.partyLevel = partyLevel; }

    public PartyStatistics getStatistics() { return statistics; }
    public void setStatistics(PartyStatistics statistics) { this.statistics = statistics; }

    public PartyListing getListing() { return listing; }
    public void setListing(PartyListing listing) { this.listing = listing; }

    public PartyQuest getActiveQuest() { return activeQuest; }
    public void setActiveQuest(PartyQuest activeQuest) { this.activeQuest = activeQuest; }
}