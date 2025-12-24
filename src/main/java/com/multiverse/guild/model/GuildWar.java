package com.multiverse.guild.model;

import java.util.Map;
import java.util.UUID;

public class GuildWar {
    private UUID warId;
    private UUID attackerGuildId;
    private UUID defenderGuildId;

    // 점수
    private int attackerScore;
    private int defenderScore;
    private int targetScore;

    // 시간
    private long startTime;
    private long endTime;
    private int duration;           // 초

    // 상태
    private WarStatus status;

    // 통계
    private Map<UUID, Integer> kills;
    private Map<UUID, Integer> deaths;

    public GuildWar(UUID warId, UUID attackerGuildId, UUID defenderGuildId, int attackerScore, int defenderScore, int targetScore, long startTime, long endTime, int duration, WarStatus status, Map<UUID, Integer> kills, Map<UUID, Integer> deaths) {
        this.warId = warId;
        this.attackerGuildId = attackerGuildId;
        this.defenderGuildId = defenderGuildId;
        this.attackerScore = attackerScore;
        this.defenderScore = defenderScore;
        this.targetScore = targetScore;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.status = status;
        this.kills = kills;
        this.deaths = deaths;
    }

    public UUID getWarId() { return warId; }
    public UUID getAttackerGuildId() { return attackerGuildId; }
    public UUID getDefenderGuildId() { return defenderGuildId; }
    public int getAttackerScore() { return attackerScore; }
    public void setAttackerScore(int attackerScore) { this.attackerScore = attackerScore; }
    public int getDefenderScore() { return defenderScore; }
    public void setDefenderScore(int defenderScore) { this.defenderScore = defenderScore; }
    public int getTargetScore() { return targetScore; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public int getDuration() { return duration; }
    public WarStatus getStatus() { return status; }
    public void setStatus(WarStatus status) { this.status = status; }
    public Map<UUID, Integer> getKills() { return kills; }
    public Map<UUID, Integer> getDeaths() { return deaths; }
}