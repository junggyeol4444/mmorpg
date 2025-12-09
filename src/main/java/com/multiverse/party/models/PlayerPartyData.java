package com.multiverse.party.models;

import java.util.UUID;

/**
 * 플레이어 파티 관련 데이터 (파티ID, 이름, 통계 등)
 */
public class PlayerPartyData {

    private UUID playerUUID;
    private String playerName;
    private UUID currentParty;
    private int partiesJoined;
    private int partiesCreated;
    private int totalParties;
    private long lastPartyTime;
    private boolean autoDecline; // 파티 초대 자동거부 여부

    public UUID getPlayerUUID() { return playerUUID; }
    public void setPlayerUUID(UUID playerUUID) { this.playerUUID = playerUUID; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public UUID getCurrentParty() { return currentParty; }
    public void setCurrentParty(UUID currentParty) { this.currentParty = currentParty; }

    public int getPartiesJoined() { return partiesJoined; }
    public void setPartiesJoined(int partiesJoined) { this.partiesJoined = partiesJoined; }

    public int getPartiesCreated() { return partiesCreated; }
    public void setPartiesCreated(int partiesCreated) { this.partiesCreated = partiesCreated; }

    public int getTotalParties() { return totalParties; }
    public void setTotalParties(int totalParties) { this.totalParties = totalParties; }

    public long getLastPartyTime() { return lastPartyTime; }
    public void setLastPartyTime(long lastPartyTime) { this.lastPartyTime = lastPartyTime; }

    public boolean isAutoDecline() { return autoDecline; }
    public void setAutoDecline(boolean autoDecline) { this.autoDecline = autoDecline; }
}