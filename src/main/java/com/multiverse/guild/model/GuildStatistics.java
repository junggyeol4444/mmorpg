package com.multiverse.guild.model;

public class GuildStatistics {
    private int totalWars;
    private int warsWon;
    private int warsLost;
    private int totalKills;
    private int totalDeaths;
    private long totalContribution;

    public int getTotalWars() { return totalWars; }
    public void setTotalWars(int totalWars) { this.totalWars = totalWars; }
    public int getWarsWon() { return warsWon; }
    public void setWarsWon(int warsWon) { this.warsWon = warsWon; }
    public int getWarsLost() { return warsLost; }
    public void setWarsLost(int warsLost) { this.warsLost = warsLost; }
    public int getTotalKills() { return totalKills; }
    public void setTotalKills(int totalKills) { this.totalKills = totalKills; }
    public int getTotalDeaths() { return totalDeaths; }
    public void setTotalDeaths(int totalDeaths) { this.totalDeaths = totalDeaths; }
    public long getTotalContribution() { return totalContribution; }
    public void setTotalContribution(long totalContribution) { this.totalContribution = totalContribution; }
}