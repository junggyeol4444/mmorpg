package com.multiverse.guild.model;

import java.util.UUID;

public class GuildMember {
    private UUID playerId;
    private String playerName;
    private String rankName;

    private long contribution;
    private long weeklyContribution;

    private long joinTime;
    private long lastOnline;

    public GuildMember(UUID playerId, String playerName, String rankName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.rankName = rankName;
        this.joinTime = System.currentTimeMillis();
        this.lastOnline = System.currentTimeMillis();
    }

    public UUID getPlayerId() { return playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getRankName() { return rankName; }
    public void setRankName(String rankName) { this.rankName = rankName; }
    public long getContribution() { return contribution; }
    public void setContribution(long contribution) { this.contribution = contribution; }
    public long getWeeklyContribution() { return weeklyContribution; }
    public void setWeeklyContribution(long weeklyContribution) { this.weeklyContribution = weeklyContribution; }
    public long getJoinTime() { return joinTime; }
    public void setJoinTime(long joinTime) { this.joinTime = joinTime; }
    public long getLastOnline() { return lastOnline; }
    public void setLastOnline(long lastOnline) { this.lastOnline = lastOnline; }
}